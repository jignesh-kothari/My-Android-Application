package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.Prevalent.Prevalent;
import com.example.e_commarceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ornach.nobobutton.NoboButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class ConformFinalOrderActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private NoboButton confirmOrderBtn;

    private String totalAmount = "";
    private MaterialTextView closeTextBtn;

    private String productRandomKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conform_final_order);

        closeTextBtn = findViewById(R.id.close_order);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);

        totalAmount = getIntent().getStringExtra("Total Price");
        DynamicToast.makeSuccess(this, "Total Price =  \uD83D\uDCB2" + totalAmount, 2000).show();
        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        nameEditText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        addressEditText = findViewById(R.id.shipment_address);
        cityEditText = findViewById(R.id.shipment_city);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Check();
            }
        });
    }

    private void Check() {
        if (isEmpty(nameEditText.getText().toString()) || !nameEditText.getText().toString().matches("[a-z,A-Z ]+")){
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Please Provide Your Valid Name")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString()) || !phoneEditText.getText().toString().matches("[0-9]{10}")){
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Please Enter Valid Phone Number(\uD83D\uDCDE)\n(In 10 degits)")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()) || addressEditText.getText().toString().length()<10){
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Please Provide Your Full Address(more then 10 characters)")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (isEmpty(cityEditText.getText().toString()) || !cityEditText.getText().toString().matches("[a-z,A-Z ]+")){
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Please Provide Valid City Name")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime, currentTimemiliseconds;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime + System.currentTimeMillis();

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> orderMap = new HashMap<>();
//        orderMap.put("poi",productRandomKey);
//        orderMap.put("curuser",Prevalent.currentOnlineUser.getPhone());
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", nameEditText.getText().toString());
        orderMap.put("phone", phoneEditText.getText().toString());
        orderMap.put("address", addressEditText.getText().toString());
        orderMap.put("city", cityEditText.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("State", "not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        DynamicToast.makeSuccess(ConformFinalOrderActivity.this,"Your Final Order has been Placed Successfullly",2000).show();
                                        Intent intent = new Intent(ConformFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}