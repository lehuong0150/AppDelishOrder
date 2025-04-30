package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.OrderStatusUtil;

import java.util.ArrayList;
import java.util.List;

public class adapterOrderOnGoing extends RecyclerView.Adapter<adapterOrderOnGoing.OrderOnGoingViewHolder> {

    private List<Order> orderList = new ArrayList<>();
    private Context context;
    private adapterOrderOnGoing.OrderOnGoingClickListener listener;
    public List<Order> getOrderList() {
        return orderList;
    }
    public adapterOrderOnGoing(Context context, List<Order> orders, adapterOrderOnGoing.OrderOnGoingClickListener listener) {
        this.context = context;
        this.orderList = orders;
        this.listener = listener;
    }
    public interface OrderOnGoingClickListener {
        void onOrderClick(Order order);
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }
    public void addOrders(List<Order> newOrders) {
        this.orderList.addAll(newOrders);
        notifyDataSetChanged();
    }
    public void filterOrdersByStatus(int status) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getStatus() == status) {
                filteredOrders.add(order);
            }
        }
        this.orderList = filteredOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderOnGoingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order_on_going, parent, false);
        return new OrderOnGoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderOnGoingViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);

        //set order detail
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    static class OrderOnGoingViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvOrderStatus;
        private final TextView tvOrderTime;
        private final TextView tvAddress;
        private final TextView tvPhone;
        private final TextView tvCustomerName;
        private final TextView tvTotalAmount;
        private final TextView tvMethodPayment;
        private final RecyclerView rvOrderDetails;

        public OrderOnGoingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTime = itemView.findViewById(R.id.tv_timeOrder);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvMethodPayment = itemView.findViewById(R.id.tv_payment_method);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            rvOrderDetails = itemView.findViewById(R.id.rv_order_items);
        }

        public void bind(Order order) {
            tvOrderStatus.setText(OrderStatusUtil.getStatusName(order.getStatus()));
            tvOrderTime.setText(order.getRegTime().toString());
            tvAddress.setText(order.getShippingAddress());
            tvPhone.setText(order.getPhone());
            tvCustomerName.setText(order.getNameCustomer());
            tvTotalAmount.setText(order.getTotalPrice() + " VND");
            if( order.getPaymentMethod() == "MoMo") {
                tvMethodPayment.setText("Đã thanh toán ( Momo )");
            } else {
                tvMethodPayment.setText("Chưa thanh toán ( Thanh toán khi giao hàng )");
            }
            // Set up the nested RecyclerView for order details
            adapterOrderDetail detailAdapter = new adapterOrderDetail(itemView.getContext(), order.getOrderDetails(),false);
            rvOrderDetails.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvOrderDetails.setAdapter(detailAdapter);
            // Đảm bảo dữ liệu không trống
            if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                detailAdapter.setOrderDetails(order.getOrderDetails());
                // Cân nhắc đặt visibility một cách rõ ràng
                rvOrderDetails.setVisibility(View.VISIBLE);
            } else {
                tvMethodPayment.setText("loi");
                rvOrderDetails.setVisibility(View.GONE);
            }
        }
    }
}