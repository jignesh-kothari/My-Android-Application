package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

public class ContactUsActivity extends AppCompatActivity {
    private NoboButton contactusbtn;
    private TextInputEditText InputName, InputPhoneNumber, InputMessage;
    private ProgressDialog loadingBar;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        MaterialTextView closeTextBtn = findViewById(R.id.close_contact);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        ImageButton calldial = findViewById(R.id.Phone_btn);
        calldial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:9924173641"));
                startActivity(i);
            }
        });


        ImageButton callemail = findViewById(R.id.Email_btn);
        callemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jigneshkothari468@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Your subject hear");
                intent.putExtra(Intent.EXTRA_TEXT, "Your message hear");

                //need this to prompts email client only
                intent.setType("message/rfc822");

                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

        contactusbtn = findViewById(R.id.contactus_btn);
        InputName = (TextInputEditText)findViewById(R.id.contact_name);
        InputPhoneNumber = (TextInputEditText)findViewById(R.id.contact_phone);
        InputMessage = (TextInputEditText)findViewById(R.id.contact_message);
        loadingBar = new ProgressDialog(this);

        contactusbtn.setOnClickListener(new View.OnClickListener() {
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
        String message = InputMessage.getText().toString();

        if (isEmpty(name) || !name.matches("[a-z,A-Z ]+"))
        {
            Snackbar.with(ContactUsActivity.this,null)
                    .type(Type.ERROR)
                    .message("Please enter Valid name(\uD83D\uDE4E)...")
                    .duration(Duration.CUSTOM,2000)
                    .show();
//            name.requestFocus();
        }
        else if (isEmpty(phone) || !phone.matches("[0-9]{10}"))
        {
            Snackbar.with(ContactUsActivity.this,null)
                    .type(Type.ERROR)
                    .message("Please enter Valid phone(\uD83D\uDCDE) number...")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (isEmpty(message) || message.length()<10)
        {
            Snackbar.with(ContactUsActivity.this,null)
                .type(Type.ERROR)
                .message("Message Length must be more then 10 characters long")
                .duration(Duration.CUSTOM,3000)
                .show();
        }
        else
        {
            loadingBar.setTitle("Sending Message");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setIcon(R.drawable.ic_sendic);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatephoneNumber(name, phone, message);
        }
    }

    private void showError(TextInputEditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void ValidatephoneNumber(final String name, final String phone, final String message)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Contact").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("message", message);
                    userdataMap.put("name", name);

                    RootRef.child("Contact").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Snackbar.with(ContactUsActivity.this,null)
                                                .type(Type.SUCCESS)
                                                .message("Your Message has been sent we will shortly reply you.")
                                                .duration(Duration.CUSTOM,4000)
                                                .show();
                                        loadingBar.dismiss();

                                        InputName.setText("");
                                        InputPhoneNumber.setText("");
                                        InputMessage.setText("");
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Snackbar.with(ContactUsActivity.this,null)
                                                .type(Type.ERROR)
                                                .message("Network Error: Please try again after some time...")
                                                .duration(Duration.CUSTOM,3000)
                                                .show();
                                    }
                                }
                            });
                }
                else
                {
                    Snackbar.with(ContactUsActivity.this,null)
                            .type(Type.ERROR)
                            .message("This " + phone + " already exists,\n\n Please try again using another\n phone(\uD83D\uDCDE) number.")
                            .duration(Duration.CUSTOM,4000)
                            .show();
                    loadingBar.dismiss();
                    /*Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
                    startActivity(intent);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}