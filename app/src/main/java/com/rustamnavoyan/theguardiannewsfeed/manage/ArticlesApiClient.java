package com.rustamnavoyan.theguardiannewsfeed.manage;


import com.rustamnavoyan.theguardiannewsfeed.models.data.PageResponse;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ArticlesApiClient {
    interface ArticlesService {

        @GET("/search")
        Call<PageResponse> getArticleList(@Query("api-key") String apiKey,
                                          @Query("show-fields") String showFields,
                                          @Query("page") int page, @Query("page-size") int pageSize);
    }

    private static class ServiceGenerator {
        private ArticlesService mService;
        private OkHttpClient mOkHttpClient;

        private static ServiceGenerator instance = new ServiceGenerator();

        public static ServiceGenerator getInstance() {
            return instance;
        }

        private ServiceGenerator() {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .followRedirects(false)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        Request.Builder builder = originalRequest.newBuilder()
                                .header("User-Agent", USER_AGENT)
                                .header("Accept-Charset", CHARSET);

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(new LoggingInterceptor());

            mOkHttpClient = okHttpBuilder.build();
        }

        private synchronized ArticlesService createService() {
            if (mService == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(mOkHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                mService = retrofit.create(ArticlesService.class);
            }

            return mService;
        }
    }

    public interface ResponseCallback {
        void onResponse(PageResponse response);
    }

    private static final String BASE_URL = "https://content.guardianapis.com";

    private static final String API_KEY = "0b731f9c-1cd1-438a-952d-6a02c8a78bf2";

    private static final String CHARSET = "UTF-8";
    private static final String USER_AGENT = "NewsFeed";

    private static final int CONNECTION_TIMEOUT = 25000;

    private ServiceGenerator mServiceGenerator;

    public ArticlesApiClient() {
        mServiceGenerator = ServiceGenerator.getInstance();
    }

    public void getArticleList(int page, int pageSize, ResponseCallback callback) {
        mServiceGenerator.createService()
                .getArticleList(API_KEY, "thumbnail", page, pageSize).enqueue(new Callback<PageResponse>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse> call, @NonNull Response<PageResponse> response) {
                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse> call, Throwable t) {
            }
        });
    }
}
