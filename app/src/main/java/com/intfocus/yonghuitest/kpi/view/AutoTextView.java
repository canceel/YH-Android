package com.intfocus.yonghuitest.kpi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * 根据字符串的长度自动缩小字体
 * Created by zbaoliang on 17-5-10.
 */
public class AutoTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint testPaint;
    private float cTextSize;

    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        refitText(getText().toString(), this.getWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            testPaint = new Paint();
            testPaint.set(this.getPaint());
            float availableWidth = textWidth - getPaddingLeft() - getPaddingRight();
            Rect rect = new Rect();
            testPaint.getTextBounds(text, 0, text.length(), rect);
            float textWidths = rect.width();
            cTextSize = this.getTextSize();
            while (textWidths > availableWidth) {
                cTextSize = cTextSize - 20;
                testPaint.setTextSize(cTextSize);
                textWidths = testPaint.measureText(text);
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, cTextSize);
        }
    }
}
