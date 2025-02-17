package com.jxp.openapi;

import com.fasterxml.jackson.databind.JsonNode;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * @author jiaxiaopeng
 * Created on 2025-02-14 17:26
 */
public interface DemoClient {

    @GET(value = "https://jsonplaceholder.typicode.com/posts")
    Call<JsonNode> posts();

//    Call<JsonNode> messageSend(@Body String message, @Query("language") String language);
}
