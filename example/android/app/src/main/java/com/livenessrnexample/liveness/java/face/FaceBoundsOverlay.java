package com.livenessrnexample.liveness.java.face;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class FaceBoundsOverlay extends View {

    private List<FaceBounds> facesBounds = new ArrayList<>();
    private Paint anchorPaint = new Paint();
    private Paint idPaint = new Paint();
    private Paint boundsPaint = new Paint();

    public FaceBoundsOverlay(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        anchorPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));

        idPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
        idPaint.setTextSize(40f);

        boundsPaint.setStyle(Paint.Style.STROKE);
        boundsPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
        boundsPaint.setStrokeWidth(4f);
    }

    void updateFaces(List<FaceBounds> bounds) {
        if (bounds != null) {
            facesBounds.clear();
            facesBounds.addAll(bounds);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (FaceBounds faceBounds : facesBounds) {
            drawAnchor(canvas, getCenter(faceBounds.getBox()));
            drawId(canvas, faceBounds.getId().toString(), getCenter(faceBounds.getBox()));
            drawBounds(canvas, faceBounds.getBox());
        }
    }

    /**
     * Draws an anchor (dot) at the center of a face.
     */
    private void drawAnchor(Canvas canvas, PointF center) {
        canvas.drawCircle(center.x, center.y, ANCHOR_RADIUS, anchorPaint);
    }

    /**
     * Draws (Writes) the face's id.
     */
    private void drawId(Canvas canvas, String faceId, PointF center) {
        canvas.drawText("face id " + faceId, center.x - ID_OFFSET, center.y + ID_OFFSET, idPaint);
    }

    /**
     * Draws bounds around a face as a rectangle.
     */
    private void drawBounds(Canvas canvas, RectF box) {
        canvas.drawRect(box, boundsPaint);
    }

    private static PointF getCenter(RectF rect) {
        float centerX = rect.left + (rect.right - rect.left) / 2;
        float centerY = rect.top + (rect.bottom - rect.top) / 2;
        return new PointF(centerX, centerY);
    }

    private static final float ANCHOR_RADIUS = 10f;
    private static final float ID_OFFSET = 50f;
}
