package com.livenessrnexample.liveness.java.face;

import android.graphics.RectF;

public class FaceBounds {
    private Integer id;
    private RectF box;

    public FaceBounds(Integer id, RectF box) {
        this.id = id;
        this.box = box;
    }

    public Integer getId() {
        return id;
    }

    public RectF getBox() {
        return box;
    }
}
