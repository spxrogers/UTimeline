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
public class TimelineConnectorView extends View {
    Paint paint = new Paint();
    private int screenWidth;

    public TimelineConnectorView(Context context) {
        super(context);
        init(context);
    }

    public TimelineConnectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimelineConnectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        paint.setColor(Color.parseColor("#CF5300"));
        paint.setStrokeWidth(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // hoizontal
        canvas.drawLine(0, getHeight() / 2, getWidth() / 2, getHeight() / 2, paint);
        // vertical
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);
    }

    // todo: remove after how-to
    private int getCenterX() {
        double len = screenWidth - (getWidth() / 6 * 10 / 4);
        return (int)len;
    }
}
