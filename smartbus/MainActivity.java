package com.example.user.smartbus;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Switch;

import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView SubTitleOne, SubTitleTwo, SubTitleThree, SubTitleFour, SubTitleFive, SubTitleSix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        title for dashboard

        String dashboard = getString(R.string.dashboard);
        getSupportActionBar().setTitle(dashboard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        logo for dashboard
        getSupportActionBar().setLogo(R.drawable.ic_directions_bus_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        activity main start here
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initialize all cardView
        SubTitleOne = findViewById(R.id.SubTitleOne);
        SubTitleTwo = findViewById(R.id.SubTitleTwo);
        SubTitleThree = findViewById(R.id.SubTitleThree);
        SubTitleFour = findViewById(R.id.SubTittleFour);
        SubTitleFive = findViewById(R.id.SubTitleFive);
        SubTitleSix = findViewById(R.id.SubTitleSix);
        SubTitleOne.setOnClickListener(this);
        SubTitleTwo.setOnClickListener(this);
        SubTitleThree.setOnClickListener(this);
        SubTitleFour.setOnClickListener(this);
        SubTitleFive.setOnClickListener(this);
        SubTitleSix.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.SubTitleOne:
                i = new Intent(this, Where_My_Bus.class);
                startActivity(i);
                break;
            case R.id.SubTitleTwo:
                i = new Intent(this, Schedule.class);
                startActivity(i);
                break;
            case R.id.SubTitleThree:
                i = new Intent(this, Galleries.class);
                startActivity(i);
                break;
            case R.id.SubTittleFour:
                i = new Intent("android.intent.action.VIEW", Uri.parse("https://goo.gl/forms/XqoSQJJc02UBb1vf2"));
                startActivity(i);
                break;
            case R.id.SubTitleFive:
                i = new Intent(this, Where_My_Bus.class);
                startActivity(i);
                break;
            case R.id.SubTitleSix:
                i = new Intent(this, Contact.class);
                startActivity(i);
                break;

        }
    }
}
