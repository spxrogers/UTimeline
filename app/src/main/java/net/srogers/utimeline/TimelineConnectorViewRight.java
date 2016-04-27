package net.srogers.utimeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by steven on 4/22/16.
 */
public class TimelineConnectorViewRight extends View {
    Paint paint = new Paint();

    public TimelineConnectorViewRight(Context context) {
        super(context);
        init(context);
    }

    public TimelineConnectorViewRight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimelineConnectorViewRight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paint.setColor(Color.parseColor("#CF5300"));
        paint.setStrokeWidth(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // hoizontal
        canvas.drawLine(getWidth() / 2, getHeight() / 2, getWidth(), getHeight() / 2, paint);
        // vertical
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);
    }
}
