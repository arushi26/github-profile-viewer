package com.example.githubprofileviewer.data;

import com.example.githubprofileviewer.data.model.Profile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ApiRequestInterface {

    @Headers({"Content-Type: application/json",
            "User-Agent: Github-Profile-Viewer-App"})
    @GET("/users/{username}")
    Call<Profile> getProfile(@Path("username") String username);
}