package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Model.Category;
import com.example.appdelishorder.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterCategory extends RecyclerView.Adapter<adapterCategory.CategoryViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private OnCategoryClickListener  listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    public adapterCategory(Context context, List<Category> categoryList,OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public adapterCategory.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterCategory.CategoryViewHolder holder, int position) {

        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        // Sử dụng Glide để load ảnh từ URL
        Glide.with(context)
                .load(category.getImageCategory())
                .into(holder.categoryImage);
        if (!category.isAvaible()) {
            holder.frameSoldOut.setVisibility(View.VISIBLE);
        } else {
            holder.frameSoldOut.setVisibility(View.GONE);
        }
        // Kiểm tra xem listener có null không
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> {
                Log.d("Adapter", "Category clicked: " + category.getName()); // Thêm log để kiểm tra
                listener.onCategoryClick(category);
            });
        } else {
            Log.e("Adapter", "OnCategoryClickListener is null!");
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView categoryName;
        CircleImageView categoryImage;
        FrameLayout frameSoldOut;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.name_category);
            categoryImage = itemView.findViewById(R.id.img_Category);
            frameSoldOut = itemView.findViewById(R.id.view_outsold);
        }
    }
}
