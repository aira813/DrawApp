package jp.tkcdroid.drawapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.tkcdroid.drawapp.MainActivity;
import jp.tkcdroid.drawapp.view.dto.PathDto;

public class DrawView extends View {
    private List<PathDto> mPathDtoList;
    private Path mCurrentPath;
    private int mCurrentPaintSize = 2;
    private int mCurrentPaintColor = Color.BLACK;
    private Bitmap mBitmap;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPathDtoList = new ArrayList<PathDto>();
        mBitmap = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setCurrentCanvas(canvas);
        if (mCurrentPath != null) {
            canvas.drawPath(mCurrentPath, getPaint());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentPath = new Path();
                mCurrentPath.moveTo(event.getX(), event.getY());
                MainActivity mainActivity = (MainActivity) getContext();
                mainActivity.hidePenMenu();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPath.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCurrentPath.lineTo(event.getX(), event.getY());
                mPathDtoList.add(new PathDto(mCurrentPath, getPaint()));
                mCurrentPath = null;
                invalidate();
                break;
        }
        return true;
    }

    public String save(String attachName) {
        // Bitmapの作成
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        setCurrentCanvas(canvas);

        try {
            FileOutputStream out = new FileOutputStream(attachName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("ERROR", "" + e);
        }
        return attachName;

    }

    private void setCurrentCanvas(Canvas canvas) {
        if (mBitmap == null) {
            canvas.drawColor(Color.WHITE);
        } else {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        for (PathDto pathDto : mPathDtoList) {
            canvas.drawPath(pathDto.getPath(), pathDto.getPaint());
        }
    }

    public void reset() {
        init();
        invalidate();
    }

    public void undo() {
        if (mPathDtoList.size() > 0) {
            mPathDtoList.remove(mPathDtoList.size() - 1);
        }
        invalidate();
    }

    public void setPenColor(int color) {
        mCurrentPaintColor = color;
    }

    public void setPenSize(int penSize) {
        mCurrentPaintSize = penSize;
    }

    public int getPenSize() {
        return mCurrentPaintSize;
    }

    public int getPenColor(){
        return mCurrentPaintColor;
    }

    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mCurrentPaintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mCurrentPaintSize);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }
}
