package com.zhaoshengqi.android.puzzle.jigsaw.ui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.zhaoshengqi.android.puzzle.R;
import com.zhaoshengqi.android.puzzle.jigsaw.ui.test.ArcViewTestActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ZHAOSHENGQI467 on 2017/8/22.
 */

@RunWith(AndroidJUnit4.class)
public class ArcViewTest {

    @Rule
    public ActivityTestRule<ArcViewTestActivity> mActivityRule =
            new ActivityTestRule<>(ArcViewTestActivity.class);

    @Test
    public void test_launch() {
        onView(withId(R.id.arc_view));
    }
}
