/*
 * Copyright (C) 2013 The CyanogenMod Project (DvTonder)
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
package com.cyanogenmod.lockclock.notification;

import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.cyanogenmod.lockclock.ClockWidgetService;
import com.cyanogenmod.lockclock.R;
import com.cyanogenmod.lockclock.misc.Constants;

public class MissedCallNotification extends Notification {

    private static final String TAG = "MissedCallNotification";
    private static final boolean D = Constants.DEBUG;

    // This needs to be static (unclear why)
    private static int missedCalls = 0;

    public MissedCallNotification(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        getContext().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new NotificationContentObserver(this));
    }

    @Override
    protected void updateNotifications() {
        String[] projection = {
                CallLog.Calls.TYPE,
                CallLog.Calls.NEW
        };
        String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + " = 1";
        Cursor c = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, null);
        if (missedCalls != c.getCount()) {
            missedCalls = c.getCount();
            try {
                ClockWidgetService.getWidgetRefreshIntent(getContext()).send();
            } catch (CanceledException e) {}
        }
        c.close();
    }

    @Override
    public boolean hasNotifications() {
        return missedCalls > 0;
    }

    @Override
    protected Intent getClickIntent() {
        Intent intentCallLog = new Intent(Intent.ACTION_VIEW);
        intentCallLog.setType(CallLog.Calls.CONTENT_TYPE);
        return intentCallLog;
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_lock_idle_phone;
    }

    @Override
    protected String getNotification() {
        return Integer.toString(missedCalls);
    }
}
