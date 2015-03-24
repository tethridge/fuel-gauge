/*
 * Copyright (C) 2015 Unchained Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unchained.fuelgauge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;

import com.unchained.widgets.FuelGaugeView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    private final long ANIMATION_DURATION = 300;
    private final int EMPTY = 0;
    private final int FULL = 1;

    @InjectView(R.id.fuelGaugeView) FuelGaugeView mFuelGauge;
    @InjectView(R.id.emptyBtn) Button mEmptyBtn;
    @InjectView(R.id.fullBtn) Button mFullBtn;
    @InjectView(R.id.seekBar) SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mSeekBar.setProgress(Math.round(mFuelGauge.getFuelLevel() * 100));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    handleSeekBarChanged(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.emptyBtn)
    protected void handleEmptyButtonClicked() {
        float oldLevel = mFuelGauge.getFuelLevel();
        float newLevel = EMPTY;
        animateFuelLevelChange(oldLevel, newLevel);
    }

    @OnClick(R.id.fullBtn)
    protected void handleFullButtonClicked() {
        float oldLevel = mFuelGauge.getFuelLevel();
        float newLevel = FULL;
        animateFuelLevelChange(oldLevel, newLevel);
    }

    protected void handleSeekBarChanged(int progress) {
        float newLevel = (float) progress / 100f;
        mFuelGauge.setFuelLevel(newLevel);
    }

    protected void enableControls(boolean enabled) {
        mEmptyBtn.setEnabled(enabled);
        mFullBtn.setEnabled(enabled);
        mSeekBar.setEnabled(enabled);
    }

    protected void animateFuelLevelChange(float oldLevel, float newLevel) {
        if (oldLevel != newLevel) {
            enableControls(false);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mFuelGauge, "fuelLevel", oldLevel, newLevel);
            animator.setDuration(ANIMATION_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentProgress = (float) animation.getAnimatedValue();
                    mSeekBar.setProgress(Math.round(currentProgress * 100));
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    enableControls(true);
                }
            });
            animator.start();
        }
    }
}
