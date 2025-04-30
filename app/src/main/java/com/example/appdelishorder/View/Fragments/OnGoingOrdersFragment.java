package com.example.appdelishorder.View.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appdelishorder.Adapter.adapterOrderOnGoing;
import com.example.appdelishorder.Contract.orderContract;
import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.Presenter.orderPresenter;
import com.example.appdelishorder.R;
import com.example.appdelishorder.SignalRManager;
import com.example.appdelishorder.Utils.OrderStatusUtil;
import com.example.appdelishorder.Utils.SessionManager;
import com.example.appdelishorder.View.Activities.OrderDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnGoingOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnGoingOrdersFragment extends Fragment implements orderContract.View  {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private adapterOrderOnGoing adapter;
    private orderPresenter presenter;

    private SignalRManager signalRManager;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    // TODO: Rename and change types and number of parameters
    public static OnGoingOrdersFragment newInstance(String param1, String param2) {
        OnGoingOrdersFragment fragment = new OnGoingOrdersFragment();
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
        return inflater.inflate(R.layout.fragment_on_going_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //khoi tao
        signalRManager = SignalRManager.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recyclerViewOngoingOrders);
        progressBar = view.findViewById(R.id.progressBarOngoingOrders);

        // Thiết lập listener để nhận thông báo cập nhật đơn hàng
//        signalRManager.setOrderUpdateListener((orderId, status) -> {
//            requireActivity().runOnUiThread(() -> {
//                // Trong phương thức onOrderStatusUpdated của listener
//                Log.d("OrderUpdate", "Sự kiện cập nhật nhận được: orderId=" + orderId + ", status=" + status);
//                // Hiển thị thông báo toast - chuyển orderId sang String để hiển thị
//                Toast.makeText(getContext(), "Order " + orderId + " updated to: " + status, Toast.LENGTH_SHORT).show();
//
//                // Tìm và cập nhật đơn hàng cụ thể trong danh sách
//                int updatedStatus = OrderStatusUtil.getStatusCode(status);
//                // Chuyển orderId sang int (nếu cần)
//                int orderIdInt = orderId;
//                boolean orderFound = updateOrderInList(orderIdInt, updatedStatus);
//
//                // Nếu không tìm thấy đơn hàng trong danh sách hiện tại (hoặc trạng thái đã chuyển ra khỏi danh mục "đang xử lý"),
//                // làm mới toàn bộ danh sách
//                if (!orderFound) {
//                    presenter.loadOrders(new SessionManager(requireContext()).getEmail(), "0,1,2");
//                }
//            });
//        });

        //thiet lap recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new adapterOrderOnGoing(this.getContext(), new ArrayList<>(), new adapterOrderOnGoing.OrderOnGoingClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        presenter = new orderPresenter(this);

        // Retrieve email, token from SessionManager
        SessionManager sessionManager = new SessionManager(requireContext());
        String email = sessionManager.getEmail();
        String rawToken = sessionManager.getToken();
        String token = "";

        if (rawToken.contains("Token:")) {
            token = rawToken.substring(rawToken.indexOf("Token:") + 6).trim(); // Lấy phần sau "Token:"
        }

        // Kết nối SignalR
        String serverUrl = "http://172.19.201.61:7010/orderHub"; // Thay bằng URL SignalR thực tế
//        signalRManager.initConnection(serverUrl, token);
//        signalRManager.connect();
        // Load orders for the logged-in user
        Log.d("Email", "onGoing: " + email);
        if (email != null) {
            // Load orders for the logged-in user
            presenter.loadOrders(email, "0,1,2");
        } else {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    // Thêm phương thức này để cập nhật đơn hàng cụ thể trong danh sách
    private boolean updateOrderInList(int orderIdInt, int newStatus) {
        if (adapter != null) {
            List<Order> currentOrders = adapter.getOrderList();
            if (currentOrders != null) {
                for (int i = 0; i < currentOrders.size(); i++) {
                    Order order = currentOrders.get(i);
                    if (order.getId() == orderIdInt) {
                        // Kiểm tra nếu trạng thái mới vẫn là "đang xử lý" (0,1,2)
                        if (newStatus <= OrderStatusUtil.STATUS_SHIPPING) {
                            // Cập nhật trạng thái đơn hàng và thông báo adapter
                            order.setStatus(newStatus);
                            adapter.notifyItemChanged(i);
                            return true;
                        } else {
                            // Đơn hàng không còn trong trạng thái "đang xử lý", xóa khỏi danh sách
                            currentOrders.remove(i);
                            adapter.notifyItemRemoved(i);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
        if (orders != null && !orders.isEmpty()) {
            adapter.setOrders(orders);
        } else {
            Toast.makeText(getContext(), "No ongoing orders found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayOrderDetails(Order orderDetails) {

    }

    @Override
    public void onOrderSuccess(Order order) {

    }

    @Override
    public void showError(String message) {
        Log.e("OrderError", message);

        // Show error message to user
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (signalRManager != null) {
            //signalRManager.disconnect();
        }
    }
}
