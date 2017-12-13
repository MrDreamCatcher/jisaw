package com.zhaoshengqi.android.puzzle.jigsaw.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.zhaoshengqi.android.puzzle.R;
import com.zhaoshengqi.android.puzzle.jigsaw.PuzzleView;

import java.util.Random;

/**
 * Created by ZHAOSHENGQI467 on 16/11/4.
 */

public class GameActivity extends Activity {

    public static int[] IMAGES = new int[]{
            R.drawable.bear,
            R.drawable.lion,
            R.drawable.cat,
    };

    Handler mHandler = new Handler();
    Vibrator mVibrator;
    PuzzleView mPuzzleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_game);
        mPuzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        mPuzzleView.setPuzzleBitmap(newRandomBitmap());
        mPuzzleView.setScrumbled(20);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPuzzleView.clearModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPuzzleView.scrumble();
                mPuzzleView.invalidate();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPuzzleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mPuzzleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPuzzleView.setPuzzleWidth(mPuzzleView.getMeasuredWidth());
                mPuzzleView.setPuzzleHeight(mPuzzleView.getHeight());
                mPuzzleView.init();
            }
        });
        mPuzzleView.setVisible(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPuzzleView.setVisible(false);
    }

    private Bitmap newRandomBitmap() {
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                IMAGES[new Random().nextInt(IMAGES.length)]);
        if (bitmap.getWidth() > screenWidth) {
            bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth,
                    screenWidth * bitmap.getHeight() / bitmap.getWidth(), true);
        }

        if (bitmap.getHeight() > screenHeight) {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    screenHeight * bitmap.getWidth() / bitmap.getHeight(), screenHeight, true);
        }

        return bitmap;
    }
}
