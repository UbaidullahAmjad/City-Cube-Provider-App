package com.citycubeprovider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.UploadItemBinding;

/**
 * Created by Ravindra Birla on 01,May,2021
 */
public class UploadingImageAdapter extends RecyclerView.Adapter<UploadingImageAdapter.ProductViewHolder> {


    UploadItemBinding binding;
    private Context context;
    public UploadingImageAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.upload_item,parent,false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        if(position==0)
        {
            binding.ivAdd.setVisibility(View.VISIBLE);
            binding.ivUpload.setVisibility(View.GONE);

        }
        else
        {
            binding.ivAdd.setVisibility(View.GONE);
            binding.ivUpload.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public ProductViewHolder(UploadItemBinding binding1) {
            super(binding1.getRoot());
        }
    }

}
