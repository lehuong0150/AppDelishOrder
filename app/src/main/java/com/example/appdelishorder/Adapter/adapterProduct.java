package com.example.appdelishorder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdelishorder.Model.Product;
import com.example.appdelishorder.R;

import java.util.List;

public class adapterProduct extends  RecyclerView.Adapter<adapterProduct.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    public adapterProduct(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_products, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice() + " VND");
        Glide.with(context).load(product.getImageProduct()).into(holder.image);
        //hien thi het hang
        if (!product.isAvailable()) {
            holder.frameSoldOut.setVisibility(View.VISIBLE);
        } else {
            holder.frameSoldOut.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        ImageButton btnAddCart;
        FrameLayout frameSoldOut;
        @SuppressLint("WrongViewCast")
        public ProductViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.img_Product);
            name = view.findViewById(R.id.txt_nameProduct);
            price = view.findViewById(R.id.txt_priceProduct);
            btnAddCart = view.findViewById(R.id.btn_addCart);
            frameSoldOut = itemView.findViewById(R.id.view_outsold);
        }
    }
}
