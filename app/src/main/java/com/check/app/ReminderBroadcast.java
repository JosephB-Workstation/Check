package com.check.app;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent internal = new Intent(context, ReminderBroadcast.class);
      //  intent.setFlags(())
        PendingIntent internalPendingIntent = PendingIntent.getActivity(context,0, internal, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "due")
                .setSmallIcon(R.drawable.ischecked)
                .setContentTitle(intent.getStringExtra("listName"))
                .setContentText("The due date for the task " + intent.getStringExtra("taskName") +" has passed.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(internalPendingIntent);

        NotificationManagerCompat notification = NotificationManagerCompat.from(context);
        notification.notify(intent.getIntExtra("id", 0), builder.build());
    }
}
