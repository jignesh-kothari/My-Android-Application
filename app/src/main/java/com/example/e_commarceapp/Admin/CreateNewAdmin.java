package com.example.e_commarceapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.Buyers.LoginActivity;
import com.example.e_commarceapp.Buyers.MainActivity;
import com.example.e_commarceapp.Buyers.RegisterActivity;
import com.example.e_commarceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ornach.nobobutton.NoboButton;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class CreateNewAdmin extends AppCompatActivity {
    private NoboButton CreateAccountButton;
    private TextInputEditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_admin);

        MaterialTextView closeTextBtn = findViewById(R.id.close_addnewadmin);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        CreateAccountButton = findViewById(R.id.register_btn_admin);
        InputName = (TextInputEditText)findViewById(R.id.register_username_input_admin);
        InputPhoneNumber = (TextInputEditText)findViewById(R.id.register_phone_number_input_admin);
        InputPassword = (TextInputEditText)findViewById(R.id.register_password_input_admin);
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

        if (isEmpty(name) || !name.matches("[a-z,A-Z ]+"))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid Admin Name(\uD83D\uDE4E)")
                    .duration(Duration.CUSTOM,2000)
                    .show();
            InputName.requestFocus();
        }
        else if (isEmpty(phone) || !phone.matches("[0-9]{10}"))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid Admin Phone(\uD83D\uDCDE) No")
                    .duration(Duration.CUSTOM,2000)
                    .show();
            InputPhoneNumber.requestFocus();
        }
        else if (isEmpty(password) || password.length()<6)
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("Password(\uD83D\uDD11) must be 6 character long")
                    .duration(Duration.CUSTOM,2000)
                    .show();
            InputPassword.requestFocus();
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
        input.requestFocus();
    }

    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Admins").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Admins").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Snackbar.with(CreateNewAdmin.this,null)
                                                .type(Type.SUCCESS)
                                                .message("New Admin has been created")
                                                .duration(Duration.CUSTOM,2000)
                                                .show();
                                        InputName.setText("");
                                        InputPhoneNumber.setText("");
                                        InputPassword.setText("");

                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Snackbar.with(CreateNewAdmin.this,null)
                                                .type(Type.ERROR)
                                                .message("Network Error: Please try again after some time...")
                                                .duration(Duration.CUSTOM,4000)
                                                .show();
                                    }
                                }
                            });
                }
                else
                {
                    Snackbar.with(CreateNewAdmin.this,null)
                            .type(Type.ERROR)
                            .message("This " + phone + " already exists,\n\n Please try again using another\n phone(\uD83D\uDCDE) number.")
                            .duration(Duration.CUSTOM,4000)
                            .show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}