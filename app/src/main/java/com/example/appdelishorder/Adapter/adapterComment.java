package com.example.appdelishorder.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdelishorder.Contract.commentContract;
import com.example.appdelishorder.Model.Comment;
import com.example.appdelishorder.Model.Customer;
import com.example.appdelishorder.R;
import com.example.appdelishorder.Retrofit.APIClient;
import com.example.appdelishorder.Retrofit.ApiService;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterComment extends RecyclerView.Adapter<adapterComment.CommentViewHolder> {
    private List<Comment> comments;
    private Context context;

    public adapterComment(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewhold_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.tvUserName.setText(comment.getCustomerName());
        holder.tvComment.setText(comment.getDescript());
        holder.ratingBar.setRating(comment.getEvaluate());

        // Format date using helper method
        LocalDateTime dateTime = comment.getRegTimeAsDateTime();
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            holder.tvCommentDate.setText(dateTime.format(formatter));
        } else {
            // Fallback to displaying the raw string or a default message
            holder.tvCommentDate.setText(comment.getRegTime() != null ? comment.getRegTime() : "");
        }

        // Load user avatar
        if (comment.getCustomerAvatar() !=null) {
            Glide.with(context)
                    .load(comment.getCustomerAvatar())
                    .placeholder(R.drawable.avt)
                    .error(R.drawable.avt)
                    .circleCrop()
                    .into(holder.imgUserAvatar);
        } else {
            holder.imgUserAvatar.setImageResource(R.drawable.avt);
        }
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public void updateData(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgUserAvatar;
        TextView tvUserName, tvCommentDate;
        TextView tvComment;
        RatingBar ratingBar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.img_user);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvCommentDate = itemView.findViewById(R.id.txtDateTime);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
