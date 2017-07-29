package com.hydro.library;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
class notification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "JSCST";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of  notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     */

    static void notify(final Context context, int counter, String FullPath, String openPath, String FileName,
                       String exampleString) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.notificationdownload);
        final String title;
        if (FileName != null) {
            title = res.getString(
                    R.string.notititle);
        } else {
            title = res.getString(R.string.notititle) + ": " + res.getString(R.string.dofanoti);
        }




        Intent intent = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("path", FullPath);
        intent.putExtra("FileName", FileName);
        intent.putExtra("openPath", openPath);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,counter, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                        // Set required fields, including the small icon, the
                        // notification title, and text.
                .setSmallIcon(R.drawable.notificationdownload)
                .setContentTitle(title)
                .setContentText(exampleString)

                        // All fields below this line are optional.

                        // Use a default priority (recognized on devices running Android
                        // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        // Provide a large icon, shown with the notification in the
                        // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                        // If this notification relates to a past or upcoming event, you
                        // should set the relevant time information using the setWhen
                        // method below. If this call is omitted, the notification's
                        // timestamp will by set to the time at which it was shown.
                        // TODO: Call setWhen if this notification relates to a past or
                        // upcoming event. The sole argument to this method should be
                        // the notification timestamp in milliseconds.
                        //.setWhen(...)

                        // Set the pending intent to be initiated when the user touches

                       // the notification.
                .setContentIntent(pendingIntent)
                .setDeleteIntent(getDeletedIntent(context,counter,FullPath,FileName))


                // Show expanded text content on devices running Android 4.1 or
                        // later.
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(exampleString)
                                .setBigContentTitle(title)
                                .setSummaryText(context.getString(R.string.devby)))


                                // Example additional actions for this notification. These will
                                // only show on devices running Android 4.1 or later, so you
                                // should ensure that the activity in this notification's
                                // content intent provides access to the same actions in
                                // another way.
                                // Automatically dismiss the notification when it is touched.
                        .setAutoCancel(true)
                        .setNumber(counter);


        notify(context, builder.build(), counter);
    }

    protected static PendingIntent getDeletedIntent(Context context, int counter, String path, String FileName) {
        if (FileName == null) {
            Intent intent = new Intent(context, NotificationBrodcastRecevie.class);
            intent.setAction("Canceled");
            intent.putExtra("FilePath", path);
            return PendingIntent.getBroadcast(context, counter, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        else
            return null;
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification,int counter) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG,counter,notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }
}
