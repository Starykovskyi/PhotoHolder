package com.starikovskiy_cr.scope_8.net;

import com.starikovskiy_cr.scope_8.db.models.Photo;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by starikovskiy_cr on 10.03.16.
 */
public interface IParseClient {
    @POST("files/{name}")
    Call<Map<String, String>> uploadPhoto(@Path("name") String name, @Body RequestBody body);

    @POST("classes/Photo")
    Call<Map<String, String>> uploadModel(@Body Photo photo);

    @GET("classes/Photo")
    Call<Map<String, List<Photo>>> getPhotos();
}
