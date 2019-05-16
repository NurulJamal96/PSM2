package com.example.user.smartbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dashboard = getString(R.string.dashboard);
        getSupportActionBar().setTitle(dashboard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        logo for dashboard
        getSupportActionBar().setLogo(R.drawable.ic_directions_bus_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }
}
