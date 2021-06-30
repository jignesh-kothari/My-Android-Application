package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.Prevalent.Prevalent;
import com.example.e_commarceapp.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        MaterialTextView closeTextBtn = findViewById(R.id.close_order);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }
    private void CheckOrderState(){
        /*DatabaseReference orderRef;
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
        });*/
    }
}