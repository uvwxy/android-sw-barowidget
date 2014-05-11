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

package de.uvwxy.barometer.swidget;

import android.os.Handler;
import android.util.Log;

import com.sonyericsson.extras.liveware.extension.util.widget.BaseWidget;

/**
 * This demonstrates how to implement a simple text widget.
 */
class BaroWidget extends BaseWidget {


    public static final int WIDGET_WIDTH_CELLS = 2;
    public static final int WIDGET_HEIGHT_CELLS = 1;

    /**
     * Creates a widget extension.
     */
    public BaroWidget(WidgetBundle bundle) {
        super(bundle);
        baro = new BaroLogic();
    }

    @Override
    public void onStartRefresh() {
    	baro.setContext(mContext);
		Log.d(BaroWidgetExtensionService.LOG_TAG, "startRefresh");
		baro.loadValue();
		baro.loadValueRelative();
		restartRefresh();
    }

	@Override
	public void onStopRefresh() {
		Log.d(BaroWidgetExtensionService.LOG_TAG, "stopRefesh");
		baro.storeValue();
		baro.storeValueRelative();
		pauseHandler();
	}

    @Override
    public void onTouch(final int type, final int x, final int y) {
        Log.d(BaroWidgetExtensionService.LOG_TAG, "onTouch() " + type);
    }

    @Override
    public int getWidth() {
        return (int)(mContext.getResources().getDimension(R.dimen.smart_watch_2_widget_cell_width) * WIDGET_WIDTH_CELLS);
    }

    @Override
    public int getHeight() {
        return (int)(mContext.getResources().getDimension(R.dimen.smart_watch_2_widget_cell_height) * WIDGET_HEIGHT_CELLS);
    }

    @Override
    public int getPreviewUri() {
        return R.drawable.widget_frame;
    }

    @Override
    public int getName() {
        return R.string.extension_name;
    }
	
	private static final int NUM_REFRESHS = 5;
	private static final long HEIGHT_RESET_TIMEOUT = 3000;
	private int refreshCount = 0;
	private int longClickCount = 0;

	private BaroLogic baro;

	private Handler mHandler = new Handler();
	private long delayMillis = 1000;



//
//	@Override
//	public void onTouch(final int type, final int x, final int y) {
//		Log.d(BaroWidgetExtensionService.LOG_TAG, "onTouch() " + type);
//
//		switch (type) {
//		case 0:
//			restartRefresh();
//			break;
//		case 1:
//			// longclick down + up -> refresh when up -> even click (down + up = 2)
//			longClickCount++;
//
//			if (longClickCount % 2 == 0) {
//
//				if (System.currentTimeMillis() - baro.getValueRelativeSetTime() < HEIGHT_RESET_TIMEOUT) {
//					baro.setValueRelative(0f);
//					Log.d(BaroWidgetExtensionService.LOG_TAG, "setting ref to 0");
//				} else {
//					baro.setValueRelative(baro.getValue());
//					Log.d(BaroWidgetExtensionService.LOG_TAG, "setting ref to " + baro.getValue());
//
//				}
//
//				baro.setValueRelativeSetTime(System.currentTimeMillis());
//				restartRefresh();
//			}
//
//			break;
//		}
//	}
//
	private void restartRefresh() {
		pauseHandler();
		refreshCount = 0;
		unPauseHandler();
	}

	private void updateScreen() {
//		Unit uLastValue = Unit.from(Unit.MILLI_BAR);
//		uLastValue.setValue(baro.getValue());
//
//		Unit uLastHeight = Unit.from(Unit.METRE);
//		if (baro.getValueRelative() > 0) {
//			uLastHeight.setValue(BarometerReader.getHeightFromDiff(baro.getValue(), baro.getValueRelative()));
//		} else {
//			uLastHeight.setValue(BarometerReader.getHeight(baro.getValue()));
//		}

//		// Create a bundle with last read (pressue)
//		Bundle bundlePressure = new Bundle();
//		bundlePressure.putInt(Widget.Intents.EXTRA_LAYOUT_REFERENCE, R.id.tvPressure);
//		bundlePressure.putString(Control.Intents.EXTRA_TEXT, uLastValue.toString());
//
//		// Create a bundle with last read value (height)
//		Bundle bundleHeight = new Bundle();
//		bundleHeight.putInt(Widget.Intents.EXTRA_LAYOUT_REFERENCE, R.id.tvHeight);
//		bundleHeight.putString(Control.Intents.EXTRA_TEXT, uLastHeight.toString());
//
//		Bundle[] layoutData = new Bundle[] { bundlePressure, bundleHeight };

		// Send a UI when the widget is visible.
		showLayout(R.layout.layout_widget, null);
	}

	private Runnable mUpdateTimeTask = new Runnable() {

		public void run() {
			updateScreen();
			refreshCount++;

			if (refreshCount < NUM_REFRESHS) {
				mHandler.postDelayed(this, delayMillis);
			} else {
				refreshCount = 0;
				pauseHandler();
			}
		}
	};

	private void pauseHandler() {
		baro.stop();
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	private void unPauseHandler() {
		baro.start();
		updateScreen();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, delayMillis);
	}

}
