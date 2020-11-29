package com.lavadip.skeye.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.Vector3d;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/* access modifiers changed from: package-private */
public class RemoteBluetoothDevice extends DeviceBase {
    private static final UUID BTLE_CHARACTERISTIC_FIRMWARE_REVISION = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    private static final UUID BTLE_CHARACTERISTIC_MODEL_NUMBER = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    private static final UUID BTLE_SERVICE_DEVICE_INFO = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    private static final int BYTE_MASK = 255;
    protected static final long HUNDRED_DAYS_IN_MILLIS = 8640000000L;
    private static final UUID SENSORTAG_MOVEMENT_CONFIG_UUID = UUID.fromString("F000AA82-0451-4000-B000-000000000000");
    private static final UUID SENSORTAG_MOVEMENT_NOTIFIC_UUID = UUID.fromString("F000AA81-0451-4000-B000-000000000000");
    private static final UUID SENSORTAG_MOVEMENT_PERIOD_UUID = UUID.fromString("F000AA83-0451-4000-B000-000000000000");
    private static final UUID SENSORTAG_MOVEMENT_SERVICE_UUID = UUID.fromString("F000AA80-0451-4000-B000-000000000000");
    private static final UUID WICED_SECURE_UPGRADE_APP_INFO_UUID = UUID.fromString("6AA5711B-0376-44F1-BCA1-8647B48BDB55");
    private static final UUID WICED_SECURE_UPGRADE_SERVICE_UUID = UUID.fromString("A86ABC2D-D44C-442E-99F7-80059A873E36");
    private static final UUID WICED_SENSOR_NOTIFICATION_UUID = UUID.fromString("33ef9113-3b55-413e-b553-fea1eaada459");
    private static final UUID WICED_SENSOR_SERVICE_UUID = UUID.fromString("739298B6-87B6-4984-A5DC-BDC18B068985");
    private SensorCalibrator accCalibrator;
    private final BluetoothDevice btDevice;
    private String calibMsg1 = "";
    private String calibMsg2 = "";
    private final Activity context;
    private BluetoothDeviceConnection deviceConnection;

    /* renamed from: dm */
    private final DeviceManager f91dm;
    private final ExpoSmoother expSmootherAcc = new ExpoSmoother(Sensitivity.High.accAlpha, Sensitivity.High.accExp, Sensitivity.High.accDAlpha, 3);
    private final ExpoSmoother expSmootherMag = new ExpoSmoother(Sensitivity.High.magAlpha, Sensitivity.High.magExp, Sensitivity.High.magDAlpha, 3);
    final BluetoothGattCallback gattHandler = new BluetoothGattCallback() {
        /* class com.lavadip.skeye.device.RemoteBluetoothDevice.C01302 */
        VendorHandler vendorHandler = null;

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println("Connection status changed: " + status + ", " + newState);
            if (status != 0) {
                RemoteBluetoothDevice.this.updateStatus("Gatt connection failed. Retrying again.");
                gatt.connect();
            } else if (newState == 2) {
                RemoteBluetoothDevice.this.state = DeviceState.CONNECTED;
                RemoteBluetoothDevice.this.updateStatus("device connected.\nQuerying services");
                gatt.discoverServices();
            } else if (newState == 0 && this.vendorHandler != null) {
                RemoteBluetoothDevice.this.updateStatus("Device '" + gatt.getDevice().getName() + "' disconnected.\nTrying to connect again.");
                gatt.connect();
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != 0) {
                gatt.disconnect();
                RemoteBluetoothDevice.this.updateStatus("Failed to discover services");
                return;
            }
            RemoteBluetoothDevice.this.updateStatus("Services discovered");
            String name = gatt.getDevice().getName();
            if (name.contains("WICED")) {
                this.vendorHandler = new WicedHandler(RemoteBluetoothDevice.this, null);
            } else if (name.contains("SensorTag")) {
                this.vendorHandler = new SensorTagHandler(RemoteBluetoothDevice.this, null);
            }
            if (this.vendorHandler != null) {
                this.vendorHandler.servicesDiscovered(gatt);
            } else {
                CustomDialog.createMessageDialog(RemoteBluetoothDevice.this.context, "Connecting to device", "Device not recognized.\n\nCurrently only WICED Sense is supported.\n\nSensor Tag and other devices will be supported soon.").show();
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status != 0) {
                RemoteBluetoothDevice.this.updateStatus("Error reading from characteristic");
                System.out.println("Error reading from chr:" + characteristic.getUuid());
            } else if (this.vendorHandler != null) {
                this.vendorHandler.characteristicRead(gatt, characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (this.vendorHandler != null) {
                this.vendorHandler.onCharacteristicChanged(gatt, characteristic);
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            System.out.println("Characteristic written: " + characteristic);
            if (this.vendorHandler != null) {
                this.vendorHandler.onCharacteristicWrite(gatt, characteristic, status);
            }
        }
    };
    private SensorCalibrator magCalibrator;
    final BlockingQueue<double[]> processingQueue = new ArrayBlockingQueue(40);
    private Thread processingThread;
    private final Runnable processor = new Runnable() {
        /* class com.lavadip.skeye.device.RemoteBluetoothDevice.RunnableC01291 */

        public void run() {
            while (true) {
                try {
                    double[] data = RemoteBluetoothDevice.this.processingQueue.take();
                    if (data.length != 0) {
                        RemoteBluetoothDevice.this.processMovementData(data);
                    } else {
                        return;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Processing thread interrupted");
                    return;
                }
            }
        }
    };
    private boolean sensorsFound = false;
    private DeviceState state = DeviceState.UNSELECTED;
    private String titleMsg = "";
    private Toast toast = null;

    /* access modifiers changed from: private */
    public enum DeviceState {
        UNSELECTED,
        SELECTED,
        CONNECTING,
        CONNECTED,
        LISTENING
    }

    private interface VendorHandler {
        void characteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

        void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic);

        void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i);

        void servicesDiscovered(BluetoothGatt bluetoothGatt);
    }

    private final class WicedHandler implements VendorHandler {
        private static final float magScale = 0.032f;
        private float accScale;

        private WicedHandler() {
            this.accScale = 0.1f;
        }

        /* synthetic */ WicedHandler(RemoteBluetoothDevice remoteBluetoothDevice, WicedHandler wicedHandler) {
            this();
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void servicesDiscovered(BluetoothGatt gatt) {
            findFirmwareVersion(gatt);
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (RemoteBluetoothDevice.WICED_SENSOR_NOTIFICATION_UUID.equals(characteristic.getUuid())) {
                updateData(characteristic.getValue());
            } else {
                System.out.println("Char changed: " + characteristic.getUuid());
            }
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void characteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (RemoteBluetoothDevice.WICED_SECURE_UPGRADE_APP_INFO_UUID.equals(characteristic.getUuid())) {
                byte[] value = characteristic.getValue();
                RemoteBluetoothDevice.this.titleMsg = "Wiced Firmware version: " + ((int) value[2]) + "." + ((int) value[3]) + "\n";
                if (value[2] == 4) {
                    System.out.println("Switching to better firmware!");
                    this.accScale = 0.00625f;
                } else if (value[2] == 5) {
                    System.out.println("Switching to the best firmware 5.x!");
                    this.accScale = 1.25E-4f;
                }
                RemoteBluetoothDevice.this.showToast();
                prepareToReadFromSensors(gatt);
                return;
            }
            System.out.println("Chr read: " + characteristic.getUuid() + "  : " + Arrays.toString(characteristic.getValue()));
        }

        private void findFirmwareVersion(BluetoothGatt gatt) {
            BluetoothGattService service = gatt.getService(RemoteBluetoothDevice.WICED_SECURE_UPGRADE_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic chr = service.getCharacteristic(RemoteBluetoothDevice.WICED_SECURE_UPGRADE_APP_INFO_UUID);
                if (chr == null) {
                    RemoteBluetoothDevice.this.updateStatus("Firmware Info characteristic not found");
                } else if (!gatt.readCharacteristic(chr)) {
                    RemoteBluetoothDevice.this.updateStatus("Couldn't read app info");
                }
            } else {
                RemoteBluetoothDevice.this.updateStatus("Firmware Upgrade service not found");
                prepareToReadFromSensors(gatt);
            }
        }

        private void prepareToReadFromSensors(BluetoothGatt gatt) {
            BluetoothGattService service = gatt.getService(RemoteBluetoothDevice.WICED_SENSOR_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic chr = service.getCharacteristic(RemoteBluetoothDevice.WICED_SENSOR_NOTIFICATION_UUID);
                if (chr != null) {
                    RemoteBluetoothDevice.this.updateStatus("Connected to Wiced Sense");
                    RemoteBluetoothDevice.this.sensorsFound = true;
                    RemoteBluetoothDevice.this.deviceConnection = new BluetoothDeviceConnection(gatt, chr);
                    RemoteBluetoothDevice.this.f91dm.connected();
                    return;
                }
                RemoteBluetoothDevice.this.updateStatus("Sensor characteristic not found");
                return;
            }
            RemoteBluetoothDevice.this.updateStatus("Sensor service not found");
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        /* access modifiers changed from: package-private */
        public void updateData(byte[] data) {
            byte firstByte = data[0];
            if (firstByte == 52) {
                return;
            }
            if ((firstByte & 11) != 0) {
                int offset = 0;
                boolean accPresent = false;
                double accX = 0.0d;
                double accY = 0.0d;
                double accZ = 0.0d;
                if ((firstByte & 1) != 0) {
                    int accOrigX = RemoteBluetoothDevice.bytes2SignedInt(data, 1);
                    int accOrigY = RemoteBluetoothDevice.bytes2SignedInt(data, 3);
                    int accOrigZ = RemoteBluetoothDevice.bytes2SignedInt(data, 5);
                    float accScaleLocal = this.accScale;
                    accX = (double) (((float) accOrigY) * accScaleLocal);
                    accY = (double) (((float) (-accOrigX)) * accScaleLocal);
                    accZ = (double) (((float) accOrigZ) * accScaleLocal);
                    accPresent = true;
                    offset = 0 + 6;
                }
                if ((firstByte & 2) != 0) {
                    offset += 6;
                }
                if (accPresent && (firstByte & 8) != 0) {
                    double magX = (double) (((float) RemoteBluetoothDevice.bytes2SignedInt(data, offset + 1)) * magScale);
                    double magY = (double) (((float) RemoteBluetoothDevice.bytes2SignedInt(data, offset + 3)) * magScale);
                    RemoteBluetoothDevice.this.processingQueue.offer(new double[]{accX, accY, accZ, magX, magY, (double) (((float) RemoteBluetoothDevice.bytes2SignedInt(data, offset + 5)) * magScale)});
                    return;
                }
                return;
            }
            System.out.println("Unhandled data of type: " + ((int) firstByte));
        }
    }

    private final class SensorTagHandler implements VendorHandler {
        private static final double accScale = 4.8828125E-4d;
        private static final double magScale = 0.14990234375d;
        private volatile boolean firstData;

        private SensorTagHandler() {
            this.firstData = true;
        }

        /* synthetic */ SensorTagHandler(RemoteBluetoothDevice remoteBluetoothDevice, SensorTagHandler sensorTagHandler) {
            this();
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void servicesDiscovered(BluetoothGatt gatt) {
            gatt.readCharacteristic(gatt.getService(RemoteBluetoothDevice.BTLE_SERVICE_DEVICE_INFO).getCharacteristic(RemoteBluetoothDevice.BTLE_CHARACTERISTIC_FIRMWARE_REVISION));
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (RemoteBluetoothDevice.SENSORTAG_MOVEMENT_NOTIFIC_UUID.equals(characteristic.getUuid())) {
                updateData(characteristic.getValue());
            } else {
                System.out.println("Char changed: " + characteristic.getUuid());
            }
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void characteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (RemoteBluetoothDevice.BTLE_CHARACTERISTIC_FIRMWARE_REVISION.equals(characteristic.getUuid())) {
                RemoteBluetoothDevice.this.titleMsg = "Sensor Tag firmware version: " + characteristic.getStringValue(0);
                RemoteBluetoothDevice.this.showToast();
                System.out.println(RemoteBluetoothDevice.this.titleMsg);
                prepareToReadFromSensors(gatt);
                return;
            }
            System.out.println("Chr read: " + characteristic.getUuid() + "  : " + Arrays.toString(characteristic.getValue()));
        }

        private void prepareToReadFromSensors(BluetoothGatt gatt) {
            BluetoothGattService service = gatt.getService(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic chrConfig = service.getCharacteristic(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_CONFIG_UUID);
                byte[] b = new byte[2];
                b[0] = Byte.MAX_VALUE;
                chrConfig.setValue(b);
                gatt.writeCharacteristic(chrConfig);
                return;
            }
            RemoteBluetoothDevice.this.updateStatus("Sensor service not found");
        }

        @Override // com.lavadip.skeye.device.RemoteBluetoothDevice.VendorHandler
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BluetoothGattService service = gatt.getService(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_SERVICE_UUID);
            if (characteristic.getUuid().equals(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_CONFIG_UUID)) {
                System.out.println("Wrote config: " + status);
                BluetoothGattCharacteristic chrPeriod = service.getCharacteristic(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_PERIOD_UUID);
                chrPeriod.setValue(new byte[]{10});
                gatt.writeCharacteristic(chrPeriod);
            } else if (characteristic.getUuid().equals(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_PERIOD_UUID)) {
                System.out.println("Wrote period, status: " + status);
                BluetoothGattCharacteristic chr = service.getCharacteristic(RemoteBluetoothDevice.SENSORTAG_MOVEMENT_NOTIFIC_UUID);
                RemoteBluetoothDevice.this.updateStatus("Connected to SensorTag");
                this.firstData = true;
                RemoteBluetoothDevice.this.sensorsFound = true;
                RemoteBluetoothDevice.this.deviceConnection = new BluetoothDeviceConnection(gatt, chr);
                RemoteBluetoothDevice.this.f91dm.connected();
            }
        }

        /* access modifiers changed from: package-private */
        public void updateData(byte[] data) {
            if (data.length < 18) {
                System.out.println("Unhandled data of length: " + data.length);
            } else if (this.firstData) {
                this.firstData = false;
            } else {
                int accOrigX = parseInt(data, 6);
                int accOrigY = parseInt(data, 8);
                int accOrigZ = parseInt(data, 10);
                double accX = ((double) (-accOrigX)) * accScale;
                double accY = ((double) accOrigY) * accScale;
                double accZ = ((double) (-accOrigZ)) * accScale;
                double magY = ((double) parseInt(data, 12)) * magScale;
                double magX = ((double) (-parseInt(data, 14))) * magScale;
                double magZ = ((double) parseInt(data, 16)) * magScale;
                RemoteBluetoothDevice.this.processingQueue.offer(new double[]{accX, accY, accZ, magX, magY, magZ});
            }
        }

        private int parseInt(byte[] data, int offset) {
            return (data[offset + 1] << 8) + (data[offset] & 255);
        }
    }

    public RemoteBluetoothDevice(BluetoothDevice btDevice2, Activity context2, DeviceManager dm) {
        this.btDevice = btDevice2;
        this.context = context2;
        this.f91dm = dm;
    }

    @Override // com.lavadip.skeye.device.Device
    public int getSuggestedSampleWindow() {
        return 600;
    }

    @Override // com.lavadip.skeye.device.Device
    public double getFitnessTolerance() {
        return 0.02d;
    }

    @Override // com.lavadip.skeye.device.Device
    public Sensitivity getSensitivity() {
        return Sensitivity.High;
    }

    @Override // com.lavadip.skeye.device.Device
    public boolean isAutoPossible() {
        return true;
    }

    @Override // com.lavadip.skeye.device.Device
    public String getSensorPresentMessage(Context c) {
        String presentStr = c.getString(C0031R.string.present);
        String absentStr = c.getString(C0031R.string.absent);
        String acclStr = String.valueOf(c.getString(C0031R.string.accelerometer)) + ": " + presentStr;
        return String.valueOf(acclStr) + "\n" + (String.valueOf(c.getString(C0031R.string.magnetometer)) + ": " + presentStr) + "\n" + (String.valueOf(c.getString(C0031R.string.gyroscope)) + ": " + presentStr) + "\n" + (String.valueOf(c.getString(C0031R.string.sensor_fusion)) + ": " + absentStr);
    }

    @Override // com.lavadip.skeye.device.Device
    public String getName() {
        return this.btDevice.getName();
    }

    @Override // com.lavadip.skeye.device.Device
    public String getId() {
        return "BT," + this.btDevice.getAddress();
    }

    public int getMagneticToleranceExtreme() {
        return 22;
    }

    public int getMagneticTolerance() {
        return 22;
    }

    @Override // com.lavadip.skeye.device.Device
    public synchronized void select() {
        if (this.state == DeviceState.UNSELECTED) {
            this.state = DeviceState.SELECTED;
            new CustomDialog.Builder(this.context).setSingleChoiceItems(new String[]{"One minute", "Two minutes", "Three minutes", "Four minutes"}, 0, new DialogInterface.OnClickListener() {
                /* class com.lavadip.skeye.device.RemoteBluetoothDevice.DialogInterface$OnClickListenerC01313 */

                public void onClick(DialogInterface dialog, int which) {
                    RemoteBluetoothDevice.this.state = DeviceState.CONNECTING;
                    dialog.dismiss();
                    System.out.println("Connecting to gatt " + RemoteBluetoothDevice.this.btDevice.getAddress());
                    RemoteBluetoothDevice.this.btDevice.connectGatt(RemoteBluetoothDevice.this.context.getApplicationContext(), false, RemoteBluetoothDevice.this.gattHandler);
                    RemoteBluetoothDevice.this.magCalibrator = new SensorCalibrator("Magnetometer", "Check for magnetic interference.", RemoteBluetoothDevice.this, 0);
                    RemoteBluetoothDevice.this.magCalibrator.setup(which + 1);
                    RemoteBluetoothDevice.this.accCalibrator = new SensorCalibrator("Accelerometer", "Spin gently", RemoteBluetoothDevice.this, 1);
                    if (!RemoteBluetoothDevice.this.accCalibrator.initFromString(RemoteBluetoothDevice.this.context.getSharedPreferences("calibrations", 0).getString(RemoteBluetoothDevice.this.getCalibKey(), null), RemoteBluetoothDevice.HUNDRED_DAYS_IN_MILLIS)) {
                        RemoteBluetoothDevice.this.accCalibrator.setup(which + 1);
                    }
                }
            }).setTitle("Approximate duration of calibration").create().show();
            this.processingThread = new Thread(this.processor);
            this.processingThread.start();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getCalibKey() {
        return "accCalib" + this.btDevice.getAddress();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateStatus(String msg) {
        this.f91dm.updateStatus(msg);
    }

    @Override // com.lavadip.skeye.device.Device
    public synchronized void deselect() {
        if (this.deviceConnection != null) {
            this.deviceConnection.disconnect();
            this.deviceConnection = null;
            this.processingQueue.clear();
            this.processingQueue.offer(new double[0]);
        }
        this.state = DeviceState.UNSELECTED;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void processMovementData(double[] data) {
        Vector3d usedMagVec;
        Vector3d usedAccVec;
        Vector3d accVec = new Vector3d(data[0], data[1], data[2]);
        Vector3d magVec = new Vector3d(data[3], data[4], data[5]);
        if (this.magCalibrator.fitness < 50.0d) {
            this.magCalibrator.handleData(magVec);
        }
        if (this.accCalibrator.fitness < 50.0d) {
            this.accCalibrator.handleData(accVec);
        }
        if (this.accCalibrator.isFresh) {
            this.context.getSharedPreferences("calibrations", 0).edit().putString(getCalibKey(), this.accCalibrator.serializeToString()).apply();
            this.accCalibrator.isFresh = false;
        }
        if (this.magCalibrator.fitness > 50.0d) {
            usedMagVec = this.magCalibrator.correct(magVec);
        } else {
            usedMagVec = magVec;
        }
        if (this.accCalibrator.fitness > 50.0d) {
            usedAccVec = this.accCalibrator.correct(accVec);
        } else {
            usedAccVec = accVec;
        }
        sendData(usedAccVec, usedMagVec);
    }

    private void sendData(Vector3d accVec, Vector3d magVec) {
        this.expSmootherAcc.update(this.bufferedAcceleration, new float[]{(float) accVec.f16x, (float) accVec.f17y, (float) accVec.f18z});
        this.expSmootherMag.update(this.bufferedMagneticField, new float[]{(float) magVec.f16x, (float) magVec.f17y, (float) magVec.f18z});
        float[] buff = this.bufferedMagneticField;
        this.fieldStrength = (float) Math.sqrt((double) ((buff[0] * buff[0]) + (buff[1] * buff[1]) + (buff[2] * buff[2])));
        this.f91dm.onOrientationChanged(true);
    }

    /* access modifiers changed from: private */
    public static int bytes2SignedInt(byte[] data, int i) {
        int unsignedInt = bytes2UnsignedInt(data, i);
        if ((32768 & unsignedInt) != 0) {
            return unsignedInt | -65536;
        }
        return unsignedInt;
    }

    private static int bytes2UnsignedInt(byte[] data, int i) {
        return ((data[i + 1] & 255) << 8) | (data[i] & 255);
    }

    @Override // com.lavadip.skeye.device.Device
    public synchronized void startListening() {
        this.deviceConnection.startListening();
    }

    @Override // com.lavadip.skeye.device.Device
    public synchronized void stopListening() {
        if (this.state == DeviceState.LISTENING && this.deviceConnection != null) {
            this.deviceConnection.stopListening();
            this.state = DeviceState.CONNECTED;
        }
    }

    public void setStatus(int calibIndex, String msg) {
        if (calibIndex == 0) {
            this.calibMsg1 = msg;
        } else {
            this.calibMsg2 = msg;
        }
        showToast();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showToast() {
        if (this.sensorsFound) {
            this.f91dm.handler.post(new Runnable() {
                /* class com.lavadip.skeye.device.RemoteBluetoothDevice.RunnableC01324 */

                public void run() {
                    if (RemoteBluetoothDevice.this.toast == null) {
                        RemoteBluetoothDevice.this.toast = Toast.makeText(RemoteBluetoothDevice.this.context, "", 0);
                    }
                    RemoteBluetoothDevice.this.toast.setText(String.valueOf(RemoteBluetoothDevice.this.titleMsg) + RemoteBluetoothDevice.this.calibMsg1 + "\n" + RemoteBluetoothDevice.this.calibMsg2);
                    RemoteBluetoothDevice.this.toast.show();
                }
            });
        }
    }
}
