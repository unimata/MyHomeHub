package com.homehub.dragan.myhomehub.Classes.provider;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.LogSheet;
import com.homehub.dragan.myhomehub.Classes.model.rest.BootstrapResponse;
import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestAPIService {

    @GET("/api/bootstrap")
    Call<BootstrapResponse> bootstrap(@Header("Authorization") String password);

    @GET("/api/bootstrap")
    Call<String> rawBootstrap(@Header("Authorization") String password);

    @GET("/api/states")
    Call<String> rawStates(@Header("Authorization") String password);

    @GET("/api/states")
    Call<ArrayList<Entity>> getStates(@Header("Authorization") String password);

    @GET("/api/states/{entityId}")
    Call<Entity> getState(@Header("Authorization") String password, @Path("entityId") String entityId);

    @POST("/api/services/{domain}/{service}")
    Call<ArrayList<Entity>> callService(@Header("Authorization") String password, @Path("domain") String domain, @Path("service") String service, @Body CallServiceRequest json);

    @GET("/api/history/period")
    Call<ArrayList<ArrayList<Entity>>> getHistory(@Header("Authorization") String password, @Query("filter_entity_id") String entityId);

    @GET("/api/logbook/{timestamp}")
    Call<ArrayList<LogSheet>> getLogbook(@Header("Authorization") String password, @Path("timestamp") String domain);

}
