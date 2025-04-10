package com.example.appdelishorder.Retrofit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "http://192.168.1.6:7010/api/";  // Đổi thành URL server thực tế
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .dispatcher(new Dispatcher())
                    .build();
            Dispatcher dispatcher = client.dispatcher();
            dispatcher.setMaxRequests(64);  // Số lượng request tối đa
            dispatcher.setMaxRequestsPerHost(5);  // Số lượng request tối đa trên mỗi host

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.6:7010/api/") // Đặt URL API của bạn tại đây
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
