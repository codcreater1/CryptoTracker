package com.example.cryptotracker.controller;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Singleton factory that provides a configured {@link Retrofit} instance
 * and the {@link ApiService} proxy.
 *
 * Using a singleton ensures a single HTTP client is reused across the app,
 * which is efficient for connection pooling.
 */
public class RetrofitClient {

    /** CoinGecko public API – no API key required for basic endpoints. */
    private static final String BASE_URL = "https://api.coingecko.com/api/v3/";

    private static RetrofitClient instance;
    private final ApiService apiService;

    /**
     * Private constructor – builds and caches the Retrofit + OkHttp stack.
     */
    private RetrofitClient() {
        // Log HTTP request/response bodies in debug builds
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * Returns the singleton instance, creating it on first call (lazy init).
     *
     * @return The single {@link RetrofitClient} instance.
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * Exposes the generated {@link ApiService} implementation.
     *
     * @return Ready-to-use API service.
     */
    public ApiService getApiService() {
        return apiService;
    }
}
