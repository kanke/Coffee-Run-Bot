package com.example.mcminer.practice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by kanke on 28/09/2016.
 */

public interface SlackApi {

    @POST("T02GQ0BEQ/B2H12KYJU/qe7xXDLvPzvgDAUzovKCXZ50")
    Call<String> sendToken(@Body MessageDetails payload);
}
