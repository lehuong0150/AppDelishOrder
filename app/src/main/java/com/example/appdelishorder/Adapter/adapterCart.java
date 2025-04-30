package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Model.CartItem;
import com.example.appdelishorder.Model.OrderDetail;
import com.example.appdelishorder.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class adapterCart extends RecyclerView.Adapter<adapterCart.CartViewHolder>{
    private List<CartItem> cartItems;
    private Context context;
    private CartItemListener listener;

    public interface CartItemListener {
        void onIncrementQuantity(int position);
        void onDecrementQuantity(int position);
        void onRemoveItem(int position);
    }

    public adapterCart(Context context, List<CartItem> cartItems,CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder__cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Load product image
        Glide.with(context)
                .load(item.getProduct().getImageProduct())
                .placeholder(R.drawable.avt)
                .error(R.drawable.edit_text_border)
                .into(holder.ivProductImage);

        // Set product name
        holder.tvProductName.setText(item.getProduct().getName());

        // Format and set price
        holder.tvProductPrice.setText(String.valueOf(item.getProduct().getPrice()) + " VND");

        // Set quantity
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        float totalItemPrice = item.getQuantity() * item.getProduct().getPrice();
        holder.tvTotalPrice.setText(String.valueOf(totalItemPrice) + " VND");
        // Set click listeners
        holder.btnIncrement.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncrementQuantity(holder.getAdapterPosition());
            }
        });

        holder.btnDecrement.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecrementQuantity(holder.getAdapterPosition());
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvQuantity;
        TextView tvTotalPrice;
        View btnIncrement;
        View btnDecrement;
        View btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_subtotal);
            btnIncrement = itemView.findViewById(R.id.btn_plus);
            btnDecrement = itemView.findViewById(R.id.btn_minus);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
