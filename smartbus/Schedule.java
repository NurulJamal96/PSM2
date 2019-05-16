package com.example.user.smartbus;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Console;

public class Schedule extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String dashboard = getString(R.string.dashboard);
        getSupportActionBar().setTitle(dashboard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        logo for dashboard
        getSupportActionBar().setLogo(R.drawable.ic_directions_bus_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ImageView image =findViewById(R.id.scheduleImage);
        String imageUri = "https://firebasestorage.googleapis.com/v0/b/fypshafri.appspot.com/o/schedule%2F1A1B.jpg?alt=media&token=fe34e054-d241-4469-a390-05bb07b625bb";
    //   Picasso.with(this).load(imageUri).into(image);
       Picasso.get().load(imageUri).into(image);

    }
}
