package com.example.appdelishorder.Retrofit;

import com.example.appdelishorder.Model.Account;
import com.example.appdelishorder.Model.Category;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Model.LoginRequest;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Model.TokenResponse;
import com.example.appdelishorder.Model.UpdateTokenRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    //Lay thong tin khach hang
    @GET("CustomerApi")
    Call<Customer> getCustomerInformation(@Query("email") String email);

    // Cập nhật thông tin khách hàng
    @PUT("CustomerApi/{id}")
    Call<Customer> updateCustomerInfo(@Path("id") int id, @Body Customer customer);

    // Lấy danh sách danh mục
    @GET("CategoryApi")
    Call<List<Category>> getCategories();

    // Thêm danh mục
    @POST("CategoryApi")
    Call<Category> addCategory(@Body Category category);

    // Lấy danh sách sản phẩm
    @GET("ProductApi/all")
    Call<List<Product>> getProducts();

    // Loc san pham theo danh muc
    @GET("ProductApi/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") String categoryId);

    //  tìm kiếm sản phẩm theo tên
    @GET("ProductApi/search")
    Call<List<Product>> filterProductsByQuery(@Query("keyword") String keyword);

    // API sắp xếp theo giá
    @GET("ProductApi/sortByPrice")
    Call<List<Product>> getProductsSortedByPrice(@Query("direction") String direction);

    // API sắp xếp theo ngày
    @GET("ProductApi/sortByDate")
    Call<List<Product>> getProductsSortedByDate(@Query("direction") String direction);

    //Chi tiet san pham
    @GET("ProductApi/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    @GET("ProductApi/recommendations")
    Call<List<Product>> getRecommendedProducts(@Query("accountId") String accountId);

    // Comment APIs
    @GET("CommentApi/product/{id}")
    Call<List<Comment>> getCommentsByProductId(@Path("id") int productId);

    //Xoa comment
    @DELETE("CommentApi/{id}")
    Call<Void> deleteComment(@Path("id") int commentId);

    // Tạo đơn hàng
    @POST("OrderApi/orders")
    Call<Order> createOrder(@Body Order order);

    //Lay danh sach don hang
    @GET("OrderApi")
    Call<List<Order>> getOrders(@Query("Email") String email, @Query("status") String status);

    // Lấy chi tiết đơn hàng
    @GET("OrderApi/{id}")
    Call<Order> getOrderDetailById(@Path("id") int id);

    // Thêm bình luận sản phẩm
    @POST("CommentApi")
    Call<Comment> addComment(@Body Comment comment);

    @POST("NotificationApi/send")
    Call<Void> sendOrderNotification(@Query("orderId") String orderId, @Query("message") String message);

    // Gui token len server
    @POST("AccountApi/updateFirebaseToken")
    Call<Void> updateFirebaseToken(@Body UpdateTokenRequest request);
}
