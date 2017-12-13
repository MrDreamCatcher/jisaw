package com.zhaoshengqi.android.puzzle.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ZHAOSHENGQI467 on 16/11/4.
 */
public class Puzzle {

    private Bitmap mBmp;

    private RectF mBounds = new RectF();

    private ArrayList<Puzzle> mConnectedPuzzles = new ArrayList<>();

    private Matrix mMatrix = new Matrix();

    private Path mNewPath;
    private Path mPath;

    private float mRatio = 1f;

    private float mRx;
    private float mRy;
    private float mX;
    private float mY;
    private float mXOriginal;
    private float mYOriginal;

    public Bitmap getBmp() {
        return mBmp;
    }

    public RectF getBounds() {
        return mBounds;
    }

    public ArrayList<Puzzle> getConnectedPuzzles() {
        return mConnectedPuzzles;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    public float getRx() {
        return mRx;
    }

    public void setRx(float rx) {
        this.mRx = rx;
    }

    public float getRy() {
        return mRy;
    }

    public void setRy(float ry) {
        this.mRy = ry;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    int row;
    int col;

    public Puzzle(Bitmap bitmap, Path path, float xOriginal, float yOriginal, int row, int col) {
        mBmp = bitmap;
        this.mPath = path;
        this.mXOriginal = xOriginal;
        this.mYOriginal = yOriginal;
        this.row = row;
        this.col = col;
        mX = (col % 2) + xOriginal;
        mY = (row % 2) + yOriginal;
        applyMove();
    }

    public Puzzle containsPoint(float x, float y) {
        if (!mBounds.contains(x, y)) {
            ListIterator<Puzzle> it = mConnectedPuzzles.listIterator();
            while (it.hasNext()) {
                Puzzle puzzle = it.next();
                if (puzzle.mBounds.contains(x, y)) {
                    return puzzle;
                }
            }
        }
        return null;
    }

    public void translate(float x, float y) {
        this.mX = x;
        this.mY = y;
        applyMove();
        translateConnected();
    }

    public void applyMove() {
        mMatrix.reset();
        mMatrix.postTranslate(mX, mY);
        mNewPath = new Path(mPath);
        mNewPath.transform(mMatrix);
        mNewPath.computeBounds(mBounds, false);
    }

    public void translateConnected() {
        ListIterator<Puzzle> it = mConnectedPuzzles.listIterator();
        while (it.hasNext()) {
            Puzzle puzzle = it.next();
            puzzle.translate(puzzle.mXOriginal + mX - mXOriginal, puzzle.mYOriginal + mY - mYOriginal);
        }
    }

    boolean absorbNeighbor(Puzzle puzzle) {
        if(isNeighbour(puzzle)) {
            mConnectedPuzzles.add(puzzle);
            mConnectedPuzzles.addAll(puzzle.mConnectedPuzzles);
            puzzle.mConnectedPuzzles.clear();
            translateConnected();
            return true;
        }
        return false;
    }

    private boolean isConnected(Puzzle puzzle) {
        return this != puzzle && !this.mConnectedPuzzles.contains(puzzle) &&
                Math.abs(this.col - puzzle.col) < 2 && Math.abs(Math.abs(this.mXOriginal - this.mX) -
                Math.abs(puzzle.mXOriginal - puzzle.mX)) < 4.0f && Math.abs(this.row - puzzle.row) < 2 &&
                Math.abs(Math.abs(this.mYOriginal - this.mY) - Math.abs(puzzle.mYOriginal - puzzle.mY)) < 4.0f &&
                (this.row == puzzle.row || this.col == puzzle.col);
    }

    private boolean isNeighbour(Puzzle puzzle) {
        if (this == puzzle) {
            return false;
        }
        if (mConnectedPuzzles.contains(puzzle)) {
            return false;
        }
        boolean isConnected = isConnected(puzzle);
        if (!isConnected) {
            for (Puzzle p : puzzle.mConnectedPuzzles) {
                if (isConnected(p)) {
                    return true;
                }
            }
            for (Puzzle p : mConnectedPuzzles) {
                if (p.isConnected(puzzle)) {
                    return true;
                }
            }
            for (Puzzle p : mConnectedPuzzles) {
                for (Puzzle p1 : puzzle.mConnectedPuzzles) {
                    if (p.isConnected(p1)) {
                        return true;
                    }
                }
            }
        }
        return isConnected;
    }

    private boolean isOriginalNeighbour(Puzzle puzzle) {
        return this != puzzle && !this.mConnectedPuzzles.contains(puzzle) &&
                Math.abs(this.col - puzzle.col) < 2 && Math.abs(this.row - puzzle.row) < 2 &&
                (this.row == puzzle.row || this.col == puzzle.col);
    }

    boolean isPossibleNeighbor(Puzzle puzzle) {
        if (this == puzzle || this.mConnectedPuzzles.contains(puzzle)) {
            return false;
        }

        boolean isOriginalNeighbour = isOriginalNeighbour(puzzle);
        if (!isOriginalNeighbour) {
            for (Puzzle p : puzzle.mConnectedPuzzles) {
                if (isOriginalNeighbour(p)) {
                    return true;
                }
            }
            for (Puzzle p : mConnectedPuzzles) {
                if (p.isOriginalNeighbour(puzzle)) {
                    return true;
                }
            }
            for (Puzzle p : mConnectedPuzzles) {
                for (Puzzle p1 : puzzle.mConnectedPuzzles) {
                    if (p1.isOriginalNeighbour(p)) {
                        return true;
                    }
                }
            }
        }
        return isOriginalNeighbour;
    }
}
