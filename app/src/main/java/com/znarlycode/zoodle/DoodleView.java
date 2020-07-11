package com.znarlycode.zoodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Stack;

public class DoodleView extends View {

    private static final int TOUCH_TOLERANCE = 10;

    private int backgroundColor = Color.WHITE;
    private int strokeColor = Color.BLACK;

    private Bitmap bitmap;
    private Canvas canvas;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointsMap;
    private Paint linePaint;
    private Paint screenPaint;

    private Stack<Integer> pointerIds = new Stack<>();

    public DoodleView(Context context) {
        this(context, null);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenPaint = new Paint();
        linePaint = new Paint();
        pathMap = new HashMap<>();
        previousPointsMap = new HashMap<>();
        strokeColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        backgroundColor = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);

        linePaint.setAntiAlias(true);
        linePaint.setColor(strokeColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(7f);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        bitmap.eraseColor(backgroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, screenPaint);
        for (Integer pathKey : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(pathKey), linePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            onTouchStarted(
                    event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            onTouchEnded(event.getPointerId(actionIndex));
        } else {
            onTouchMoved(event);
        }

        invalidate();
        return true;
    }

    public void clear() {
        pathMap.clear();
        previousPointsMap.clear();
        bitmap.eraseColor(backgroundColor);
        invalidate();
    }

    boolean setIsFill() {
        if (linePaint.getStyle() == Paint.Style.STROKE) {
            linePaint.setStyle(Paint.Style.FILL);
            return false;
        }
        linePaint.setStyle(Paint.Style.STROKE);
        return true;
    }

    int getStrokeColor() {
        return linePaint.getColor();
    }

    void setStrokeColor(int color) {
        linePaint.setColor(color);
    }

    int getBackgroundColor() {
        return backgroundColor;
    }


    public void setBackgroundColor(int color) {
        backgroundColor = (color);
        bitmap.eraseColor(backgroundColor);
        invalidate();
        super.setBackgroundColor(color);
    }

    void setStrokeWidth(int strokeWidth) {
        linePaint.setStrokeWidth(strokeWidth);
    }

    int getStrokeWidth() {
        return (int) linePaint.getStrokeWidth();
    }

    private void onTouchMoved(MotionEvent event) {
        for (int pointerIndex = 0; pointerIndex < event.getPointerCount(); pointerIndex++) {
            int pointerId = event.getPointerId(pointerIndex);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointsMap.get(pointerId);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(
                            point.x,
                            point.y,
                            (newX + point.x) / 2,
                            (newY + point.y) / 2);
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void onTouchEnded(int pointerId) {
        Path path = pathMap.get(pointerId);
        canvas.drawPath(path, linePaint);
        path.reset();
        pointerIds.push(pointerId);
    }

    private void onTouchStarted(float x, float y, int pointerId) {
        Point point;
        Path path;

        if (pathMap.containsKey(pointerId)) {
            path = pathMap.get(pointerId);
            point = previousPointsMap.get(pointerId);
        } else {
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointsMap.put(pointerId, point);
        }

        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    boolean eraseLast() {
        if (pointerIds.empty()) {
            return false;
        }
        Integer pointerId = pointerIds.pop();
        Path path = pathMap.remove(pointerId);
        previousPointsMap.remove(pointerId);
        //canvas.clipPath(path);
        invalidate();
        return true;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
