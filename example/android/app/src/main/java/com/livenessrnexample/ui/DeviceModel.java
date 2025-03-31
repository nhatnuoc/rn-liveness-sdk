package com.livenessrnexample.ui;

public class DeviceModel {
    private String name;
    private String info;

    public DeviceModel() {
    }

    public DeviceModel(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
