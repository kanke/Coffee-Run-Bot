package com.example.mcminer.coffeerun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kanke on 28/09/2016.
 */

public class SendMessage extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "IN Sendmessage");

        Intent myIntent;

        SlackApi slackApi;

        myIntent = new Intent();

        myIntent.setAction(Intent.ACTION_VIEW);
        myIntent.setData(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hooks.slack.com/services/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        slackApi = retrofit.create(SlackApi.class);

        MessageDetails payload = new MessageDetails();

        payload.setText("This is a line of text.\nAnd this is another one.");

        Call call = slackApi.sendToken(payload);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d(TAG, "Success!" + call.toString() + "Response" + response.message());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, "Call failed! call"+ call.toString());
                t.printStackTrace();
            }
        });



    }


}
