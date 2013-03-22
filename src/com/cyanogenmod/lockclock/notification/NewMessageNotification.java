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
import android.net.Uri;
import android.util.Log;

import com.cyanogenmod.lockclock.ClockWidgetService;
import com.cyanogenmod.lockclock.R;

public class NewMessageNotification extends Notification {

    // there is no official api for sms (!)
    public static final Uri SMS_INBOX_CONTENT_URI = Uri.parse("content://sms/");
    public static final int SMS_INBOX_MESSAGE_TYPE_INBOX = 0x1;
    public static final String SMS_INBOX_CONTENT_TYPE = "vnd.android-dir/mms-sms";
    public static final String SMS_INBOX_TYPE = "type";
    public static final String SMS_INBOX_SEEN = "seen";

    // This needs to be static (unclear why)
    private static int newMessages = 0;

    public NewMessageNotification(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        getContext().getContentResolver().registerContentObserver(SMS_INBOX_CONTENT_URI, true, new NotificationContentObserver(this));
    }

    @Override
    protected void updateNotifications() {
        String[] projection = {
                SMS_INBOX_TYPE,
                SMS_INBOX_SEEN
        };
        String where = SMS_INBOX_TYPE + "=" + SMS_INBOX_MESSAGE_TYPE_INBOX + " AND " + SMS_INBOX_SEEN + " = 0";
        Cursor c = getContext().getContentResolver().query(SMS_INBOX_CONTENT_URI, projection, where, null, null);
        if (newMessages != c.getCount()) {
            newMessages = c.getCount();
            try {
                ClockWidgetService.getWidgetRefreshIntent(getContext()).send();
            } catch (CanceledException e) {}
        }
        Log.v("NewMessageNotification", "Messages: " + newMessages);
        c.close();
    }

    @Override
    public boolean hasNotifications() {
        return newMessages > 0;
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_lock_idle_messages;
    }

    @Override
    protected String getNotification() {
        return Integer.toString(newMessages);
    }

    @Override
    protected Intent getClickIntent() {
        Intent intentSmsInbox = new Intent(Intent.ACTION_MAIN);
        intentSmsInbox.setType(SMS_INBOX_CONTENT_TYPE);
        return intentSmsInbox;
    }
}