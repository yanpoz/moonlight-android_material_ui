package com.limelight.nvstream.http;

import com.limelight.LimeLog;

public class NvApp {
    private String appName = "";
    private int appId;
    private boolean initialized;
    private boolean hdrSupported;
    private int position = 0;
    
    public NvApp() {}
    
    public NvApp(String appName) {
        this.appName = appName;
    }
    
    public NvApp(String appName, int appId, boolean hdrSupported) {
        this.appName = appName;
        this.appId = appId;
        this.hdrSupported = hdrSupported;
        this.initialized = true;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public void setAppId(String appId) {
        try {
            this.appId = Integer.parseInt(appId);
            this.initialized = true;
        } catch (NumberFormatException e) {
            LimeLog.warning("Malformed app ID: "+appId);
        }
    }
    
    public void setAppId(int appId) {
        this.appId = appId;
        this.initialized = true;
    }

    public void setHdrSupported(boolean hdrSupported) {
        this.hdrSupported = hdrSupported;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public int getAppId() {
        return this.appId;
    }

    public boolean isHdrSupported() {
        return this.hdrSupported;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Name: ").append(appName).append("\n");
        str.append("HDR Supported: ").append(hdrSupported ? "Yes" : "Unknown").append("\n");
        str.append("ID: ").append(appId).append("\n");
        str.append("Position: ").append(position).append("\n");
        return str.toString();
    }
}
