/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2014, Sony Mobile Communications AB

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

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.sonyericsson.extras.liveware.extension.util.registration.WidgetContainer;

/**
 * Provides information needed during extension registration.
 */
public class BaroWidgetRegistrationInformation extends RegistrationInformation {

    final Context mContext;
    private String extensionKey;
    private static final String EXTENSION_KEY_PREF = "EXTENSION_KEY_PREF";


    /**
     * Creates a widget registration object.
     *
     * @param context The context.
     */
    protected BaroWidgetRegistrationInformation(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        mContext = context;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        // This extension supports all accessories from Widget API level 3 and
        // up.
        return 3;
    }

    @Override
    public int getTargetWidgetApiVersion() {
        return 3;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return API_NOT_REQUIRED;
    }

    /**
     * Checks if the widget size is supported.
     *
     * @param width The widget width.
     * @param height The widget height.
     * @return True if the widget size is supported.
     */
    @Override
    public boolean isWidgetSizeSupported(final int width, final int height) {
        return true;
    }

    // This method must be implemented to register API level 3 widgets.
    @SuppressWarnings("unchecked")
    @Override
    protected WidgetClassList getWidgetClasses(Context context, String hostAppPackageName,
            WidgetContainer widgetContainer) {
        // Return the widget to register.
        return new WidgetClassList(BaroWidget.class);
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
    	Log.d("UVWXY", " REG REG REG REG REG REG REG REG REG REG REG REG REG REG REG");
        String iconHostapp = ExtensionUtils.getUriString(mContext, R.drawable.icon);
        String iconExtension = ExtensionUtils.getUriString(mContext, R.drawable.icon_extension);

        ContentValues values = new ContentValues();

        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                BaroWidgetPreferenceActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT,
                mContext.getString(R.string.configuration_text));
        values.put(Registration.ExtensionColumns.NAME, mContext.getString(R.string.extension_name));
        values.put(Registration.ExtensionColumns.EXTENSION_KEY, getExtensionKey());
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName());

        return values;
    }

    /**
     * A basic implementation of getExtensionKey
     * Returns and saves a random string based on UUID.randomUUID()
     *
     * Note that this implementation doesn't guarantee random numbers on Android 4.3 and older. See <a href="https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html">https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html</a>
     *
     * @return A saved key if it exists, otherwise a randomly generated one.
     * @see com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation#getExtensionKey()
     */
    @Override
    public synchronized String getExtensionKey() {
        if (TextUtils.isEmpty(extensionKey)) {
            // Retrieve key from preferences
            SharedPreferences pref = mContext.getSharedPreferences(EXTENSION_KEY_PREF,
                    Context.MODE_PRIVATE);
            extensionKey = pref.getString(EXTENSION_KEY_PREF, null);
            if (TextUtils.isEmpty(extensionKey)) {
                // Generate a random key if not found
                extensionKey = UUID.randomUUID().toString();
                pref.edit().putString(EXTENSION_KEY_PREF, extensionKey).commit();
            }
        }
        return extensionKey;
    }

}
