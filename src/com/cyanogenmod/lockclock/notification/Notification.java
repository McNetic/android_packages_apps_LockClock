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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.cyanogenmod.lockclock.R;

public abstract class Notification extends Handler {

    private Context mContext;

    public Notification(Context context) {
        mContext = context;
        onCreate();
        updateNotifications();
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public void handleMessage(Message msg) {
        Log.v("Notification", "handleMessage()");
        updateNotifications();
    }

    /**
     * Called from constructor. Set up event listeners here.
     */
    protected void onCreate() {};

    /**
     * Update the notification status
     * 
     * Implement the actual notification querying in this method. Will be called
     * automatically from constructor (after onCreate) and from ContentObserver
     * if present.
     */
    protected abstract void updateNotifications();


    /**
     * Query if notifications are available (and item should be displayed)
     * 
     * @return boolean indicating availabe notifications
     */
    public abstract boolean hasNotifications();

    /**
     * Get the icon displayed before the notification
     * 
     * @return resource id
     */
    protected abstract int getIcon();

    /**
     * Get the click intent for the notification. Return null if item is not
     * clickable
     * 
     * @return click intent for the notification, or null
     */
    protected Intent getClickIntent() {
        return null;
    }

    /**
     * Get the text displayed as notification (usually a count)
     * 
     * @return notification display text
     */
    protected abstract String getNotification();

    public void updateView(RemoteViews itemViews) {
        // set icon
        itemViews.setTextViewCompoundDrawablesRelative(R.id.notification_item, getIcon(), 0, 0, 0);
        // add count
        itemViews.setTextViewText(R.id.notification_item, getNotification());

        final Intent intentNotification = getClickIntent();
        if (intentNotification != null) {
            // set click intent if available
            final PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
            itemViews.setOnClickPendingIntent(R.id.notification_item, pi);
        }
    }

    public static RemoteViews createItemView(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.notification_item);
    }
}

class NotificationContentObserver extends ContentObserver {

    private Handler mHandler;

    public NotificationContentObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.v("NotificationObserver", "NotificationObserver.onChange( " + selfChange + ")");
        mHandler.dispatchMessage(new Message());
    }
}