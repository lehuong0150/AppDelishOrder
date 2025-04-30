package com.example.appdelishorder.View.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Adapter.adapterOrderDetail;
import com.example.appdelishorder.Contract.orderContract;
import com.example.appdelishorder.Contract.productDetailContract;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Presenter.orderPresenter;
import com.example.appdelishorder.Presenter.productDetailPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.OrderStatusUtil;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity implements orderContract.View, productDetailContract.View {

    private TextView tvTitle, tvProductCount, tvSubtotal, tvDeliveryFee, tvTotal, tvPaymentMethod, tvDateOrder, btnBackMenu, tvStatus;
    private EditText etAddress, etPhone;
    private RecyclerView rvProducts;
    private orderContract.Presenter presenterOrder;
    private productDetailContract.Presenter presenterProduct;
    private adapterOrderDetail productAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        // Initialize views
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.ivStatus);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvDateOrder = findViewById(R.id.tv_DateOrder);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotal = findViewById(R.id.tvTotal);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnBackMenu = findViewById(R.id.btnBackMenu);
        rvProducts = findViewById(R.id.rvProducts);

        // Initialize presenters
        presenterOrder = new orderPresenter(this);
        presenterProduct = new productDetailPresenter(this);
        // Retrieve ORDER_ID from Intent
        int orderId = getIntent().getIntExtra("ORDER_ID", 0);
        if (orderId != 0) {
            // Load order details using the order ID
            presenterOrder.loadOrderDetails(orderId);
        } else {
            // Handle error: Order ID not found
            Toast.makeText(this, "ID khong tim thay", Toast.LENGTH_SHORT).show();
        }

        //set click back menu
        btnBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void displayProductDetails(Product product) {

    }

    @Override
    public void displayQuantity(int quantity) {

    }

    @Override
    public void displayTotalPrice(float price) {

    }

    @Override
    public void displayOrders(List<Order> orders) {

    }

    @Override
    public void displayOrderDetails(Order orderDetails) {
        // Set order details to the views
        tvTitle.setText("Order ID: " + orderDetails.getId());
        tvDateOrder.setText(orderDetails.getRegTime());
        tvProductCount.setText(orderDetails.getOrderDetails().size() + " sản phẩm");
        tvStatus.setText(OrderStatusUtil.getStatusName(orderDetails.getStatus()));
        tvSubtotal.setText(orderDetails.getTotalPrice() + " VND");
        tvDeliveryFee.setText("16.000 VND");
        tvTotal.setText((orderDetails.getTotalPrice() + 16.000) + " VND");
        tvPaymentMethod.setText(orderDetails.getPaymentMethod());

        // Set address and phone number
        etAddress.setText(orderDetails.getShippingAddress());
        etPhone.setText(orderDetails.getPhone());
        tvTitle.setText(orderDetails.getNameCustomer());

        // Set up RecyclerView for product details
        List<OrderDetail> productList = orderDetails.getOrderDetails();
        productAdapter = new adapterOrderDetail(this, productList, false);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(productAdapter);
    }

    @Override
    public void onOrderSuccess(Order order) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showAddedToCartSuccess() {

    }
}