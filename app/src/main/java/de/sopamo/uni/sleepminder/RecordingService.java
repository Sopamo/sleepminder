package de.sopamo.uni.sleepminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import de.sopamo.uni.sleepminder.activities.MainActivity;
import de.sopamo.uni.sleepminder.storage.FileHandler;

public class RecordingService extends Service {

    private final int ONGOING_NOTIFICATION_ID = 1;
    public static RecordingService instance;

    public RecordingService() {
        instance = this;
    }

    @Override
    public void onDestroy() {
        MyApplication.recorder.stop(MyApplication.context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Show the "we are tracking" notification
        startForeground(ONGOING_NOTIFICATION_ID, getNotification());

        // Start the tracker
        MyApplication.recorder.start(MyApplication.context, new FileHandler());

        return START_STICKY;
    }

    private Notification getNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.launcher_icon)
                        .setContentTitle("SleepMinder is active")
                        .setContentText("Sleep well :)");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setProgress(0,0,true);
        mBuilder.setCategory(NotificationCompat.CATEGORY_PROGRESS);

        mBuilder.setOngoing(true);

        PendingIntent stopTrackingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.addAction(R.drawable.ic_action_stop,"Stop tracking", stopTrackingIntent);

        return mBuilder.build();
    }
}
