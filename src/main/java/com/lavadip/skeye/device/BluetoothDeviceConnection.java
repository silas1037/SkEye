package com.lavadip.skeye.device;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import java.util.UUID;

/* access modifiers changed from: package-private */
@TargetApi(18)
public class BluetoothDeviceConnection implements DeviceConnection {
    private static final UUID descrUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final BluetoothGattCharacteristic chr;
    private final BluetoothGatt gatt;

    public BluetoothDeviceConnection(BluetoothGatt gatt2, BluetoothGattCharacteristic chr2) {
        this.gatt = gatt2;
        this.chr = chr2;
    }

    @Override // com.lavadip.skeye.device.DeviceConnection
    public void startListening() {
        this.gatt.setCharacteristicNotification(this.chr, true);
        changeNotification(true);
    }

    @Override // com.lavadip.skeye.device.DeviceConnection
    public void disconnect() {
        this.gatt.close();
    }

    @Override // com.lavadip.skeye.device.DeviceConnection
    public void stopListening() {
        this.gatt.setCharacteristicNotification(this.chr, false);
        changeNotification(false);
    }

    private void changeNotification(boolean enable) {
        BluetoothGattDescriptor descr = this.chr.getDescriptor(descrUUID);
        if (descr != null) {
            System.out.println("    Descr: " + descr.getUuid());
            descr.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            this.gatt.writeDescriptor(descr);
            return;
        }
        System.out.println("Can't find descriptor");
    }
}
