package com.lavadip.skeye.device;

public interface DeviceConnection {
    void disconnect();

    void startListening();

    void stopListening();
}
