package com.example.e_commarceapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commarceapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;

public class AboutUsPage extends AppCompatActivity{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar mainbar;
    Menu menu;
    TextView ab_txt,abt;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_about_us_page);

                MaterialTextView closeTextBtn = findViewById(R.id.close_about);
                closeTextBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
                closeTextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        finish();
                    }
                });
                ab_txt = findViewById(R.id.ab_txt);
                ab_txt.setText("AtoZ Mart is a Professional Shopping Platform. Here we will provide you only interesting Products and useful, which you will like very much.\n" +
                        "\n" +
                        "\n" +
                        "Founded in 2020 by Jignesh Kothari, AtoZ Mart has come a long way from its beginnings in Porbandar.\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "We're dedicated to providing you the best of Shopping Application, with a focus on dependability and Daily serve New Products.\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "We hope you enjoy our products as much as we enjoy offering them to you. If you have any questions or comments, please don't hesitate to contact us.\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "We're working to turn our passion for Shopping Application into a booming online website. We hope you enjoy our Shopping Application as much as we enjoy offering them to you\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "I will keep posting more important and useful products on my Application for all of you. Please give your support and love.\n" +
                        "\n" +
                        "\n" +
                        "Sincerely,\n" +
                        "Mr. Jignesh Kothari ");

            }
}