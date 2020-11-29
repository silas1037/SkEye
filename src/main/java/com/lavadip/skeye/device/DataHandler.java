package com.lavadip.skeye.device;

import com.lavadip.skeye.Vector3d;

public interface DataHandler {
    void handleData(Vector3d vector3d);

    boolean hasStopped();
}
