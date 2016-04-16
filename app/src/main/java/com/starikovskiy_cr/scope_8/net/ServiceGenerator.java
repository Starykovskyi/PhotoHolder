package com.starikovskiy_cr.scope_8.net;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by starikovskiy_cr on 10.03.16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = " https://api.parse.com/1/";
    public static String APP_ID_KEY = "X-Parse-Application-Id";
    public static String APP_ID_VALUE = "M8rRCVqnexjEeSX61i2Ah4Yo05XEYs76CkCIXUT0";
    public static String API_KEY = "X-Parse-REST-API-Key";
    public static String API_KEY_VALUE = "7rsDrBPH8WRUcMmXt7rAbVDZrIFcSUjK0yiMwFoB";


    private static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request =  chain.request();
            request = request.newBuilder()
                    .addHeader(APP_ID_KEY, APP_ID_VALUE)
                    .addHeader(API_KEY, API_KEY_VALUE)
                    .build();
            return chain.proceed(request);
        }
    };

    private static OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    private static Retrofit.Builder builder =new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()));

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
