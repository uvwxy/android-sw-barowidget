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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.widget.Widget;
import com.sonyericsson.extras.liveware.extension.util.widget.BaseWidget;

import de.uvwxy.sensors.BarometerReader;
import de.uvwxy.units.Unit;

/**
 * This demonstrates how to implement a simple text widget.
 */
class AltiWidget1x1 extends BaroWidget {
    /**
     * Creates a widget extension.
     */
    public AltiWidget1x1(WidgetBundle bundle) {
        super(bundle);
        WIDGET_HEIGHT_CELLS = 1;
        WIDGET_WIDTH_CELLS = 1;
    }

    @Override
    protected void updateScreen() {
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
        Bundle bundleHeight = new Bundle();
        bundleHeight.putInt(Widget.Intents.EXTRA_LAYOUT_REFERENCE, R.id.tv1x1);
        
        String strHeight = uLastHeight.to(Unit.from(unitLength)).toString();
        if (baro.getValueRelative() > 0) {
            strHeight = strHeight + "~";
        }
        bundleHeight.putString(Control.Intents.EXTRA_TEXT, strHeight);

        Bundle[] layoutData = new Bundle[] { bundleHeight };

        // Send a UI when the widget is visible.
        showLayout(R.layout.layout_widget1x1, layoutData);
    }

    @Override
    public int getPreviewUri() {
        return R.drawable.swidgets_baro;
    }

    @Override
    public int getName() {
        return R.string.extension_name_alti_1x1;
    }
}
