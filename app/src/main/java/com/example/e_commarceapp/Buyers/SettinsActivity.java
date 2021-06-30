package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.Prevalent.Prevalent;
import com.example.e_commarceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.ornach.nobobutton.NoboButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettinsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private ImageView plusimg;
    private TextInputEditText fullNameEditText, userPhoneEditText, addressEditText;
    private MaterialTextView profileChangeTextBtn,  closeTextBtn;
    private NoboButton securityQuestionBtn, saveTextButton, remove_profile;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";

    DatabaseReference UsersRef1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settins);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
//
        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        plusimg = findViewById(R.id.plus_hid);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userPhoneEditText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);
        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);

        closeTextBtn = findViewById(R.id.close_settings_btn);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);

        saveTextButton = findViewById(R.id.update_account_settings_btn);
        securityQuestionBtn = findViewById(R.id.security_questions_btn);

        //        remove user
        remove_profile = findViewById(R.id.remove_my_profile);
        remove_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettinsActivity.this);
                builder.setTitle("Delete Account!!!");
                builder.setIcon(R.drawable.ic_warning);
                builder.setMessage("Are You Sure You Want To Delete your Account permanently? \n\n It will erase your all data!!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                UsersRef1 = FirebaseDatabase.getInstance().getReference();
                                String uphone = Prevalent.currentOnlineUser.getPhone();
//                                RemoverOrder(uphone);
//delete cart
                                UsersRef1.child("Users")
                                        .child(uphone)
                                        .removeValue();
                                Intent intent = new Intent(SettinsActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SettinsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)

                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("CROP")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonTitle("Done")
                        .setRequestedSize(400, 400)

//                        .setAspectRatio(1, 1)
                        .start(SettinsActivity.this);
            }
        });
    }
    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());
        userMap. put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        Snackbar.with(SettinsActivity.this,null)
                .type(Type.SUCCESS)
                .message("Profile Info update successfully.")
                .duration(Duration.CUSTOM,2000)
                .show();
    }
//
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
            plusimg.setVisibility(View.GONE);
        }
        else
        {
            plusimg.setVisibility(View.VISIBLE);
            DynamicToast.makeError(this, "Error, Try Again.", 2000).show();
            startActivity(new Intent(SettinsActivity.this, SettinsActivity.class));
            finish();
        }
    }
//
//
//
//
    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid Name(\uD83D\uDE4E)")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid address")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Snackbar.with(this,null)
                    .type(Type.ERROR)
                    .message("please enter valid Phone(\uD83D\uDCDE) No")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
            updateOnlyUserInfo();
        }
    }
//
//
//
    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", fullNameEditText.getText().toString());
                                userMap. put("address", addressEditText.getText().toString());
                                userMap. put("phoneOrder", userPhoneEditText.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                Snackbar.with(SettinsActivity.this,null)
                                        .type(Type.SUCCESS)
                                        .message("Profile Info update successfully.")
                                        .duration(Duration.CUSTOM,2000)
                                        .show();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Snackbar.with(SettinsActivity.this,null)
                                        .type(Type.ERROR)
                                        .message("Error...")
                                        .duration(Duration.CUSTOM,2000)
                                        .show();
                            }
                        }
                    });
        }
        else
        {
            Snackbar.with(SettinsActivity.this,null)
                    .type(Type.ERROR)
                    .message("image is not selected.")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
    }
//
//
    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//    private void RemoverOrder(String uphone) {
//        UsersRef1.child(uphone).removeValue();
//    }
}