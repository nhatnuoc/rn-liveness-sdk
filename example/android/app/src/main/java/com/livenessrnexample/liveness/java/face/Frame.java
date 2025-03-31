package com.livenessrnexample.liveness.java.face;
import android.util.Size;
/**
 * Created by Thuytv on 30/05/2024.
 */
public class Frame {
    private byte[] data;
    private int rotation;
    private Size size;
    private int format;
    private LensFacing lensFacing;

    public Frame(byte[] data, int rotation, Size size, int format, LensFacing lensFacing) {
        this.data = data;
        this.rotation = rotation;
        this.size = size;
        this.format = format;
        this.lensFacing = lensFacing;
    }

    public byte[] getData() {
        return data;
    }

    public int getRotation() {
        return rotation;
    }

    public Size getSize() {
        return size;
    }

    public int getFormat() {
        return format;
    }

    public LensFacing getLensFacing() {
        return lensFacing;
    }
}
