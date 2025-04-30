package com.example.appdelishorder.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.threeten.bp.LocalDateTime;

import java.lang.reflect.Type;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "http://192.168.1.8:7010/api/";  // Đổi thành URL server thực tế
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Tạo interceptor ghi log để debug
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cấu hình OkHttpClient với timeout và logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .dispatcher(new Dispatcher())
                    .build();
            Dispatcher dispatcher = client.dispatcher();
            dispatcher.setMaxRequests(64);  // Số lượng request tối đa
            dispatcher.setMaxRequestsPerHost(5);  // Số lượng request tối đa trên mỗi host

            // Tạo Gson với bộ xử lý ngày tháng tùy chỉnh
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .registerTypeAdapter(org.threeten.bp.LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                        @Override
                        public org.threeten.bp.LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            try {
                                return org.threeten.bp.LocalDateTime.parse(json.getAsString(), org.threeten.bp.format.DateTimeFormatter.ISO_DATE_TIME);
                            } catch (Exception e) {
                                try {
                                    // Thử định dạng khác nếu ISO_DATE_TIME không hoạt động
                                    return org.threeten.bp.LocalDateTime.parse(json.getAsString(), org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                } catch (Exception e2) {
                                    return null;
                                }
                            }
                        }
                    })
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.8:7010/api/") // Đặt URL API của bạn tại đây
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
