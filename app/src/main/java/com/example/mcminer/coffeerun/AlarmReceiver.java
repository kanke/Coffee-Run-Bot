package com.example.mcminer.coffeerun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kanke on 28/09/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("shuffTest","I Arrived!!!!");
        //Toast.makeText(context, "Alarm worked!!", Toast.LENGTH_LONG).show();

        Bundle answerBundle = intent.getExtras();
        int userAnswer = answerBundle.getInt("userAnswer");
        if(userAnswer == 1)
        {
            Log.v("shuffTest","Pressed YES");
        }
        else if(userAnswer == 2)
        {
            Log.v("shuffTest","Pressed NO");
        }
        else if(userAnswer == 3)
        {
            Log.v("shuffTest","Pressed MAYBE");
        }

    }


}
