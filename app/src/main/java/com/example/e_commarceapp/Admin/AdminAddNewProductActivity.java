package com.example.e_commarceapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.example.e_commarceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ornach.nobobutton.NoboButton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private NoboButton AddNewProductButton;
    private ImageView InputProductImage, img_gone;
    private TextInputEditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        MaterialTextView closeTextBtn = findViewById(R.id.close_addnewproduct);
        closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        notificationManager = NotificationManagerCompat.from(this);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        CategoryName = getIntent().getExtras().get("category").toString();

        AddNewProductButton = findViewById(R.id.add_new_product);
        InputProductImage = findViewById(R.id.select_product_image);
        img_gone = findViewById(R.id.img_gone);
        img_gone.setVisibility(View.VISIBLE);
        InputProductName = findViewById(R.id.product_name);
        InputProductDescription = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
            img_gone.setVisibility(View.GONE);
        }
    }

    private void ValidateProductData()
    {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString().toLowerCase();


        if (ImageUri == null)
        {
            Snackbar.with(AdminAddNewProductActivity.this,null)
                    .type(Type.ERROR)
                    .message("Please Select Product image!")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (TextUtils.isEmpty(Pname))
        {
            Snackbar.with(AdminAddNewProductActivity.this,null)
                    .type(Type.ERROR)
                    .message("please write product name!")
                    .duration(Duration.CUSTOM,2000)
                    .show();

        }
        else if (TextUtils.isEmpty(Description) || Description.length()<35)
        {
            Snackbar.with(AdminAddNewProductActivity.this,null)
                    .type(Type.ERROR)
                    .message("please write product description!(In more then 35 characters)")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Snackbar.with(AdminAddNewProductActivity.this,null)
                    .type(Type.ERROR)
                    .message("please write product price!")
                    .duration(Duration.CUSTOM,2000)
                    .show();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Adding Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setIcon(R.drawable.ic_add);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime + System.currentTimeMillis();


        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                DynamicToast.makeError(AdminAddNewProductActivity.this, "Error: " + message, 2000).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Snackbar.with(AdminAddNewProductActivity.this,null)
                        .type(Type.SUCCESS)
                        .message("Product Image uploaded Successfully")
                        .duration(Duration.CUSTOM,2000)
                        .show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {

                            downloadImageUrl = task.getResult().toString();
                            Snackbar.with(AdminAddNewProductActivity.this,null)
                                    .type(Type.SUCCESS)
                                    .message("got the Product image Url Successfully")
                                    .duration(Duration.CUSTOM,2000)
                                    .show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            DynamicToast.makeSuccess(AdminAddNewProductActivity.this, "Product is added successfully..", 2000).show();
//                            notification
                         /*   public void showNotification(View v) {
                            RemoteViews collapsedView = new RemoteViews(getPackageName(),
                                    R.layout.notification_collapsed);
                            RemoteViews expandedView = new RemoteViews(getPackageName(),
                                    R.layout.notification_expanded);
                            Intent clickIntent = new Intent(this, NotificationReceiver.class);
                            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
                                    0, clickIntent, 0);
                            collapsedView.setTextViewText(R.id.text_view_collapsed_1, "Hello World!");
                            expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.ic_baseline_backup_24);
                            expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);
                            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_android)
                                    .setCustomContentView(collapsedView)
//                .setColor(0xff00ff00)
                                    .setCustomBigContentView(expandedView)
                                    //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                    .build();
                            notificationManager.notify(1, notification);
                        }*/
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Snackbar.with(AdminAddNewProductActivity.this,null)
                                    .type(Type.ERROR)
                                    .message("Error...")
                                    .duration(Duration.CUSTOM,2000)
                                    .show();
                        }
                    }
                });
    }

}