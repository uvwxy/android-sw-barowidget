/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.uvwxy.swidgets.barometer;

import android.os.Bundle;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.widget.Widget;
import com.sonyericsson.extras.liveware.extension.util.widget.BaseWidget;

import de.uvwxy.sensors.BarometerReader;
import de.uvwxy.units.Unit;

/**
 * This demonstrates how to implement a simple text widget.
 */
class BaroWidget extends BaseWidget {
    public static final int WIDGET_WIDTH_CELLS = 2;
    public static final int WIDGET_HEIGHT_CELLS = 1;
    private static final int TOTAL_REFRESH_COUNT = 5;
    private static final long REFRESH_TIMEOUT_MILLIS = 1000;
    private int refreshCount = 0;
    private int longClickCount = 0;

    private BaroLogic baro;
    private BaroWidgetRegistrationInformation baroRegInfo;

    /**
     * Creates a widget extension.
     */
    public BaroWidget(WidgetBundle bundle) {
        super(bundle);

        baro = new BaroLogic(mContext);
        baroRegInfo = new BaroWidgetRegistrationInformation(mContext);
    }

    @Override
    public void onStartRefresh() {

        Log.d(BaroWidgetExtensionService.LOG_TAG, "startRefresh");
        baro.loadValue();
        baro.loadValueRelative();
        baro.loadValueRelativeMode();
        // user action, always continue with N refreshs directly
        restartRefreshLoop(0, true);
    }

    @Override
    public void onStopRefresh() {
        Log.d(BaroWidgetExtensionService.LOG_TAG, "stopRefesh");
        baro.storeValue();
        baro.storeValueRelative();
        baro.storeValueRelativeMode();
        cancelScheduledRefresh(baroRegInfo.getExtensionKey());
    }

    @Override
    public void onTouch(final int type, final int x, final int y) {
        Log.d(BaroWidgetExtensionService.LOG_TAG, "onTouch() " + type + "/" + longClickCount);
        float baroMillis = baro.getBlockedValue();
        switch (type) {
        case 0:
            // user action, always continue with N refreshs directly
            baro.nextRelativeMode();
            restartRefreshLoop(0, true);
            break;
        case 1:
            // longclick down + up -> refresh when up -> even click (down + up = 2)
            longClickCount++;

            if (longClickCount % 2 == 0) {
                baro.setValueRelative(baroMillis);
                Log.d(BaroWidgetExtensionService.LOG_TAG, "setting ref to " + baroMillis);

                baro.setValueRelativeSetTime(System.currentTimeMillis());
                // user action, always continue with N refreshs directly
                restartRefreshLoop(0, true);
            }

            break;
        }
    }

    private void restartRefreshLoop(long timeoutMillis, boolean restart) {
        if (restart) {
            refreshCount = 0;
        }
        long now = System.currentTimeMillis();
        String key = baroRegInfo.getExtensionKey();
        scheduleRefresh(now + timeoutMillis, key);
    }

    @Override
    public void onScheduledRefresh() {
        updateScreen();

        if (refreshCount < TOTAL_REFRESH_COUNT) {
            refreshCount++;
            // do not restart, always continue
            restartRefreshLoop(REFRESH_TIMEOUT_MILLIS, false);
        }
    }

    private void updateScreen() {
        Unit uLastValue = Unit.from(Unit.MILLI_BAR);
        float baroMillis = baro.getBlockedValue();
        uLastValue.setValue(baroMillis);

        Unit uLastHeight = Unit.from(Unit.METRE);
        if (baro.getValueRelative() > 0) {
            uLastHeight.setValue(BarometerReader.getHeightFromDiff(baroMillis, baro.getValueRelative()));
        } else {
            uLastHeight.setValue(BarometerReader.getHeight(baroMillis));
        }

        // Create a bundle with last read (pressue)
        Bundle bundlePressure = new Bundle();
        bundlePressure.putInt(Widget.Intents.EXTRA_LAYOUT_REFERENCE, R.id.tvPressure);
        bundlePressure.putString(Control.Intents.EXTRA_TEXT, uLastValue.toString());

        // Create a bundle with last read value (height)
        Bundle bundleHeight = new Bundle();
        bundleHeight.putInt(Widget.Intents.EXTRA_LAYOUT_REFERENCE, R.id.tvHeight);
        bundleHeight.putString(Control.Intents.EXTRA_TEXT, uLastHeight.toString());

        Bundle[] layoutData = new Bundle[] { bundlePressure, bundleHeight };

        // Send a UI when the widget is visible.
        showLayout(R.layout.layout_widget, layoutData);
    }

    @Override
    public int getWidth() {
        return (int) (mContext.getResources().getDimension(R.dimen.smart_watch_2_widget_cell_width) * WIDGET_WIDTH_CELLS);
    }

    @Override
    public int getHeight() {
        return (int) (mContext.getResources().getDimension(R.dimen.smart_watch_2_widget_cell_height) * WIDGET_HEIGHT_CELLS);
    }

    @Override
    public int getPreviewUri() {
        return R.drawable.widget_frame;
    }

    @Override
    public int getName() {
        return R.string.extension_name;
    }
}
