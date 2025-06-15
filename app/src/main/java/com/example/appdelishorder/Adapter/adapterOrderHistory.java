package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Model.Order;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Utils.OrderStatusUtil;

import java.util.List;

public class adapterOrderHistory extends RecyclerView.Adapter<adapterOrderHistory.OrderHistoryViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OrderHistoryClickListener listener;

    public adapterOrderHistory(Context context, List<Order> orderList, adapterOrderHistory.OrderHistoryClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }
    public interface OrderHistoryClickListener {
        void onOrderClick(Order order);
        void onReorderClick(Order order);
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewhold_history_order, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Mã đơn hàng: " + order.getId());
        holder.tvOrderDate.setText(order.getRegTime().toString());
        holder.tvOrderStatus.setText(OrderStatusUtil.getStatusName(order.getStatus()));
        holder.tvOrderTotal.setText(order.getTotalPrice() + " VND");

        // Set up the nested RecyclerView for order details
        adapterOrderDetail detailAdapter = new adapterOrderDetail(context, order.getOrderDetails(),true);
        holder.rvOrderDetail.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderDetail.setAdapter(detailAdapter);

        // Check if the order status is "Đã giao" (delivered)
        if ("Đã giao".equals(order.getStatus())) {
            holder.btnRate.setVisibility(View.VISIBLE);
        } else {
            holder.btnRate.setVisibility(View.GONE);
        }

        // Check if the order has been rated
        // Update button appearance and behavior based on isRate
        if (order.isRate()) {
            holder.btnRate.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
            holder.btnRate.setText("Đã đánh giá");
            holder.btnRate.setVisibility(View.VISIBLE);
            holder.btnRate.setEnabled(false); // Disable the button
        } else {
            holder.btnRate.setBackgroundTintList(context.getResources().getColorStateList(R.color.black));
            holder.btnRate.setText("Đánh giá");
            holder.btnRate.setVisibility(View.VISIBLE);
            holder.btnRate.setEnabled(false); // Button is visible but not clickable
        }

        // Handle button clicks
        holder.btnOrderAgain.setOnClickListener(v -> listener.onReorderClick(order));
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
        Button btnRate, btnOrderAgain;
        RecyclerView rvOrderDetail;
        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_id_order);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            btnRate = itemView.findViewById(R.id.btn_rate);
            btnOrderAgain = itemView.findViewById(R.id.btn_reorder);
            rvOrderDetail = itemView.findViewById(R.id.rv_order_items);
        }
    }
}