package com.zhaoshengqi.android.puzzle.jigsaw.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZHAOSHENGQI467 on 16/11/15.
 */
public class Arcview extends View {

    private Paint mPaint;
    Path mPath;

    public Arcview(Context context) {
        super(context);
        init();
    }

    public Arcview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPath = new Path();
    }

    public static void anARC_Top(Path path, Point startPoint, Point endPoint, int curveDeep,
                                 boolean curveOutside, int notchCircle, boolean notchOutside) {
        float left = Math.min(startPoint.x, endPoint.x);
        float top = Math.min(startPoint.y, endPoint.y) - curveDeep + 1f;
        float right = Math.max(startPoint.x, endPoint.x);
        float bottom = Math.max(startPoint.y, endPoint.y) + curveDeep + 1f;
        RectF ovalRect = new RectF(left, top, right, bottom);

        float midX = (left + right) / 2f;
        left = midX - notchCircle + (notchOutside ? 1f : -1f);
        top = (curveOutside ? top : bottom) + (notchOutside ? -notchCircle * 2 : 0) -
                (notchOutside ? 0 : -2f);
        right = midX + notchCircle + (notchOutside ? -1f : 0);
        bottom = (curveOutside ? top : bottom) + (notchOutside ? 0 : 2 * notchCircle) +
                (notchOutside ? -2f : 0f);
        RectF rectF = new RectF(left, top, right, bottom);

        path.arcTo(ovalRect, 0, curveOutside ? -70f : 70f);
        path.arcTo(rectF, notchOutside ? 45f : -45f, notchOutside ? -270 : 270);
        path.arcTo(ovalRect, curveOutside ? -110 : 110, curveOutside ? -50 : 50);
        path.lineTo(endPoint.x, endPoint.y);
    }

    public static void anARC_Left(Path path, Point startPoint, Point endPoint, int curveDeep,
                                  boolean curveOutside, int notchCircle, boolean notchOutside) {
        float left = Math.min(startPoint.x, endPoint.x) - curveDeep + 1f;
        float top = Math.min(startPoint.y, endPoint.y);
        float right = Math.max(startPoint.x, endPoint.x) + curveDeep - 1f;
        float bottom = Math.max(startPoint.y, endPoint.y);
        RectF ovalRect = new RectF(left, top, right, bottom);

        float midY = (top + bottom) / 2;
        left = (curveOutside ? right : left) + (notchOutside ? 0 : -notchCircle * 2) +
                (notchOutside ? 2f : 0);
        top = midY - notchCircle + (notchOutside ? -1f : 1f);
        right = (curveOutside ? right : left) + (notchOutside ? 2 * notchCircle : 0) + (notchOutside ? 2f : 0f);
        bottom = midY + notchCircle + (notchOutside ? 1f : -1f);
        RectF rectF = new RectF(left, top, right, bottom);

        path.arcTo(ovalRect, -90f, (curveOutside ? 70f : -70f));
        path.arcTo(rectF, (notchOutside ? -135f : -45f), (notchOutside ? 270f : -270f));
        path.arcTo(ovalRect, (curveOutside ? 20f : 160f), (curveOutside ? 70f : -70f));
        path.lineTo(endPoint.x, endPoint.y);
    }

    public static void anARC_Right(Path path, Point startPoint, Point endPoint, int curveDeep,
                                   boolean curveOutside, int notchCircle, boolean notchOutside) {
        float left = Math.min(startPoint.x, endPoint.x) - curveDeep + 1f;
        float top = Math.min(startPoint.y, endPoint.y);
        float right = Math.max(startPoint.x, endPoint.x) + curveDeep + 1f;
        float bottom = Math.max(startPoint.y, endPoint.y);
        RectF ovalRect = new RectF(left, top, right, bottom);

        float midY = (top + bottom) / 2f;
        left = (curveOutside ? right : left) + (notchOutside ? 0 : -notchCircle * 2) +
                (notchOutside ? 0 : -2f);
        top = midY - notchCircle + (notchOutside ? 1f : -1f);
        right = (curveOutside ? right : left) + (notchOutside ? notchCircle * 2 : 0) +
                (notchOutside ? -2f : -1f);
        bottom = midY + notchCircle + (notchOutside ? -1f : 1f);
        RectF rectF = new RectF(left, top, right, bottom);

        path.arcTo(ovalRect, 90f, (curveOutside ? -70f : 70f));
        path.arcTo(rectF, (notchOutside ? 135f : 45f), (notchOutside ? -270f : 270f));
        path.arcTo(ovalRect, (curveOutside ? -20f : 200f), (curveOutside ? -70f : 70f));
        path.lineTo(endPoint.x, endPoint.y);
    }

    public static void anARC_Bottom(Path path, Point startPoint, Point endPoint, int curveDeep,
                                    boolean curveOutside, int notchCircle, boolean notchOutside) {
        float left = Math.min(startPoint.x, endPoint.x);
        float top = Math.min(startPoint.y, endPoint.y) - curveDeep - 1f;
        float right = Math.max(startPoint.x, endPoint.x);
        float bottom = Math.max(startPoint.y, endPoint.y) + curveDeep - 1f;
        RectF ovalRect = new RectF(left, top, right, bottom);

        float midX = (left + right) / 2f;
        left = midX - notchCircle + (notchOutside ? -1f : 0f);
        top = (curveOutside ? top : bottom) + (notchOutside ? -notchCircle * 2 : 0) +
                (notchOutside ? 0 : 2f);
        right = midX + notchCircle + (notchOutside ? -1f : 0f);
        bottom = (curveOutside ? top : bottom) + (notchOutside ? 0 : notchCircle * 2) +
                (notchOutside ? -2f : 0f);
        RectF rectF = new RectF(left, top, right, bottom);

        path.arcTo(ovalRect, 180f, (curveOutside ? 70f : -70f));
        path.arcTo(rectF, (notchOutside ? 135f : -135f), (notchOutside ? 270f : -270f));
        path.arcTo(ovalRect, (curveOutside ? -70f : 70f), (curveOutside ? 50f : -50f));
        path.lineTo(endPoint.x, endPoint.y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // test puzzle path
        mPath.moveTo(100f, 100f);
        Arcview.anARC_Left(mPath, new Point(100, 100), new Point(100, 200), 8, true, 20, false);
        Arcview.anARC_Bottom(mPath, new Point(100, 200), new Point(200, 200), 8, true, 20, false);
        Arcview.anARC_Right(mPath, new Point(200, 200), new Point(200, 100), 8, false, 20, true);
        Arcview.anARC_Top(mPath, new Point(200, 100), new Point(100, 100), 8, false, 20, true);
        mPath.close();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
    }
}
