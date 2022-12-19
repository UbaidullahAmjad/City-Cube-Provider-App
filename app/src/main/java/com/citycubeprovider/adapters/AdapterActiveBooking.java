package com.citycubeprovider.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.citycubeprovider.R;
import com.citycubeprovider.activities.LiveTrackingActivity;
import com.citycubeprovider.databinding.ItemActiveBookingBinding;
import com.citycubeprovider.listener.onBookingListener;
import com.citycubeprovider.model.BookingModel;

import java.util.ArrayList;

public class AdapterActiveBooking extends RecyclerView.Adapter<AdapterActiveBooking.MyViewHolder> {
    Context context;
    ArrayList<BookingModel.Result>arrayList;
    onBookingListener listener;

    public AdapterActiveBooking(Context context,ArrayList<BookingModel.Result>arrayList,onBookingListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActiveBookingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.item_active_booking,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvName.setText(arrayList.get(position).userDetails.userName);
        holder.binding.tvEmail.setText(arrayList.get(position).userDetails.email);
        holder.binding.tvDeparture.setText(arrayList.get(position).picuplocation);
        holder.binding.tvArrival.setText(arrayList.get(position).dropofflocation);
        holder.binding.tvTime.setText("Time : "+arrayList.get(position).picklatertime);
        holder.binding.tvDate.setText("Date : "+arrayList.get(position).picklaterdate);
        holder.binding.tvBookingname.setText("Name : "+arrayList.get(position).firstName);
        holder.binding.tvPhone.setText("Phone Number : "+arrayList.get(position).mobile);
        holder.binding.tvPrice.setText("€"+arrayList.get(position).fare);
        holder.binding.tvCompleted.setText(arrayList.get(position).status);
        holder.binding.tvTruck.setText(arrayList.get(position).vanSize);
        if(arrayList.get(position).status.equals("Accept")){
            holder.binding.ivNext.setVisibility(View.GONE);
            holder.binding.tvStart.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.ivNext.setVisibility(View.VISIBLE);
            holder.binding.tvStart.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemActiveBookingBinding binding;
        public MyViewHolder(@NonNull ItemActiveBookingBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.ivNext.setOnClickListener(v -> {
                context.startActivity(new Intent(context, LiveTrackingActivity.class)
                .putExtra("request_id",arrayList.get(getAdapterPosition()).id));
            });

            binding.tvStart.setOnClickListener(v -> {
               listener.onBooking(getAdapterPosition());
            });

        }
    }
}
