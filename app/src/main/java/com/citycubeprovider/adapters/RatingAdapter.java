package com.citycubeprovider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.citycubeprovider.R;
import com.citycubeprovider.databinding.RatingItemBinding;
import com.citycubeprovider.model.FeedbackModel;

import java.util.ArrayList;

/**
 * Created by Ravindra Birla on 03,May,2021
 */
public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private Context context;
    ArrayList<FeedbackModel.Result>arrayList;

    public RatingAdapter(Context context,ArrayList<FeedbackModel.Result>arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RatingItemBinding  binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rating_item,parent,false);
        return new RatingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
       holder.binding.tvName.setText(arrayList.get(position).userDetail.userName);
       holder.binding.tvReview.setText(arrayList.get(position).review);
       holder.binding.tvTime.setText(arrayList.get(position).dateTime);
       holder.binding.rate.setRating(Float.parseFloat(arrayList.get(position).rating));
        Glide.with(context)
                .load(arrayList.get(position).userDetail.userImage)
                .apply(new RequestOptions().placeholder(R.drawable.user_default))
                .override(80,80)
                .into(holder.binding.ivProfile);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {
        RatingItemBinding binding;
        public RatingViewHolder(RatingItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


}
