package de.uvwxy.barometer.swidget;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.BarometerReader;
import de.uvwxy.sensors.SensorReader.SensorResultCallback;

public class BaroLogic {
    private static final String BARO_WIDGET_SETTINGS = "BARO_SETTINGS";
    private static final String VALUE = "VALUE";
    private static final String VALUE_RELATIVE = "VALUE_RELATIVE";

    private SensorResultCallback cb = new SensorResultCallback() {

        @Override
        public void result(float[] f) {
            if (f != null && f.length > 0) {
                value = f[0];
            }
        }
    };

    private BarometerReader baroReader;

    private float value = 0f;

    private float valueRelative = 0f;

    private long valueRelativeSetTime = System.currentTimeMillis();

    public void loadValue(Context ctx) {
        value = IntentTools.getSettings(ctx, BARO_WIDGET_SETTINGS).getFloat(VALUE, 0);
        Log.d(BaroWidgetExtensionService.LOG_TAG, "loaded " + value);
    }

    public void storeValue(Context ctx) {
        Editor e = IntentTools.getSettingsEditor(ctx, BARO_WIDGET_SETTINGS);
        e.putFloat(VALUE, value);
        e.commit();
        Log.d(BaroWidgetExtensionService.LOG_TAG, "stored " + value);
    }

    public void loadValueRelative(Context ctx) {
        valueRelative = IntentTools.getSettings(ctx, BARO_WIDGET_SETTINGS).getFloat(VALUE_RELATIVE, 0);
        Log.d(BaroWidgetExtensionService.LOG_TAG, "loaded " + valueRelative);
    }

    public void storeValueRelative(Context ctx) {
        Editor e = IntentTools.getSettingsEditor(ctx, BARO_WIDGET_SETTINGS);
        e.putFloat(VALUE_RELATIVE, valueRelative);
        e.commit();
        Log.d(BaroWidgetExtensionService.LOG_TAG, "stored " + valueRelative);
    }

    public void start(Context ctx) {
        if (baroReader == null) {
            baroReader = new BarometerReader(ctx, -1, cb);
        }
        baroReader.startReading();
    }

    public void stop() {
        if (baroReader != null) {
            baroReader.stopReading();
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValueRelative() {
        return valueRelative;
    }

    public void setValueRelative(float valueRelative) {
        this.valueRelative = valueRelative;
    }

    public long getValueRelativeSetTime() {
        return valueRelativeSetTime;
    }

    public void setValueRelativeSetTime(long valueRelativeSetTime) {
        this.valueRelativeSetTime = valueRelativeSetTime;
    }

}
