package com.zhaoshengqi.android.puzzle.jigsaw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.zhaoshengqi.android.puzzle.R;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by ZHAOSHENGQI467 on 16/11/4.
 */
public class PuzzleView extends View implements View.OnTouchListener {

    private static final String TAG = "PuzzleView";

    private final Context mContext;
    private Bitmap mPuzzleBitmap;
    private int mPuzzleWidth;
    private int mPuzzleHeight;
    private int mScrumbled = 50;
    private boolean mVisible = false;
    private boolean mFirstInit = true;
    private boolean mPreparePuzzleToShow = false;
    private boolean mShowGrid = false;
    private Vibrator mVibrator;

    private Model mModel;

    private Paint mPaint;
    private Paint mGridPaint;

    private Puzzle mCurrentPuzzle;
    private float mLastXPosition;
    private float mLastYPosition;
    private float mLatestReduceRatio = 1.0f;
    private Handler mHandler = new Handler();
    private MediaPlayer mPlaySound;

    Runnable movePuzzles = new Runnable() {

        @Override
        public void run() {
            if (mScrumbled != 0) {
                scrumbleToCenter();
                invalidate();
            }
        }
    };

    Runnable scrumbleRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (mScrumbled != 0 && mVisible) {
                    while (true) {
                        mHandler.post(movePuzzles);

                        Thread.sleep(100);

                        if (mScrumbled > 50) {
                            return;
                        }
                        mScrumbled++;
                    }
                }
            } catch (InterruptedException e) {
                Log.e("PuzzleView", "Scrumble interrupted!");
            }
        }
    };

    public PuzzleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setOnTouchListener(this);

        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mPlaySound = MediaPlayer.create(context, R.raw.finish);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6.0f);
        mPaint.setTextSize(16.0f);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setStyle(Paint.Style.STROKE);

        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setColor(Color.BLACK);
        mGridPaint.setStrokeWidth(2);
        mGridPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void init() {
        mModel = new Model(mPuzzleWidth, mPuzzleHeight);

        loadPreferences();

        mModel.createAllPuzzles(mPuzzleWidth, mPuzzleHeight, mPuzzleBitmap, mLatestReduceRatio);
        if (this.mModel.size() <= 0 && this.mFirstInit) {
            this.mFirstInit = false;
        }
        invalidate();
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public void setPuzzleWidth(int width) {
        mPuzzleWidth = width;
    }

    public void setPuzzleHeight(int height) {
        mPuzzleHeight = height;
    }

    public void setPuzzleBitmap(Bitmap bitmap) {
        mPuzzleBitmap = bitmap;
    }

    public void setScrumbled(int scrumbled) {
        mScrumbled = scrumbled;
    }

    public void scrumble() {
        if (mScrumbled != 0) {
            if (mScrumbled > 0) {
                for (int i = 0; i < mModel.size(); i++) {
                    Puzzle puzzle = mModel.allPuzzles().get(i);
                    List<Puzzle> connectedPuzzles = puzzle.getConnectedPuzzles();
                    if (!connectedPuzzles.isEmpty()) {
                        mModel.allPuzzles().addAll(connectedPuzzles);
                        connectedPuzzles.clear();
                    }
                }
                int index = 0;
                while (index < mModel.size()) {
                    mModel.allPuzzles().add(mModel.allPuzzles().remove(
                            (int) (mModel.size() * Math.random())));
                    index++;
                }
            }
            new Thread(scrumbleRunnable).start();
        }
    }

    public void loadPreferences() {
        this.mModel.setColumns(3);
        this.mModel.setRows(3);
    }

    void scrumbleToCenter() {
        float midWidth = mModel.getWidth() / 2;
        float midHeight = mModel.getHeight() / 2;
        for (Puzzle puzzle : mModel.allPuzzles()) {
            if (mScrumbled > 50) {
                puzzle.translate((float) (puzzle.getX() + (midWidth - puzzle.getX()) * 0.1d * Math.random()),
                        (float) (puzzle.getY() + (midHeight - puzzle.getY()) * 0.1d * Math.random()));
            } else {
                puzzle.translate((float) (puzzle.getX() + (puzzle.getRx() - puzzle.getX()) * 0.1d * Math.random()),
                        (float) (puzzle.getY() + (puzzle.getRy() - puzzle.getY()) * 0.1d * Math.random()));
            }
        }
        showOutsidePuzzles();
    }

    private void showOutsidePuzzles() {
        for (Puzzle puzzle : mModel.allPuzzles()) {
            if (puzzle.getX() < 0) {
                puzzle.translate(0f, puzzle.getY());
                invalidate();
            }

            if (puzzle.getY() < 0) {
                puzzle.translate(puzzle.getX(), 0f);
                invalidate();
            }

            if (puzzle.getX() > mPuzzleWidth - 40) {
                puzzle.translate(mPuzzleWidth - 60, puzzle.getY());
                invalidate();
            }

            if (puzzle.getY() > mPuzzleHeight - 40) {
                puzzle.translate(puzzle.getX(), mPuzzleHeight - 60);
                invalidate();
            }
        }
    }

    public void clearModel() {
        mPreparePuzzleToShow = true;
        mModel.clear();
    }

    private boolean restore() {
        if (mPreparePuzzleToShow) {
            mPreparePuzzleToShow = false;
        }
        mScrumbled = 20;
        init();
        return true;
    }

    private Puzzle getCurrentPuzzle(float x, float y) {
        List<Puzzle> puzzles = mModel.allPuzzles();
        for (int i = puzzles.size() - 1; i >= 0 ; i--) {
            Puzzle puzzle = puzzles.get(i);
            if (puzzle.getBounds().contains(x, y)) {
                return puzzle;
            }
        }
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastXPosition = x;
                mLastYPosition = y;
                mCurrentPuzzle = getCurrentPuzzle(x, y);
                ListIterator<Puzzle> it =  mModel.allPuzzles().listIterator();
                while (it.hasNext()) {
                    Puzzle puzzle = it.next();
                    if (puzzle.containsPoint(x, y) != null) {
                        mCurrentPuzzle = puzzle;
                        it.remove();
                        it.add(puzzle);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentPuzzle != null) {
                    mCurrentPuzzle.translate(mCurrentPuzzle.getX() + x - mLastXPosition,
                            mCurrentPuzzle.getY() + y - mLastYPosition);
                    mLastXPosition = x;
                    mLastYPosition = y;

                    checkConnections();

                    invalidate();
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == 0 && mCurrentPuzzle != null) {
                    mCurrentPuzzle = null;
                    invalidate();
                }
                break;
        }
        return true;
    }

    private void checkConnections() {
        for (int i = 0; i < mModel.size(); i++) {
            if (mCurrentPuzzle.absorbNeighbor(mModel.allPuzzles().get(i))) {
                mModel.allPuzzles().remove(i);
                if (mModel.size() != 1) {
                    indicateClash(false);
                } else {
                    indicateClash(true);
                }
            }
        }
    }

    private void indicateClash(boolean success) {
        if (success) {
            Toast.makeText(mContext, "Congratulations.",
                    Toast.LENGTH_LONG).show();
        }
        mVibrator.vibrate(20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mPreparePuzzleToShow || restore()) {
            mPaint.setColor(Color.BLACK);
            ListIterator<Puzzle> it = mModel.allPuzzles().listIterator();
            while (it.hasNext()) {
                Puzzle puzzle = it.next();
                canvas.drawBitmap(puzzle.getBmp(), puzzle.getMatrix(), mPaint);
                ListIterator<Puzzle> it2 = puzzle.getConnectedPuzzles().listIterator();
                while (it2.hasNext()) {
                    Puzzle connectedPuzzle = it2.next();
                    canvas.drawBitmap(connectedPuzzle.getBmp(), connectedPuzzle.getMatrix(), mPaint);
                }
            }

            if (mShowGrid) {
                for (int i = 0; i < mModel.getRows(); i++) {
                    for (int j = 0; j < mModel.getColumns(); j++) {
                        canvas.drawPath(mModel.cellPath(i, j, false), mGridPaint);
                    }
                }
            }
        }
    }
}
