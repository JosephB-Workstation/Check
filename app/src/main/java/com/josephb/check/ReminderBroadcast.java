package com.josephb.check;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent activity = new Intent(context, Login_Activity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activity, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "due")
                .setSmallIcon(R.drawable.ischecked)
                .setContentTitle(intent.getStringExtra("listName"))
                .setContentText("The due date for the task " + intent.getStringExtra("taskName") +" has passed.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setGroup("dueTasks");


        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, "due")
                .setSmallIcon(R.drawable.ischecked)
                .setStyle(new NotificationCompat.InboxStyle()
                .addLine("Some tasks have passed their due dates!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setGroup("dueTasks")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true);


        NotificationManagerCompat notification = NotificationManagerCompat.from(context);
        notification.notify(intent.getIntExtra("id", 0), builder.build());
        notification.notify(-1, summaryBuilder.build()) ;
    }
}
