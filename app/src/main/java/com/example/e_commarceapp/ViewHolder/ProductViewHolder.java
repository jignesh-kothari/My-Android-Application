package com.example.e_commarceapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commarceapp.Interface.ItemClickListner;
import com.example.e_commarceapp.R;
import com.google.android.material.textview.MaterialTextView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public MaterialTextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView, addtocartdirectbtn;
    public ItemClickListner listner;


    public ProductViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = itemView.findViewById(R.id.product_name);
        txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        addtocartdirectbtn = itemView.findViewById(R.id.ic_addtocart_img);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
