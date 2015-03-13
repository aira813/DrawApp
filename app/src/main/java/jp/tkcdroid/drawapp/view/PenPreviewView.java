package jp.tkcdroid.drawapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PenPreviewView extends View {
    private int mPenSize;
    private int mPenColor = Color.BLACK;

    public PenPreviewView(Context context) {
        super(context);
    }

    public PenPreviewView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPenSize < 2) {
            canvas.drawPoint(getWidth() / 2, getHeight() / 2, getPaint());
        } else {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mPenSize / 2, getPaint());
        }
    }

    public void setPenSize(int penSize) {
        mPenSize = penSize;
        invalidate();
    }

    public void setPenColor(int penColor) {
        mPenColor = penColor;
        invalidate();
    }

    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setColor(mPenColor);
        paint.setAntiAlias(true);
        return paint;
    }

}
