package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.Admin.AdminMaintainProductsActivity;
import com.example.e_commarceapp.Model.Products;
import com.example.e_commarceapp.Prevalent.Prevalent;
import com.example.e_commarceapp.R;
import com.example.e_commarceapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ornach.nobobutton.NoboButton;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private NoboButton addToCartButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private MaterialTextView productPrice, productDescription, productName;
    private String productID = "";
    private String ProductID = "", state = "Normal";
   private Toolbar mToolbar;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        MaterialTextView closeTextBtn = findViewById(R.id.close_pdetails);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        //set toolbar with back button
        /*mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Details");*/

        addToCartButton = findViewById(R.id.pd_add_to_cart_button);
        numberButton = findViewById(R.id.number_btn);
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(90, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(90);
                }
            }
        });
        productImage = findViewById(R.id.product_image_details);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);

        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();

                /*if (state.equals("Order Placed") || state.equals("Order Shipped")) {
                    Snackbar.with(ProductDetailsActivity.this,null)
                            .type(Type.WARNING)
                            .message("You can purchase more products, once your order is shipped or conformed.")
                            .duration(Duration.CUSTOM,4000)
                            .show();
                } else {
                    addingToCartList();
                }*/
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

//        CheckOrderState();
    }

    private void addingToCartList() {

/*        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Cart List"), Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
          *//*              holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription().substring(0, 19) + "...");
                        holder.txtProductPrice.setText(model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(holder.imageView);*//*
                                productID = model.getPid();
                                        String saveCurrentTime, saveCurrentDate;

                                        Calendar calForDate = Calendar.getInstance();
                                        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
                                        saveCurrentDate = currentDate.format(calForDate.getTime());

                                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                        saveCurrentTime = currentDate.format(calForDate.getTime());

                                        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                        final HashMap<String, Object> cartMap = new HashMap<>();
                                        cartMap.put("pid", model.getPid());
                                        cartMap.put("pname", model.getPname());
                                        cartMap.put("price", model.getPrice());
                                        cartMap.put("image", model.getImage());
                                        cartMap.put("description", model.getDescription());
                                        cartMap.put("date", saveCurrentDate);
                                        cartMap.put("time", saveCurrentTime);
                                        cartMap.put("quantity", numberButton.getNumber());
                                        cartMap.put("discount", "");

                                        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                                .child("Products").child(model.getPid())
                                                .updateChildren(cartMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                                                    .child("Products").child(model.getPid())
                                                                    .updateChildren(cartMap)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Snackbar.with(ProductDetailsActivity.this, null)
                                                                                        .type(Type.SUCCESS)
                                                                                        .message("Added to Cart\uD83D\uDED2 List")
                                                                                        .duration(Duration.CUSTOM, 2000)
                                                                                        .show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };*/

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
//        cartMap.put("image", productImage);
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("description", productDescription.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Snackbar.with(ProductDetailsActivity.this,null)
                                                        .type(Type.SUCCESS)
                                                        .message("Added to Cart\uD83D\uDED2 List")
                                                        .duration(Duration.CUSTOM,2000)
                                                        .show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductDetails(String productID) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shipppingState = snapshot.child("State").getValue().toString();

                    if (shipppingState.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (shipppingState.equals("not shipped")) {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//code of back aero button no toolbar

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    //send user to main activity when press back aero from toolbar
    private void SendUserToMainActivity() {
        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}