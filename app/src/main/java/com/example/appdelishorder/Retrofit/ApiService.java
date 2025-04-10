package com.example.appdelishorder.Retrofit;

import com.example.appdelishorder.Model.Account;
import com.example.appdelishorder.Model.Category;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.LoginRequest;
import com.example.appdelishorder.Model.Orders;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Model.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Đăng ký tài khoản
    @POST("AccountApi/register")
    Call<Account> registerAccount(@Body Account account);

    // Lấy thông tin tài khoản dựa trên email (hoặc accountId)
    @GET("AccountApi/{email}")
    Call<Account> getAccountByEmail(@Path("email") String email);

    // Đăng nhập tài khoản
    @POST("AccountApi/login")
    Call<TokenResponse> loginAccount(@Body LoginRequest loginRequest);

    // Lấy danh sách danh mục
    @GET("CategoryApi")
    Call<List<Category>> getCategories();

    // Thêm danh mục
    @POST("CategoryApi")
    Call<Category> addCategory(@Body Category category);

    // Lấy danh sách sản phẩm
    @GET("ProductApi")
    Call<List<Product>> getProducts();

    // Loc san pham theo danh muc
    @GET("ProductApi/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") String categoryId);

    //  tìm kiếm sản phẩm theo tên
    @GET("ProductApi/search")
    Call<List<Product>> filterProductsByQuery(@Query("keyword") String keyword);

    // Tạo đơn hàng
    @POST("orders")
    Call<Orders> createOrder(@Body Orders order);

    // Lấy chi tiết đơn hàng
    @GET("orders/{id}")
    Call<Orders> getOrderById(@Path("id") int id);

    // Thêm bình luận sản phẩm
    @POST("comments")
    Call<Comment> addComment(@Body Comment comment);

    // Lấy danh sách bình luận cho sản phẩm
    @GET("comments/{product_id}")
    Call<List<Comment>> getCommentsByProductId(@Path("product_id") int productId);
}
