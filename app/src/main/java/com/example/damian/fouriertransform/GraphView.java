package com.example.damian.fouriertransform;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Damian on 2018-04-18.
 */

public class GraphView extends SurfaceView {
    private float w, h;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        w = (float) getWidth();
        h = (float) getHeight();
    }

    public void drawGraph2D(float[] plot) {

        Canvas canvas = getHolder().lockCanvas();
        Paint paint = new Paint();

        for (int i = 0; i + 1 < plot.length; i++) {
            canvas.drawLine(
                    w / plot.length * i,
                    plot[i] * h * 2 + h / 2,
                    w / plot.length * (i + 1),
                    plot[i + 1] * h * 2 + h / 2, paint);
        }
    }
}
