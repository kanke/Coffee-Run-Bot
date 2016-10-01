package com.example.mcminer.practice;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;

public class MainActivity extends Activity implements BootstrapNotifier {

    private static final String CUSTOM_INTENT = "Custom_intent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((mcminerApp) getApplication()).setmainactivity(this);
        // stop listening to touches
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // remove the icon from App Drawer
        // PackageManager p = getPackageManager();
        // ComponentName componentName = new ComponentName(this, com.example.mcminer.practice.MainActivity.class);
        // p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);


   /*     Intent myIntent;

        myIntent = new Intent();

        myIntent.setAction(Intent.ACTION_VIEW);
        myIntent.setData(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); */


    }

 /*   public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    public void cancelNotification(int notificationId) {

        if (Context.NOTIFICATION_SERVICE != null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
            nMgr.cancel(notificationId);
        }
    }

    @Override
    public void didEnterRegion(Region region) {

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        PendingIntent pIntent2 = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);


        // Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://alchemytec.slack.com/messages/16/"));
        //Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("slack://16?id=C0PT94S1X&team=T02GQ0BEQ&message=test"));

        String finalUrl = "javascript:" +
                "var to = 'https://hooks.slack.com/services/XXXXXXXXX/XXXXXXXXX/XXXXXXXXXXXXXXXXXXXXXXXX';" +
                "var p = payload={username: 'Coffee Bot',text: 'Your coffee has arrived!\nPlease come downstairs to pick it up.',icon_emoji: ':coffee_bot:', channel: '#16'};"+
                "var myForm = document.createElement('form');" +
                "myForm.method='post' ;" +
                "myForm.action = to;" +
                "for (var k in p) {" +
                "var myInput = document.createElement('input') ;" +
                "myInput.setAttribute('type', 'text');" +
                "myInput.setAttribute('name', k) ;" +
                "myInput.setAttribute('value', p[k]);" +
                "myForm.appendChild(myInput) ;" +
                "}" +
                "document.body.appendChild(myForm) ;" +
                "myForm.submit() ;" +
                "document.body.removeChild(myForm) ;";
        Log.d("finalurl", finalUrl);
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));


        Log.d("Uri.parse(finalUrl)", String.valueOf(Uri.parse(finalUrl)));
        // Intent notificationIntent = new Intent(MainActivity.this, SendMessage.class);


        // notificationIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent, 0);

        //Yes intent
//        Intent yesReceive = new Intent();
//        yesReceive.setAction(CUSTOM_INTENT);
//        Bundle yesBundle = new Bundle();
//        yesBundle.putInt("userAnswer", 1);//This is the value I want to pass
//        yesReceive.putExtras(yesBundle);
//        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentrt = new Intent(this, AlarmReceiver.class);
        PendingIntent pIntentrt = PendingIntent.getBroadcast(this, 0, intentrt, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("Coffee Run!")
                .setContentText("Are you back from coffee run?")
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setSmallIcon(R.drawable.coffee_cup)
                .setContentIntent(pIntent)
                .setSound(soundUri)

                 .addAction(R.drawable.yesfinal, "Yes", pIntent)
                .addAction(R.drawable.cancelfinal, "No", pIntent2)


                //.addAction(R.drawable.yesfinal, "Yes", pIntentrt)

        .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);


    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
