package de.uvwxy.barometer.swidget;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private Lock lock = new ReentrantLock();
    
    private SensorResultCallback cb = new SensorResultCallback() {

        @Override
        public void result(float[] f) {
            if (f != null && f.length > 0) {
                value = f[0];
                lock.unlock();
            }
        }
    };

    private BarometerReader baroReader;

    private float value = 0f;

    private float valueRelative = 0f;

    private long valueRelativeSetTime = System.currentTimeMillis();

    private Context mContext = null;

    public BaroLogic(Context ctx) {
        this.mContext = ctx;
    }

    public void loadValue() {
        value = IntentTools.getSettings(mContext, BARO_WIDGET_SETTINGS).getFloat(VALUE, 0);
        Log.d(BaroWidgetExtensionService.LOG_TAG, "loaded " + value);
    }

    public void storeValue() {
        Editor e = IntentTools.getSettingsEditor(mContext, BARO_WIDGET_SETTINGS);
        e.putFloat(VALUE, value);
        e.commit();
        Log.d(BaroWidgetExtensionService.LOG_TAG, "stored " + value);
    }

    public void loadValueRelative() {
        valueRelative = IntentTools.getSettings(mContext, BARO_WIDGET_SETTINGS).getFloat(VALUE_RELATIVE, 0);
        Log.d(BaroWidgetExtensionService.LOG_TAG, "loaded " + valueRelative);
    }

    public void storeValueRelative() {
        Editor e = IntentTools.getSettingsEditor(mContext, BARO_WIDGET_SETTINGS);
        e.putFloat(VALUE_RELATIVE, valueRelative);
        e.commit();
        Log.d(BaroWidgetExtensionService.LOG_TAG, "stored " + valueRelative);
    }

    public float getBlockedValue() {
        if (baroReader == null) {
            // -2 stops reader after every start
            baroReader = new BarometerReader(mContext, -2, cb);
        }
        baroReader.startReading();
        Log.d(BaroWidgetExtensionService.LOG_TAG, "locked for value");
        lock.tryLock();
        Log.d(BaroWidgetExtensionService.LOG_TAG, "unlocked");

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
