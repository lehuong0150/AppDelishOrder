package com.example.appdelishorder.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Adapter.adapterComment;
import com.example.appdelishorder.Contract.commentContract;
import com.example.appdelishorder.Contract.productDetailContract;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Presenter.commentPresenter;
import com.example.appdelishorder.Presenter.productDetailPresenter;
import com.example.appdelishorder.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity implements commentContract.View, productDetailContract.View {

    // UI components
    private ImageView imgProduct;
    private TextView tvProductName, tvProductPrice, tvProductDescription, numTxt, txtTotal;
    private RecyclerView rcvComments;
    private ImageButton minusBtn, plusBtn, btnBack;
    private AppCompatButton btnAddToCart;
    private RatingBar ratingBar;
    private ProgressBar progressBar;

    // Presenters
    private commentPresenter presenterComment;
    private productDetailPresenter presenterProductDetail;

    // Adapter and data
    private adapterComment commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private Product currentProduct;

    // State variables
    private int productId;
    private int quantity = 1; // Default quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        AndroidThreeTen.init(this);

        // Get product ID from intent
        productId = getIntent().getIntExtra("PRODUCT_ID", 0);
        if (productId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupPresenters();
        setupRecyclerView();
        setupClickListeners();
        loadProductDetails();
    }

    private void initViews() {
        // Find and initialize all UI components
        imgProduct = findViewById(R.id.picProduct);
        tvProductName = findViewById(R.id.txtNameProduct);
        tvProductPrice = findViewById(R.id.txtPrice);
        tvProductDescription = findViewById(R.id.descriptionTxt);
        ratingBar = findViewById(R.id.ratingBar);
        rcvComments = findViewById(R.id.view_comment);
        btnBack = findViewById(R.id.btnBack);
        minusBtn = findViewById(R.id.minusBtn);
        numTxt = findViewById(R.id.numTxt);
        plusBtn = findViewById(R.id.plusBtn);
        txtTotal = findViewById(R.id.txt_total);
        btnAddToCart = findViewById(R.id.btn_addCart);
        progressBar = findViewById(R.id.progressBar);

        // Set default quantity
        numTxt.setText(String.valueOf(quantity));
    }

    private void setupPresenters() {
        presenterComment = new commentPresenter(this);
        presenterProductDetail = new productDetailPresenter(this);
    }

    private void setupRecyclerView() {
        commentAdapter = new adapterComment(this, commentList);
        rcvComments.setLayoutManager(new LinearLayoutManager(this));
        rcvComments.setAdapter(commentAdapter);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Add to cart button
        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                addToCart();
            } else {
                showError("Không thể thêm vào giỏ hàng. Vui lòng thử lại sau.");
            }
        });

        // Quantity adjustment buttons
        minusBtn.setOnClickListener(v -> decreaseQuantity());
        plusBtn.setOnClickListener(v -> increaseQuantity());
    }

    private void decreaseQuantity() {
        if (currentProduct != null && quantity > 1 ) {
            quantity--;
            numTxt.setText(String.valueOf(quantity));
            updateTotalPrice();
            minusBtn.setEnabled(quantity > 1);
        }
    }

    private void increaseQuantity() {
        if (currentProduct != null && quantity < currentProduct.getQuantity()) {
            quantity++;
            numTxt.setText(String.valueOf(quantity));
            minusBtn.setEnabled(true);
            updateTotalPrice();
        } else if (currentProduct != null) {
            showError("Số lượng tối đa là " + currentProduct.getQuantity());
        }
    }

    private void updateTotalPrice()
    {
        if (currentProduct != null)
        {
            float total = currentProduct.getPrice() * quantity;
            txtTotal.setText(String.format("%.2f VNĐ", total));
        }
    }

    private void loadProductDetails() {
        showLoading(true);
        presenterProductDetail.loadProductDetails(productId);
    }

    private void addToCart() {
        // Check if quantity exceeds available stock
        if (quantity > currentProduct.getQuantity()) {
            showError("Số lượng vượt quá số lượng có sẵn trong kho!");
            return;
        }

        Toast.makeText(this, "Đã thêm " + quantity + " " + currentProduct.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
        intent.putExtra("PRODUCT_ID", currentProduct.getId());
        intent.putExtra("QUANTITY", quantity);
        startActivity(intent);
    }


    @Override
    public void displayProductDetails(Product product) {
        if (product == null) {
            showError("Không tìm thấy thông tin sản phẩm");
            return;
        }

        currentProduct = product;

        // Update UI with product details
        tvProductName.setText(product.getName());
        tvProductDescription.setText(product.getDescript());
        tvProductPrice.setText(String.format("%.2f VNĐ", product.getPrice()));

        // Update UI based on product availability
        updateProductAvailability(product.getQuantity());

        // Update total price
        updateTotalPrice();

        // Load product image
        loadProductImage(product.getImageProduct());

        // Reset rating until comments are loaded
        ratingBar.setRating(0);

        // Load comments for this product
        presenterComment.getCommentsByProductId(productId);
    }

    private void updateProductAvailability(int stockQuantity) {
        boolean isAvailable = stockQuantity > 0;

        btnAddToCart.setEnabled(isAvailable);
        btnAddToCart.setText(isAvailable ? "Thêm vào giỏ " : "Hết hàng");
        plusBtn.setEnabled(isAvailable);
        minusBtn.setEnabled(isAvailable && quantity > 1);
    }

    private void loadProductImage(String imageUrl) {
        // Check if ImageView is null
        if (imgProduct == null) {
            Log.e("ProductDetailActivity", "ImageView is null");
            return;
        }

        // Check if Activity is still valid (not destroyed)
        if (isFinishing() || isDestroyed()) {
            Log.e("ProductDetailActivity", "Activity is finishing or destroyed");
            return;
        }

        // Check if URL is valid
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Use ApplicationContext for safer loading
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.avt)
                        .error(R.drawable.avt)
                        .into(imgProduct);
            } catch (Exception e) {
                Log.e("ProductDetailActivity", "Error loading image: " + e.getMessage());
                // Fallback - set placeholder directly if Glide fails
                imgProduct.setImageResource(R.drawable.avt);
            }
        } else {
            // Set placeholder if URL is invalid
            imgProduct.setImageResource(R.drawable.avt);
        }
    }

    @Override
    public void displayQuantity(int quantity) {
        this.quantity = quantity;
        numTxt.setText(String.valueOf(quantity));
        updateTotalPrice();
    }

    @Override
    public void displayTotalPrice(float price) {
        txtTotal.setText(String.format("%.2f VNĐ", price));
    }

    @Override
    public void showAddedToCartSuccess() {
        Toast.makeText(this, "Đã thêm " + quantity + " " + currentProduct.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // Implementation of commentContract.View methods

    @Override
    public void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showLoading() {
        showLoading(true);
    }

    @Override
    public void hideLoading() {
        showLoading(false);
    }

    @Override
    public void showComments(List<Comment> comments) {
        commentList.clear();

        if (comments != null && !comments.isEmpty()) {
            commentList.addAll(comments);
            calculateAverageRating(comments);
        } else {
            showEmptyComments();
        }

        commentAdapter.notifyDataSetChanged();
    }

    private void calculateAverageRating(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return;

        float totalRating = 0;
        for (Comment comment : comments) {
            totalRating += comment.getEvaluate();
        }

        float averageRating = totalRating / comments.size();
        updateProductRating(averageRating);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyComments() {
        commentList.clear();
        commentAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Chưa có đánh giá nào cho sản phẩm này", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProductRating(float rating) {
        ratingBar.setRating(rating);
    }

    @Override
    public void onCommentSubmitted() {

    }

    @Override
    protected void onDestroy() {
        // Detach presenters to prevent memory leaks
        if (presenterComment != null) {
            presenterComment.onDetach();
        }

        if (presenterProductDetail != null) {
            presenterProductDetail.detachView();
        }

        super.onDestroy();
    }
}
