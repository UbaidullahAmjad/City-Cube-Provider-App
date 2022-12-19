package com.citycubeprovider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ItemAvailableRunBinding;
import com.citycubeprovider.databinding.ItemRunBinding;
import com.citycubeprovider.listener.onBookingListener;
import com.citycubeprovider.model.BookingModel;

import java.util.ArrayList;


    public class AdapterRun extends RecyclerView.Adapter<AdapterRun.MyViewHolder> {
        Context context;
        ArrayList<BookingModel.Result> arrayList;

        public AdapterRun(Context context, ArrayList<BookingModel.Result>arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRunBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_run,parent,false);
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
            holder.binding.tvTruck.setText(arrayList.get(position).vanSize);

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ItemRunBinding binding;
            public MyViewHolder(@NonNull ItemRunBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;

            }
        }
    }


