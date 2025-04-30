package com.example.appdelishorder.View.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdelishorder.Adapter.adapterOrderHistory;
import com.example.appdelishorder.Contract.orderContract;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Presenter.orderPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.SignalRManager;
import com.example.appdelishorder.Utils.SessionManager;
import com.example.appdelishorder.View.Activities.CartActivity;
import com.example.appdelishorder.View.Activities.OrderDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistoryFragment extends Fragment implements orderContract.View {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private adapterOrderHistory adapter;
    private orderContract.Presenter presenterOrderHistory;
    private SignalRManager signalRManager;

    // Bộ lọc thành phần
    private TextView tvStatusFilter;
    private TextView tvDateFilter;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()); // Định dạng ngày của API (điều chỉnh theo định dạng thực tế)
    private String currentStatusFilter = "all"; // all, cancelled, completed
    private List<Order> allOrders = new ArrayList<>(); // Lưu trữ tất cả đơn hàng để lọc

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        // Initialize RecyclerView and ProgressBar
        recyclerView = view.findViewById(R.id.rv_orders);
        progressBar = view.findViewById(R.id.progressBarHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo các view lọc
        tvStatusFilter = view.findViewById(R.id.tv_status_filter);
        tvDateFilter = view.findViewById(R.id.tv_date_filter);

        // Thiết lập bộ lọc trạng thái
        if (tvStatusFilter != null) {
            tvStatusFilter.setOnClickListener(v -> showStatusFilterMenu());
        }

        // Thiết lập bộ lọc ngày
        if (tvDateFilter != null) {
            tvDateFilter.setOnClickListener(v -> showDatePicker());
        }

        // Khởi tạo SignalRManager
        signalRManager = SignalRManager.getInstance(requireContext());

        // Initialize adapter
        presenterOrderHistory = new orderPresenter(this);

        // Retrieve email from SessionManager
        SessionManager sessionManager = new SessionManager(requireContext());
        String email = sessionManager.getEmail();
        String rawToken = sessionManager.getToken(); // VD: "Đăng nhập thành công. Token: dummy-jwt-token"
        String token = "";

        if (rawToken.contains("Token:")) {
            token = rawToken.substring(rawToken.indexOf("Token:") + 6).trim(); // Lấy phần sau "Token:"
        }

        // Kết nối SignalR
        String serverUrl = "http://172.19.201.61:7010/orderHub"; // Thay bằng URL SignalR thực tế

        // Load orders for the logged-in user
        if (email != null) {
            // Load orders for the logged-in user
            presenterOrderHistory.loadOrders(email, "3,4");
        } else {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    // Hiển thị menu popup cho bộ lọc trạng thái
    private void showStatusFilterMenu() {
        PopupMenu popup = new PopupMenu(requireContext(), tvStatusFilter);
        popup.getMenuInflater().inflate(R.menu.status_filter_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_status_cancelled) {
                tvStatusFilter.setText("Đã hủy");
                applyStatusFilter("cancelled");
                return true;
            }
            else if (itemId == R.id.menu_status_completed) {
                tvStatusFilter.setText("Hoàn thành");
                applyStatusFilter("completed");
                return true;
            }
            else if (itemId == R.id.menu_status_all) {
                tvStatusFilter.setText("Tất cả");
                applyStatusFilter("all");
                return true;
            }

            return false;
        });

        popup.show();
    }

    // Hiển thị hộp thoại chọn ngày
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Cập nhật văn bản hiển thị ngày
                    String formattedDate = dateFormatter.format(selectedDate.getTime());
                    tvDateFilter.setText(formattedDate);

                    // Áp dụng bộ lọc theo ngày
                    applyDateFilter(selectedDate);
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    // Phương thức để áp dụng bộ lọc trạng thái
    private void applyStatusFilter(String status) {
        this.currentStatusFilter = status;
        applyFilters();
    }

    // Phương thức để áp dụng bộ lọc ngày
    private void applyDateFilter(Calendar date) {
        applyFilters();
    }

    // Phương thức áp dụng tất cả bộ lọc
    private void applyFilters() {
        if (allOrders.isEmpty()) {
            return;
        }

        List<Order> filteredOrders = new ArrayList<>(allOrders);

        // Áp dụng lọc trạng thái
        if (!currentStatusFilter.equals("all")) {
            filteredOrders = filteredOrders.stream()
                    .filter(order -> {
                        // Ánh xạ "cancelled" với số trạng thái tương ứng (VD: 3)
                        // và "completed" với số trạng thái tương ứng (VD: 4)
                        if (currentStatusFilter.equals("cancelled")) {
                            return order.getStatus() == 4; // Giả định 3 là mã trạng thái "Đã hủy"
                        } else if (currentStatusFilter.equals("completed")) {
                            return order.getStatus() == 3; // Giả định 4 là mã trạng thái "Hoàn thành"
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // Áp dụng lọc ngày nếu đã chọn ngày
        if (tvDateFilter != null && !tvDateFilter.getText().toString().equals("Ngày/ tháng/năm")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDateStr = dateFormat.format(selectedDate.getTime());

            filteredOrders = filteredOrders.stream()
                    .filter(order -> {
                        // Kiểm tra nếu regtime là null
                        if (order.getRegTime() == null || order.getRegTime().isEmpty()) {
                            return false;
                        }

                        try {
                            // Lấy ngày từ chuỗi regtime (giả sử định dạng: "yyyy-MM-dd'T'HH:mm:ss")
                            String orderDateStr = order.getRegTime().split("T")[0]; // Lấy phần ngày trước T
                            return orderDateStr.equals(selectedDateStr);
                        } catch (Exception e) {
                            // Nếu có lỗi khi phân tích chuỗi ngày
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // Cập nhật RecyclerView với dữ liệu đã lọc
        adapter = new adapterOrderHistory(getContext(), filteredOrders, new adapterOrderHistory.OrderHistoryClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }

            @Override
            public void onReorderClick(Order order) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    // Xóa bộ lọc và hiển thị tất cả đơn hàng
    private void clearFilters() {
        tvStatusFilter.setText("Trạng thái");
        tvDateFilter.setText("Ngày/ tháng/năm");
        currentStatusFilter = "all";
        adapter = new adapterOrderHistory(getContext(), allOrders, new adapterOrderHistory.OrderHistoryClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }

            @Override
            public void onReorderClick(Order order) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayOrders(List<Order> orders) {
        // Lưu tất cả đơn hàng để dùng cho bộ lọc
        this.allOrders = new ArrayList<>(orders);

        adapter = new adapterOrderHistory(getContext(), orders, new adapterOrderHistory.OrderHistoryClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }

            @Override
            public void onReorderClick(Order order) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void displayOrderDetails(Order orderDetails) {
        // Không đổi
    }

    @Override
    public void onOrderSuccess(Order order) {
        // Không đổi
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }
}
