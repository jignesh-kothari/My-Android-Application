package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ornach.nobobutton.NoboButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

public class RegisterActivity extends AppCompatActivity {

    private NoboButton CreateAccountButton;
    private TextInputEditText InputName, InputPhoneNumber, InputPassword, InputPasswordConform;
    private ProgressDialog loadingBar;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.backarrow);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = (TextInputEditText)findViewById(R.id.register_username_input);
        InputPhoneNumber = (TextInputEditText)findViewById(R.id.register_phone_number_input);
        InputPassword = findViewById(R.id.register_password_input);
        InputPasswordConform = findViewById(R.id.register_password_input_conform);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String passwordconform = InputPasswordConform.getText().toString();

        if (isEmpty(name) || !name.matches("[a-z,A-Z ]+"))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid Name(\uD83D\uDE4E)")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (isEmpty(phone) || !phone.matches("[0-9]{10}"))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid Phone(\uD83D\uDCDE) No")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (isEmpty(password) || password.length()<6)
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Password(\uD83D\uDD11) must be 6 character long")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (!password.equals(passwordconform)){
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Password and Conform password must be same")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setIcon(R.drawable.ic_setting);
            loadingBar.show();

            ValidatephoneNumber(name, phone, password);
        }
    }

    private void showError(TextInputEditText input, String s) {
//        input.setError(s);
//        input.requestFocus();
    }

    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        InputName.setText("");
                                        InputPhoneNumber.setText("");
                                        InputPassword.setText("");
                                        /*Snackbar.with(RegisterActivity.this,null)
                                                .type(Type.SUCCESS)
                                                .message("Congratulations, your account has been created.")
                                                .duration(Duration.SHORT)
                                                .show();*/
                                        DynamicToast.makeSuccess(RegisterActivity.this, "Congratulations "+name+", your account has been created.", 2000).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Snackbar.with(RegisterActivity.this,null)
                                                .type(Type.ERROR)
                                                .message("Network Error: Please try again after some time...")
                                                .duration(Duration.SHORT)
                                                .show();
//                                        Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Snackbar.with(RegisterActivity.this,null)
                            .type(Type.ERROR)
                            .message("This " + phone + " already exists,\n\n Please try again using another\n phone(\uD83D\uDCDE) number.")
                            .duration(Duration.CUSTOM,4000)
                            .show();
                    loadingBar.dismiss();

                    /*Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}