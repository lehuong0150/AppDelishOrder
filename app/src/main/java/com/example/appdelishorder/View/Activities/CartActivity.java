package com.example.appdelishorder.View.Activities;


import static androidx.core.content.ContentProviderCompat.requireContext;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Adapter.adapterCart;
import com.example.appdelishorder.Adapter.SuggestedItemsAdapter;
import com.example.appdelishorder.Contract.customerContract;
import com.example.appdelishorder.Contract.orderContract;
import com.example.appdelishorder.Contract.productDetailContract;
import com.example.appdelishorder.Contract.vnPayContract;
import com.example.appdelishorder.Model.CartItem;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.Presenter.customerPresenter;
import com.example.appdelishorder.Presenter.orderPresenter;
import com.example.appdelishorder.Presenter.productDetailPresenter;
import com.example.appdelishorder.Presenter.vnPayPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.OrderUtils;
import com.example.appdelishorder.Utils.SessionManager;
import com.example.appdelishorder.View.Fragments.HomeFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CartActivity extends AppCompatActivity implements productDetailContract.View, orderContract.View, customerContract.View, vnPayContract.View {


    private TextView tvTitle, tvProductCount, tvSubtotal, tvDeliveryFee, tvTotal, btnBackToMenu;
    private EditText etAddress, etPhone;
    private RecyclerView rvProducts;
    private ImageButton btnAddItem;
    private Button btnVNPay, btnCash, btnOrder ;
    private adapterCart cartAdapter;
    private List<CartItem> cartItems;
    private int productId;
    private String selectedPaymentMethod = "Tiền mặt"; // Default payment method
    private productDetailContract.Presenter productPresenter;
    private orderContract.Presenter presenterOrder;
    private vnPayContract.Presenter vnpayPresenter;
    private static final String PREF_NAME = "CartPreferences";
    private static final String CART_ITEMS_KEY = "cartItems";
    private static final int ADD_PRODUCT_REQUEST = 1001;

    private customerContract.Presenter customerPresenter;
    private Customer currentCustomer;
    String email;
    boolean productExists = false;
    int quantity = 0;
    private int lastOrderIdVNPay = -1;

    private RecyclerView rvSuggestedItems;
    private List<Product> suggestedProducts = new ArrayList<>();
    private SuggestedItemsAdapter suggestedItemsAdapter;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize presenter
        productPresenter = new productDetailPresenter(this);
        presenterOrder = new orderPresenter(this);
        customerPresenter = new customerPresenter(this);
        vnpayPresenter = new vnPayPresenter(this);
        // Initialize views
        initializeViews();

        SessionManager sessionManager = new SessionManager(this); // hoặc getApplicationContext()
        email = sessionManager.getEmail();

        // Check if coming from Order
        int orderId = getIntent().getIntExtra("ORDER_ID", 0);
        Log.d("ID", "onCreate: "+ orderId);
        if (orderId > 0) {
            // Load order details for the given order_ID
            presenterOrder.loadOrderDetails(orderId);
        } else {
            // Load cart items from SharedPreferences
            cartItems = loadCartItems();
            setupRecyclerView();
            updateCartSummary();
        }

        // Get product ID from intent - if coming from OrderHistoryFragment for reordering
        productId = getIntent().getIntExtra("PRODUCT_ID", 0);

        //Get quantity from intent
         quantity= getIntent().getIntExtra("QUANTITY", 0);

        // Load cart items from SharedPreferences
        cartItems = loadCartItems();

        // If product ID is passed and it's a valid product, add it to cart
        if (productId > 0) {
            productPresenter.loadProductDetails(productId);
        }
        // load thong tin khach hang
        loadCustomerInformation();

        // Initialize RecyclerView
        setupRecyclerView();
        setupSuggestedItems();  // Add this line

        // Update cart summary
        updateCartSummary();

        // Setup button listeners
        setupButtonListeners();

        // Set default payment method
        selectPaymentMethod("Tiền mặt");
        Log.d("VNPay", "Intent data: " + getIntent().getDataString());
        handleDeepLink(getIntent());
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotal = findViewById(R.id.tvTotal);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        rvProducts = findViewById(R.id.rvProducts);
        btnVNPay = findViewById(R.id.btnVNPay);
        btnCash = findViewById(R.id.btnCash);
        btnOrder = findViewById(R.id.btnOrder);
        btnAddItem = findViewById(R.id.ivbAddItem);
        btnBackToMenu = findViewById(R.id.btnBackMenu);
    }

    // Phương thức để tải thông tin khách hàng
    private void loadCustomerInformation() {
        if (!email.isEmpty()) {
            customerPresenter.loadCustomerInfo(email);
        }
    }

    private void addOrUpdateProductInCart(Product product, int quantity) {
        boolean productExists = false;

        // Check if the product already exists in the cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                // Update the quantity
                item.setQuantity(item.getQuantity() + quantity);
                productExists = true;
                break;
            }
        }

        // If the product does not exist, add it as a new item
        if (!productExists) {
            CartItem newItem = new CartItem(product, quantity);
            cartItems.add(newItem);
        }

        // Update the UI and save the cart
        cartAdapter.notifyDataSetChanged();
        updateCartSummary();
        saveCartItems();

        Toast.makeText(this, product.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }
    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new adapterCart(this, cartItems, new adapterCart.CartItemListener() {
            @Override
            public void onIncrementQuantity(int position) {
                CartItem item = cartItems.get(position);
                item.setQuantity(item.getQuantity() + 1);
                cartAdapter.notifyItemChanged(position);
                updateCartSummary();
                saveCartItems();
            }

            @Override
            public void onDecrementQuantity(int position) {
                CartItem item = cartItems.get(position);
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    cartAdapter.notifyItemChanged(position);
                    updateCartSummary();
                    saveCartItems();
                }
            }

            @Override
            public void onRemoveItem(int position) {
                cartItems.remove(position);
                cartAdapter.notifyItemRemoved(position);
                updateCartSummary();
                saveCartItems();
            }
        });
        rvProducts.setAdapter(cartAdapter);
    }

    private void setupSuggestedItems() {
        rvSuggestedItems = findViewById(R.id.rvSuggestedItems);
        rvSuggestedItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        suggestedItemsAdapter = new SuggestedItemsAdapter(this, suggestedProducts, product -> {
            addOrUpdateProductInCart(product, 1);
            Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
        
        rvSuggestedItems.setAdapter(suggestedItemsAdapter);
        updateSuggestedProducts();
    }

    private void updateSuggestedProducts() {
        if (cartItems.isEmpty()) return;
        
        try {
            // Read association rules from JSON file
            InputStream is = getAssets().open("output.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray rules = jsonObject.getAsJsonArray("association_rules");

            // Get current cart item IDs
            Set<Integer> cartItemIds = new HashSet<>();
            for (CartItem item : cartItems) {
                cartItemIds.add(item.getProduct().getId());
            }

            // Find suggestions based on rules
            Map<Integer, Double> suggestions = new HashMap<>();
            for (JsonElement element : rules) {
                JsonObject rule = element.getAsJsonObject();
                JsonArray antecedents = rule.getAsJsonArray("antecedents");
                JsonArray consequents = rule.getAsJsonArray("consequents");
                double confidence = rule.get("confidence").getAsDouble();

                // Check if rule antecedents match cart items
                boolean matches = true;
                for (JsonElement ant : antecedents) {
                    if (!cartItemIds.contains(ant.getAsInt())) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    // Add consequents to suggestions
                    for (JsonElement cons : consequents) {
                        int productId = cons.getAsInt();
                        if (!cartItemIds.contains(productId)) {
                            suggestions.put(productId, confidence);
                        }
                    }
                }
            }

            // Sort suggestions by confidence
            List<Map.Entry<Integer, Double>> sortedSuggestions = new ArrayList<>(suggestions.entrySet());
            sortedSuggestions.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // Load top 5 suggested products
            suggestedProducts.clear();
            int count = 0;
            for (Map.Entry<Integer, Double> entry : sortedSuggestions) {
                if (count >= 5) break;
                productPresenter.loadProductDetails(entry.getKey());
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupButtonListeners() {
        // Add product button
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            intent.putExtra("SELECT_MODE", true);
            startActivityForResult(intent, ADD_PRODUCT_REQUEST);
        });

        // Back to menu button
        btnBackToMenu.setOnClickListener(v -> {
            saveCartItems();
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            // Thêm cờ để xóa các Activity khác khỏi ngăn xếp
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Handle payment method selection
        btnVNPay.setOnClickListener(v -> {
            selectPaymentMethod("VNPay");
            placeOrder();
        });
        btnCash.setOnClickListener(v -> selectPaymentMethod("Tiền mặt"));

        // Handle order placement
        btnOrder.setOnClickListener(v -> placeOrder());
    }

    private List<CartItem> loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String jsonCartItems = sharedPreferences.getString(CART_ITEMS_KEY, "");

        if (jsonCartItems.isEmpty()) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
        return gson.fromJson(jsonCartItems, type);
    }

    private void saveCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonCartItems = gson.toJson(cartItems);
        editor.putString(CART_ITEMS_KEY, jsonCartItems);
        editor.apply();
    }

    private void updateCartSummary() {
        int subtotal = 0;
        int itemCount = 0;

        for (CartItem item : cartItems) {
            subtotal += item.getQuantity() * item.getProduct().getPrice();
            itemCount += item.getQuantity();
        }

        int deliveryFee = 16000; // Fixed delivery fee
        int total = subtotal + deliveryFee;

        tvProductCount.setText(itemCount + " sản phẩm");
        tvSubtotal.setText(subtotal + " VND");
        tvDeliveryFee.setText(deliveryFee + " VND");
        tvTotal.setText(total + " VND");
        btnOrder.setText("Đặt đơn " + total + " VND");
    }

    private void selectPaymentMethod(String method) {
        // Save selected payment method
        selectedPaymentMethod = method;

        // Highlight selected payment method button
        if (method.equals("VNPay")) {
            btnVNPay.setBackgroundResource(R.drawable.cat_0_background);
            btnCash.setBackgroundResource(R.drawable.category_bg);
        } else {
            btnVNPay.setBackgroundResource(R.drawable.category_bg);
            btnCash.setBackgroundResource(R.drawable.cat_0_background);
        }

        Toast.makeText(this, "Phương thức thanh toán: " + method, Toast.LENGTH_SHORT).show();
    }

    private void placeOrder() {
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tính tổng tiền
        float subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getQuantity() * item.getProduct().getPrice();
        }
        float deliveryFee = 16000;
        float totalPrice = subtotal + deliveryFee;

        // Tạo đối tượng Order phù hợp với model của server
        Order order = Order.createNewOrder();
        order.setShippingAddress(address);
        order.setPhone(phone);
        //thoi gian hien tai
        order.setRegTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(new Date()));
        order.setStatus(0); // Giả sử 1 là trạng thái "Đã đặt hàng" hoặc "Chờ xác nhận"
        order.setAccountEmail(email); // Lấy email của user hiện tại đã đăng nhập
        order.setPaymentMethod(selectedPaymentMethod.equals("Tiền mặt") ? "Tiền mặt" : "VNPay");
        order.setPaymentStatus("Chưa thanh toán");
        order.setTotalPrice(totalPrice);
        order.setRate(false);


        // Thêm thông tin khách hàng nếu có
        if (currentCustomer != null) {
            order.setNameCustomer(currentCustomer.getName());
        }
        // Tạo danh sách OrderDetail từ cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setProductId(item.getProduct().getId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            detail.setProductName(item.getProduct().getName());
            detail.setImageProduct(item.getProduct().getImageProduct());
            detail.setRate(false); // Giả sử chưa đánh giá
            orderDetails.add(detail);
        }
        order.setOrderDetails(orderDetails);

        // Gọi API để tạo đơn hàng sử dụng presenter hiện có
        presenterOrder.placeOrder(order);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            int selectedProductId = data.getIntExtra("SELECTED_PRODUCT_ID", 0);
            if (selectedProductId > 0) {
                productPresenter.loadProductDetails(selectedProductId);
            }
        }

    }

    // productDetailContract.View implementation
    @Override
    public void showLoading() {
        // Show loading UI if needed
    }

    @Override
    public void hideLoading() {
        // Hide loading UI
    }

    @Override
    public void displayCustomerInfo(Customer customer) {
        currentCustomer = customer;

        // Điền thông tin của khách hàng vào form
        if (customer != null) {
            tvTitle.setText(customer.getName());
            etAddress.setText(customer.getAddress());
            etPhone.setText(customer.getPhone());
        }
    }

    @Override
    public void showUpdateSuccess(String message) {

    }

    @Override
    public void showUpdateError(String error) {
    }

    @Override
    public void displayProductDetails(Product product) {
        // For suggestions
        if (!cartItems.stream().anyMatch(item -> item.getProduct().getId() == product.getId())) {
            suggestedProducts.add(product);
            suggestedItemsAdapter.notifyDataSetChanged();
            return;
        }

        // Check if product already exists in cart
        boolean productExists = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                // Increment quantity if product already exists
                item.setQuantity(item.getQuantity() + (quantity > 0 ? quantity : 1));
                productExists = true;
                break;
            }
        }

        // Add new product if it doesn't exist
        if (!productExists) {
            int quantityToAdd = quantity > 0 ? quantity : 1;
            CartItem cartItem = new CartItem(product, quantityToAdd);
            cartItems.add(cartItem);
        }

        // Update UI
        cartAdapter.notifyDataSetChanged();
        updateCartSummary();
        saveCartItems();
        Toast.makeText(this, product.getName() + " đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void displayQuantity(int quantity) {
        // Not needed for cart activity
    }

    @Override
    public void displayTotalPrice(float price) {
        // Not needed for cart activity
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAddedToCartSuccess() {
        // Already handled in displayProductDetails
    }
    @Override
    public void displayOrders(List<Order> orders) {

    }

    @Override
    public void displayOrderDetails(Order orderDetails) {

    }

    @Override
    public void onOrderSuccess(Order order) {
        if ("VNPay".equals(order.getPaymentMethod())) {
            // Gọi VNPay sau khi đã có orderId
            lastOrderIdVNPay = order.getId();
            vnpayPresenter.createVNPayPayment(String.valueOf((int)order.getTotalPrice()), String.valueOf(order.getId()));
        } else {
            // Hiển thị thông báo thành công cho thanh toán tiền mặt
            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            OrderUtils.sendOrderNotification(String.valueOf(order.getId()), "Đơn hàng mới đã được tạo!");
            cartItems.clear();
            saveCartItems();
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onVNPayUrlReceived(String url) {
    // Mở trình duyệt hoặc WebView để thanh toán
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(url));
        startActivity(intent);
    }
    private void handleDeepLink(Intent intent) {
        if (intent.getData() != null
                && "appdelishorder".equals(intent.getData().getScheme())
                && "vnpay_return".equals(intent.getData().getHost())) {
            String status = intent.getData().getQueryParameter("vnp_TransactionStatus");
            if ("00".equals(status)) {
                presenterOrder.updateOrderStatus(lastOrderIdVNPay);
                Toast.makeText(this, "Thanh toán VNPay thành công!", Toast.LENGTH_SHORT).show();
                OrderUtils.sendOrderNotification(String.valueOf(lastOrderIdVNPay), "Đơn hàng mới đã được tạo!");
                cartItems.clear();
                saveCartItems();
                Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else {
                Toast.makeText(this, "Thanh toán VNPay thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("VNP", "onNewIntent: " + intent.getData());
        handleDeepLink(intent);
    }
    @Override
    public void onVNPayError(String message) {
        Toast.makeText(this, "Lỗi VNPay: " + message, Toast.LENGTH_SHORT).show();

    }
}