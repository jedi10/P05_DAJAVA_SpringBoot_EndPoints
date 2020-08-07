package com.safetynet.alerts.dao;

import java.io.IOException;

public interface IRootFile {
    void setBytesWithPath(boolean forceSetting) throws IOException;

    String getPath();

    byte[] getBytes();

    void setPath(String path);

    void setBytes(byte[] bytes);
}
