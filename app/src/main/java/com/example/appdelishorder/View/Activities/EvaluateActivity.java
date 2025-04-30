package com.example.appdelishorder.View.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Contract.commentContract;
import com.example.appdelishorder.Contract.productContract;
import com.example.appdelishorder.Contract.productDetailContract;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Presenter.commentPresenter;
import com.example.appdelishorder.Presenter.productDetailPresenter;
import com.example.appdelishorder.Presenter.productPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.SessionManager;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EvaluateActivity extends AppCompatActivity implements productDetailContract.View, commentContract.View {

    private TextView tvProductName, tvProductPrice;
    private ImageView ivProductImage;
    private Button btnSubmitComment;
    private RatingBar ratingBar;
    private EditText etComment;
    private productDetailContract.Presenter presenterProduct;
    private commentContract.Presenter presenterComment;

    private int productId;
    private String email, regTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evaluate);

        // Initialize views
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        ivProductImage = findViewById(R.id.imgProduct);
        btnSubmitComment = findViewById(R.id.btnSubmit);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etReview);

        // Initialize presenter
        presenterProduct    = new productDetailPresenter(this);
        presenterComment = new commentPresenter(this);

        // Retrieve product details from Intent
        Integer productID = getIntent().getIntExtra("PRODUCT_ID", -1);

        //Email
        SessionManager sessionManager = new SessionManager(this);
        email = sessionManager.getEmail();

        // Display product details
        if (productID != -1) {
            productId = productID;
            presenterProduct.loadProductDetails(productID);
        } else {
            // Handle error: product ID not found
            showError("Product ID not found");
        }

        // Set up button click listener
        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = etComment.getText().toString().trim();
                int rating = (int) ratingBar.getRating();

                if (review.isEmpty() || rating == 0) {
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                regTime = sdf.format(new Date());
                // Create a new comment
                Comment comment = new Comment(
                        email,
                        productId,
                        regTime,
                        review,
                        rating
                );

                presenterComment.submitComment(comment);
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
        // Set product name and price
        tvProductName.setText(product.getName());
        tvProductPrice.setText(String.valueOf(product.getPrice()));
        Glide.with(this)
                .load(product.getImageProduct())
                .into(ivProductImage);
    }

    @Override
    public void displayQuantity(int quantity) {

    }

    @Override
    public void displayTotalPrice(float price) {

    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showComments(List<Comment> comments) {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyComments() {

    }

    @Override
    public void updateProductRating(float rating) {

    }

    @Override
    public void onCommentSubmitted() {
        Toast.makeText(this, "Comment submitted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showAddedToCartSuccess() {

    }
}