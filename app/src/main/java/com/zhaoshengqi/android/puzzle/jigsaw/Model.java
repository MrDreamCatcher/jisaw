package com.zhaoshengqi.android.puzzle.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import com.zhaoshengqi.android.puzzle.jigsaw.ui.Arcview;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Canvas.ALL_SAVE_FLAG;


/**
 * Created by ZHAOSHENGQI467 on 16/11/7.
 */

public class Model {

    private static final int VARIATIONS[] = {
            10, -10
    };

    private static final int VARIATIONS_CURVE[] = {
            1, -1
    };

    private static final int VARIATIONS_RADIUS[] = {
            1, -1
    };

    private boolean mNoHoles = false;

    private int mDeltaXForCurve;
    private int mDeltaYForCurve;

    List<Puzzle> mPuzzles = new ArrayList<>();
    private int mColumns = 3;
    private int mRows = 3;
    private int mWidth = 200;
    private int mHeight = 100;
    private int mGabarit = 100;

    public Model(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int size() {
        return mPuzzles.size();
    }

    public List<Puzzle> allPuzzles() {
        return mPuzzles;
    }

    public void setNoHoles(boolean noHoles) {
        mNoHoles = noHoles;
    }

    public int getColumns() {
        return mColumns;
    }

    public void setColumns(int columns) {
        this.mColumns = columns;
    }

    public int getRows() {
        return mRows;
    }

    public void setRows(int rows) {
        this.mRows = rows;
    }

    public void setWidth(int width) {
        mWidth = width;
        mDeltaXForCurve = width / getColumns() / 8;
        setGabarit();
    }

    public void setHeight(int height) {
        mHeight = height;
        mDeltaYForCurve = height / getRows() / 8;
        setGabarit();
    }

    void setGabarit() {
        mGabarit = Math.min(mWidth / mColumns, mHeight / mRows);
    }

    void clear() {
        mPuzzles.clear();
    }

    void createAllPuzzles(int viewWidth, int viewHeight, Bitmap bitmap, float ratio) {
        setWidth(bitmap.getWidth());
        setHeight(bitmap.getHeight());
        clear();

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                Puzzle puzzle = getPuzzlePiece(cellPath(i, j, true), i, j, bitmap);
                puzzle.setRatio(ratio);
                puzzle.setRx((float) (Math.random() * viewWidth));
                puzzle.setRy((float) (Math.random() * viewHeight));
                mPuzzles.add(puzzle);
            }
        }
    }

    private Puzzle getPuzzlePiece(Path path, int row, int column, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        RectF rectf = new RectF();
        path.computeBounds(rectf, false);
        path.offset(-rectf.left, -rectf.top);
        Bitmap pieceBitmap = Bitmap.createBitmap((int) rectf.width(),
                (int) rectf.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(pieceBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        drawable.setBounds(0, 0, mWidth, mHeight);
        canvas.saveLayer(null, paint, ALL_SAVE_FLAG);
        canvas.translate(-rectf.left, -rectf.top);
        drawable.draw(canvas);
        canvas.restore();
        return new Puzzle(pieceBitmap, path, rectf.left, rectf.top, row, column);
    }

    Path cellPath(int row, int column, boolean flag) {
        Path path = new Path();
        if (!mNoHoles) {
            Point lt; // left top point of a rectangle
            Point rt; // top right point of a rectangle
            Point lb; // left bottom point of a rectangle
            Point rb; // right bottom point of a rectangle
            lt = new Point(mWidth * column / getColumns(), mHeight * row / getRows());
            rt = new Point((column + 1) * mWidth / getColumns(), mHeight * row / getRows());
            lb = new Point(mWidth * column / getColumns(), (row + 1) * mHeight / getRows());
            rb = new Point((column + 1) * mWidth / getColumns(), (row + 1) * mHeight / getRows());

            int minWidth = Math.min(mWidth / getColumns(), mHeight / getRows());
            minWidth /= 6;
            lt.x--;
            lt.y--;
            rt.x++;
            rt.y--;
            lb.x--;
            lb.y++;
            rb.x++;
            rb.y++;

            path.moveTo(lt.x, lt.y);
            final int length = VARIATIONS_CURVE.length;
            int total = (row - 1) * (column + 1) + 1;
            if (row == 0) {
                path.lineTo(rt.x, rt.y);
            } else {
                Arcview.anARC_Bottom(path, lt, rt, VARIATIONS_CURVE[total % length],
                        total % 3 == 0, minWidth + VARIATIONS_RADIUS[total % length], false);
            }

            total = (row + 1) * column + 1;
            if (column == getColumns() - 1) {
                path.lineTo(rb.x, rb.y);
            } else {
                Arcview.anARC_Left(path, rt, rb, VARIATIONS_CURVE[total % length],
                        total % 3 == 0, minWidth + VARIATIONS_RADIUS[total % length], true);
            }

            total = row * (column + 1) + 1;
            if (row == getRows() - 1) {
                path.lineTo(lb.x, lb.y);
            } else {
                Arcview.anARC_Top(path, rb, lb, VARIATIONS_CURVE[total % length],
                        total % 3 == 0, minWidth + VARIATIONS_RADIUS[total % length], false);
            }

            total = (row + 1) * (column - 1) + 1;
            if (column == 0) {
                path.lineTo(lt.x, lt.y);
            } else {
                Arcview.anARC_Right(path, lb, lt, VARIATIONS_CURVE[total % length],
                        total % 3 == 0, minWidth + VARIATIONS_RADIUS[total % length], true);
            }
        } else {
            up(path, row, column);
            right(path, row, column);
        }
        return path;
    }

    void up(Path path, int row, int column) {
        Point top = new Point((mWidth * row) / getColumns(), (mHeight * row) / getRows());
        Point right = new Point(((column + 1) * mWidth) / getColumns(), (mHeight * row) / getRows());
        if (row == 0 || row == getRows()) {
            path.quadTo((top.x + right.x) / 2, top.y + mDeltaYForCurve * curve(row, column) + variation(row, column), right.x, right.y);
        } else {
            path.lineTo(right.x, right.y);
        }
    }

    void right(Path path, int row, int column) {
        Point right = new Point(((column + 1) * mWidth) / getColumns(), (mHeight * row) / getRows());
        Point bottom = new Point(((mWidth + 1) * column) / getColumns(), ((row + 1) * mHeight) / getRows());
        if (column == 0 || column == getColumns() - 1) {
            path.lineTo(bottom.x, bottom.y);
        } else {
            path.quadTo(right.x - mDeltaXForCurve * curve(row, column) + variation(row, column),
                    (right.y + bottom.y) / 2, bottom.x, bottom.y);
        }
    }

    int curve(int row, int column) {
        return (row + column) % 2 == 0 ? 1 : -1;
    }

    int variation(int row, int column) {
        return VARIATIONS[((row + 1) * (column + 1)) % VARIATIONS.length];
    }
}
