package com.example.appdelishorder.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.R;
import com.example.appdelishorder.View.Activities.EvaluateActivity;

import java.util.List;

public class adapterOrderDetail extends  RecyclerView.Adapter<adapterOrderDetail.OrderDetailViewHolder> {
    private Context context;
    private List<OrderDetail> orderDetailList;
    private boolean isHistoryPage;

    public adapterOrderDetail(Context context, List<OrderDetail> orderDetailList, boolean isHistoryPage) {
        this.context = context;
        this.orderDetailList = orderDetailList;
        this.isHistoryPage = isHistoryPage;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetailList = orderDetails;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public adapterOrderDetail.OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewhold_item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterOrderDetail.OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);

        holder.tvFoodName.setText(orderDetail.getProductName());
        Log.d("rate", "onBindViewHolder: "+ orderDetail.getProductName() + orderDetail.isRate());
        holder.tvPrice.setText("" + orderDetail.getPrice() + " VND");
        holder.tvQuantity.setText("" + orderDetail.getQuantity() + " món");
        // Load ảnh, nếu ảnh là URL
        Glide.with(context)
                .load(orderDetail.getImageProduct())
                .placeholder(R.drawable.ic_launcher_background) // ảnh tạm nếu load lâu
                .into(holder.imgFood);
        //set btn đánh giá
        if (isHistoryPage && !orderDetail.isRate()) {
            holder.btnDanhGia.setVisibility(View.VISIBLE);
            holder.btnDanhGia.setText("Đánh giá");
            holder.btnDanhGia.setBackgroundTintList(context.getResources().getColorStateList(R.color.red));
        } else if(isHistoryPage && orderDetail.isRate()) {
            Log.d("rate", "onBindViewHolder: " + orderDetail.isRate());
            holder.btnDanhGia.setVisibility(View.VISIBLE);
            holder.btnDanhGia.setText("Đã đánh giá");
            holder.btnDanhGia.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
        }
        else {
            holder.btnDanhGia.setVisibility(View.GONE);
        }
        //set sự kiện click cho btn đánh giá
        holder.btnDanhGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EvaluateActivity.class);
                intent.putExtra("PRODUCT_ID", orderDetail.getProductId());
                startActivity(context, intent, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailList != null ? orderDetailList.size() : 0;
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvPrice, tvQuantity;
        Button btnDanhGia;
        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.img_food);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDanhGia = itemView.findViewById(R.id.btn_review);
        }
    }
}