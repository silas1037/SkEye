package com.lavadip.skeye.device;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.OrientationManager;
import java.util.ArrayList;
import java.util.List;

@TargetApi(18)
public final class DeviceManager {
    final Activity context;
    final Handler handler;
    private final LocalAndroidDevice localAndroidDevice;

    /* renamed from: om */
    private final OrientationManager f87om;
    private Device selectedDevice = this.localAndroidDevice;

    public DeviceManager(OrientationManager om, Activity context2, SensorManager sm) {
        this.f87om = om;
        this.handler = new Handler(context2.getMainLooper());
        this.context = context2;
        this.localAndroidDevice = new LocalAndroidDevice(this, sm);
        om.setDeviceConnection(this.localAndroidDevice);
    }

    public void showManagerDialog() {
        if (Build.VERSION.SDK_INT >= 18) {
            showManagerDialogImpl();
        } else {
            CustomDialog.createMessageDialog(this.context, C0031R.string.device_manager, C0031R.string.bluetooth_le_not_supported).show();
        }
    }

    private void showManagerDialogImpl() {
        final CustomDialog dialog = new CustomDialog.Builder(this.context).setTitle(C0031R.string.device_manager).setNegativeButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.device.DeviceManager.DialogInterface$OnClickListenerC01221 */

            public void onClick(DialogInterface d, int arg1) {
                d.cancel();
            }
        }).create();
        BluetoothAdapter btAdapter = ((BluetoothManager) this.context.getSystemService("bluetooth")).getAdapter();
        if (btAdapter != null) {
            final List<BluetoothDevice> btDevices = new ArrayList<>(btAdapter.getBondedDevices());
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.device.action.FOUND");
            btAdapter.startDiscovery();
            final List<Device> allDevices = new ArrayList<>();
            final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                /* class com.lavadip.skeye.device.DeviceManager.C01232 */

                public void onReceive(Context ctx, Intent intent) {
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    if ((device.getType() & 2) != 0) {
                        System.out.println("Found BLE device " + device.getName());
                        for (BluetoothDevice bonded : btDevices) {
                            if (bonded.equals(device)) {
                                return;
                            }
                        }
                        allDevices.add(new RemoteBluetoothDevice(device, DeviceManager.this.context, DeviceManager.this));
                        DeviceManager.this.buildDialogList(dialog, allDevices);
                    }
                }
            };
            this.context.registerReceiver(broadcastReceiver, intentFilter);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.lavadip.skeye.device.DeviceManager.DialogInterface$OnDismissListenerC01243 */

                public void onDismiss(DialogInterface dialog) {
                    DeviceManager.this.context.unregisterReceiver(broadcastReceiver);
                }
            });
            allDevices.add(this.localAndroidDevice);
            for (BluetoothDevice btDevice : btDevices) {
                if ((btDevice.getType() & 2) != 0) {
                    RemoteBluetoothDevice remoteBluetoothDevice = new RemoteBluetoothDevice(btDevice, this.context, this);
                    if (this.selectedDevice == null || !remoteBluetoothDevice.getId().equals(this.selectedDevice.getId())) {
                        allDevices.add(remoteBluetoothDevice);
                    } else {
                        allDevices.add(this.selectedDevice);
                    }
                }
            }
            buildDialogList(dialog, allDevices);
        } else {
            TextView message = new TextView(this.context);
            message.setText(C0031R.string.bluetooth_interface_not_found);
            dialog.replaceContent(message);
        }
        dialog.show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void buildDialogList(final CustomDialog dialog, final List<Device> allDevices) {
        int selectedIndex = 0;
        for (int i = 0; i < allDevices.size(); i++) {
            Device d = allDevices.get(i);
            if (this.selectedDevice != null && d.getId().equals(this.selectedDevice.getId())) {
                selectedIndex = i;
            }
        }
        if (allDevices.size() > 0) {
            CharSequence[] deviceNames = new String[allDevices.size()];
            for (int i2 = 0; i2 < allDevices.size(); i2++) {
                deviceNames[i2] = allDevices.get(i2).getName();
            }
            ListView lv = new ListView(this.context);
            lv.setChoiceMode(1);
            lv.setAdapter((ListAdapter) new ArrayAdapter<>(this.context, C0031R.layout.select_dialog_singlechoice, 16908308, deviceNames));
            lv.setItemChecked(selectedIndex, true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /* class com.lavadip.skeye.device.DeviceManager.C01254 */

                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    if (pos >= 0) {
                        dialog.dismiss();
                        Device newDevice = (Device) allDevices.get(pos);
                        if (!newDevice.getId().equals(DeviceManager.this.selectedDevice.getId())) {
                            DeviceManager.this.selectedDevice.deselect();
                        }
                        DeviceManager.this.selectedDevice = newDevice;
                        DeviceManager.this.selectedDevice.select();
                    }
                }
            });
            dialog.replaceContent(lv);
        }
    }

    private CustomDialog buildDialog(CustomDialog.Builder dialogBuilder, final List<Device> allDevices, int selectedIndex) {
        if (allDevices.size() > 0) {
            CharSequence[] deviceNames = new String[allDevices.size()];
            for (int i = 0; i < allDevices.size(); i++) {
                deviceNames[i] = allDevices.get(i).getName();
            }
            dialogBuilder.setSingleChoiceItems(deviceNames, selectedIndex, new DialogInterface.OnClickListener() {
                /* class com.lavadip.skeye.device.DeviceManager.DialogInterface$OnClickListenerC01265 */

                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        dialog.dismiss();
                        Device newDevice = (Device) allDevices.get(which);
                        if (!newDevice.getId().equals(DeviceManager.this.selectedDevice.getId())) {
                            DeviceManager.this.selectedDevice.deselect();
                        }
                        DeviceManager.this.selectedDevice = newDevice;
                        DeviceManager.this.selectedDevice.select();
                    }
                }
            });
            return dialogBuilder.create();
        }
        TextView message = new TextView(this.context);
        message.setText(C0031R.string.bluetooth_le_device_not_found);
        dialogBuilder.setContentView(message);
        return dialogBuilder.create();
    }

    /* access modifiers changed from: package-private */
    public void updateStatus(final String status) {
        System.out.println("device status: " + status);
        this.handler.post(new Runnable() {
            /* class com.lavadip.skeye.device.DeviceManager.RunnableC01276 */

            public void run() {
                Toast.makeText(DeviceManager.this.context, status, 0).show();
            }
        });
    }

    public float getMagneticTolerance() {
        return 9.0f;
    }

    public float getMagneticToleranceExtreme() {
        return 18.0f;
    }

    public void connected() {
        this.f87om.setDeviceConnection(this.selectedDevice);
    }

    public int getScreenOrientation(int currOrientInt) {
        return this.localAndroidDevice.isAutoPossible() ? this.localAndroidDevice.getScreenOrientation() : currOrientInt;
    }

    public void setPrefs(String acclSensitivityPref, String magSensitivityPref, String sensorFusionSensitivityPref, Boolean useSensorFusionPref) {
        this.localAndroidDevice.setPrefs(acclSensitivityPref, magSensitivityPref, sensorFusionSensitivityPref, useSensorFusionPref.booleanValue());
    }

    public static boolean sensorFusionAvailable(SensorManager sm) {
        return LocalAndroidDevice.sensorFusionAvailable(sm);
    }

    public void stopLocalListening() {
        this.localAndroidDevice.disconnectFully();
    }

    public void onOrientationChanged(final boolean isRemote) {
        this.handler.post(new Runnable() {
            /* class com.lavadip.skeye.device.DeviceManager.RunnableC01287 */

            public void run() {
                DeviceManager.this.f87om.onOrientationChanged(isRemote);
            }
        });
    }
}
