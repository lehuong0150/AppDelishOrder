package com.example.appdelishorder.Presenter;

import android.util.Log;

import com.example.appdelishorder.Contract.accountContract;
import com.example.appdelishorder.Model.Account;
import com.example.appdelishorder.Model.LoginRequest;
import com.example.appdelishorder.Model.TokenResponse;
import com.example.appdelishorder.Retrofit.ApiService;
import com.example.appdelishorder.Retrofit.APIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class accountPresenter implements accountContract.Presenter {

    private accountContract.View view;
    private ApiService apiService;

    public accountPresenter(accountContract.View view) {
        this.view = view;
        this.apiService = APIClient.getClient().create(ApiService.class); // Khởi tạo API service để gọi API
    }

    @Override
    public void doLogin(String email, String password) {
        view.showLoading();

        // Tạo request login
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Gọi API login
        apiService.loginAccount(loginRequest).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    // Đăng nhập thành công
                    String token = response.body().getToken();
                    view.showLoginSuccess("Đăng nhập thành công. Token: " + token);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Lấy chuỗi lỗi từ phản hồi của server
                            String errorResponse = response.errorBody().string();

                            // Kiểm tra xem phản hồi có phải JSON hợp lệ hay không
                            if (isValidJSON(errorResponse)) {
                                JSONObject jsonObject = new JSONObject(errorResponse);
                                String errorMessage = jsonObject.optString("error", "Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản/mật khẩu.");
                                view.showLoginError(errorMessage);
                            } else {
                                // Nếu phản hồi không phải là JSON
                                view.showLoginError("Lỗi từ server: " + errorResponse);
                            }
                        } else {
                            view.showLoginError("Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản/mật khẩu.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        view.showLoginError("Đã xảy ra lỗi khi xử lý phản hồi từ server.");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                view.hideLoading();
                // Xử lý lỗi từ phía kết nối (ví dụ không kết nối được tới server)
                view.showLoginError("Không thể kết nối tới server. Vui lòng kiểm tra kết nối mạng.");
            }
        });

    }

    @Override
    public void doRegister(String email, String password, String fullName) {
        view.showLoading();

        Account newAccount = new Account(email, password, fullName);
        // Log dữ liệu trước khi gửi
        Log.d("Register Data", "Email: " + email + ", Password: " + password + ", FullName: " + fullName);
        Call<Account> call = apiService.registerAccount( newAccount);
       call.enqueue(new Callback<Account>() {
           @Override
           public void onResponse(Call<Account> call, Response<Account> response) {
               view.hideLoading();
               if (response.isSuccessful()) {
                   view.showRegisterSuccess("Account registered successfully");
               } else {
                   view.showRegisterError("Registration failed: " + response.message());
                   Log.e("Register Error", "Response code: " + response.code() + " - Message: " + response.message());
               }
           }

           @Override
           public void onFailure(Call<Account> call, Throwable t) {
               view.hideLoading();
               view.showRegisterError(t.getMessage());
               Log.e("Register Error", "Request failed: " + t.getMessage());
           }
       });
    }
    // doi mat khau
    @Override
    public void doChangePassword(String email, String oldPassword, String newPassword) {
        view.showLoading();
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("oldPassword", oldPassword);
        data.put("newPassword", newPassword);

        apiService.changePassword(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                view.hideLoading();
                if (response.isSuccessful()) {
                    view.showChangePasswordSuccess("Đổi mật khẩu thành công.");
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Đổi mật khẩu thất bại.";
                        view.showChangePasswordError(errorMsg);
                    } catch (IOException e) {
                        view.showChangePasswordError("Lỗi xử lý phản hồi.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                view.hideLoading();
                view.showChangePasswordError("Không thể kết nối tới server.");
            }
        });
    }


    // Hàm để kiểm tra xem chuỗi có phải là JSON hợp lệ không
    private boolean isValidJSON(String jsonString) {
        try {
            new JSONObject(jsonString);  // Thử tạo đối tượng JSON
            return true;  // Nếu không có lỗi, chuỗi là JSON hợp lệ
        } catch (JSONException e) {
            return false;  // Nếu có lỗi, chuỗi không hợp lệ
        }
    }

}
