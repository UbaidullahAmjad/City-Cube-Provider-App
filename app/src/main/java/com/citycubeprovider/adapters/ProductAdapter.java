package com.citycubeprovider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.citycubeprovider.R;
import com.citycubeprovider.databinding.ProductItemBinding;

/**
 * Created by Ravindra Birla on 01,May,2021
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {


    ProductItemBinding binding;
    private Context context;
    public ProductAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.product_item,parent,false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public ProductViewHolder(ProductItemBinding binding1) {
            super(binding1.getRoot());
        }
    }

}
