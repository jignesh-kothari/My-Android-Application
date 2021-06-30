package com.example.e_commarceapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commarceapp.Interface.ItemClickListner;
import com.example.e_commarceapp.R;
import com.google.android.material.textview.MaterialTextView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView cartImage;
    public LinearLayout remove, edit;
    public MaterialTextView txtProductName, txtProductPrice, txtProductQuantity, txtProductDescription;
    private ItemClickListner itemClickListner;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cartImage = itemView.findViewById(R.id.image_cartlist);
        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductDescription = itemView.findViewById(R.id.description_cart_products);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);

        remove = itemView.findViewById(R.id.layout_action1);
        edit = itemView.findViewById(R.id.layout_action2);
    }

    @Override
    public void onClick(View view){
        itemClickListner.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
