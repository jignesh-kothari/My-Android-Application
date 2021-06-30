package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.Model.Cart;
import com.example.e_commarceapp.Prevalent.Prevalent;
import com.example.e_commarceapp.R;
import com.example.e_commarceapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ornach.nobobutton.NoboButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NoboButton NextProcessBtn;
    private MaterialTextView txtTotalAmount, txtMsg1;

    private int overTotalPrice = 0;
    private int particular_totalprice = 0;
    ConstraintLayout constraintLayout;
    private MaterialTextView closeTextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        constraintLayout = (ConstraintLayout) findViewById(R.id.layout_cart_empty);

        closeTextBtn = findViewById(R.id.close_cart);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        NextProcessBtn.setVisibility(View.GONE);
        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTotalAmount.setText("Total Price = \uD83D\uDCB2" + String.valueOf(overTotalPrice));
                Intent intent = new Intent(CartActivity.this, ConformFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        CheckOrderState();`

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                Picasso.get().load(model.getImage()).into(holder.cartImage);
                holder.txtProductQuantity.setText("Quentity = " + model.getQuantity());
                holder.txtProductDescription.setText(model.getDescription().substring(0, 30) + "...");

                int ProductPric = Integer.valueOf(model.getPrice()) * Integer.valueOf(model.getQuantity());
//                particular_totalprice = particular_totalprice + ProductPric;

                holder.txtProductPrice.setText("Price " + ProductPric + "$");
//                holder.txtProductPrice.setText("Price " + model.getPrice() + "$");
                holder.txtProductName.setText(model.getPname());

                int oneTyprProductPrice = Integer.valueOf(model.getPrice()) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTyprProductPrice;

                txtTotalAmount.setText("Total Price = \uD83D\uDCB2" + String.valueOf(overTotalPrice));
                NextProcessBtn.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.GONE);

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        for clear cart admin
                        cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products").child(model.getPid()).removeValue();

                        cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products")
                                .child(model.getPid())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            int oneTyprProductPric = Integer.valueOf(model.getPrice()) * Integer.valueOf(model.getQuantity());
                                            overTotalPrice = overTotalPrice - oneTyprProductPric;
                                            txtTotalAmount.setText("Total Price = \uD83D\uDCB2" + String.valueOf(overTotalPrice));

//                                                      cart item availabel or not
                                            DatabaseReference orderRe;
                                            orderRe = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone());
                                            orderRe.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists())
                                                    {
                                                        NextProcessBtn.setVisibility(View.VISIBLE);
                                                        constraintLayout.setVisibility(View.GONE);
                                                    }
                                                    else {
                                                        NextProcessBtn.setVisibility(View.GONE);
                                                        constraintLayout.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                });
                    }
                });
                /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1) {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        DynamicToast.makeSuccess(CartActivity.this, "Item removed successfully.", 2000).show();
                                                        int oneTyprProductPric = Integer.valueOf(model.getPrice()) * Integer.valueOf(model.getQuantity());
                                                        overTotalPrice = overTotalPrice - oneTyprProductPric;
                                                        txtTotalAmount.setText("Total Price = \uD83D\uDCB2" + String.valueOf(overTotalPrice));

//                                                      cart item availabel or not
                                                        DatabaseReference orderRe;
                                                        orderRe = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone());
                                                        orderRe.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists())
                                                                {
                                                                    NextProcessBtn.setVisibility(View.VISIBLE);
                                                                    constraintLayout.setVisibility(View.GONE);
                                                                }
                                                                else {
                                                                    NextProcessBtn.setVisibility(View.GONE);
                                                                    constraintLayout.setVisibility(View.VISIBLE);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });*/
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String shipppingState = snapshot.child("State").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shipppingState.equals("shipped")){
                        txtTotalAmount.setText("Dear "+ userName + "\n order is shipped Successfully");
                        recyclerView.setVisibility(View.GONE);

                        constraintLayout.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulation Your final order has been Shipped Successfully. Soon you will recive yout order at your door step.");
                        NextProcessBtn.setVisibility(View.GONE);
                    }

                    else if(shipppingState.equals("not shipped")){
                        txtTotalAmount.setText("Product(Item) Not Shipped Yet");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.GONE);

                        Snackbar.with(CartActivity.this,null)
                                .type(Type.WARNING)
                                .message("You can Purchase More Products, Once You Received Your First Final Order")
                                .duration(Duration.CUSTOM,4000)
                                .show();
                    }
                }
                else {
                    NextProcessBtn.setVisibility(View.GONE);
                    txtTotalAmount.setText("Total Price = \uD83D\uDCB2" + String.valueOf(overTotalPrice));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}