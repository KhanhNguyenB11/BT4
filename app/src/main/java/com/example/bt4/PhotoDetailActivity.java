package com.example.bt4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity
{
    private ConstraintLayout cs;
    private TextView location, title, name, party;
    private ImageView dp;
    private static final String TAG = "PhotoDetailActivity";
    private Official temp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        setupComponents();
        setUpLocation();
        fillData();
    }

    void setupComponents()
    {
        cs = findViewById(R.id.constrainedLayout);
        location = findViewById(R.id.location);
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        dp = findViewById(R.id.dp);
    }

    void setUpLocation()
    {
        if(getIntent().hasExtra("location"))
            location.setText(getIntent().getStringExtra("location"));
        else
            location.setText("");
    }

    void fillData()
    {
        if(getIntent().hasExtra("official"))
        {
            temp = (Official) getIntent().getSerializableExtra("official");
            assert temp != null;
            title.setText(temp.getTitle());
            name.setText(temp.getName());
            party.setText(temp.getParty());

            if(temp.getParty().trim().toLowerCase().contains("democratic"))
                setUpDemocraticTheme();
            else if(temp.getParty().trim().toLowerCase().contains("republican"))
                setUpRepublicanTheme();
            else
                setUpNonPartisanTheme();

            loadProfilePicture(temp.getPhotoURL().trim());
        }

    }

    void setUpDemocraticTheme()
    {
        location.setBackgroundResource(R.color.dark_blue);
        cs.setBackgroundResource(R.color.blue);
        getWindow().setNavigationBarColor(getColor(R.color.blue));
    }

    void setUpRepublicanTheme()
    {
        location.setBackgroundResource(R.color.dark_red);
        cs.setBackgroundResource(R.color.red);
        getWindow().setNavigationBarColor(getColor(R.color.red));
    }
    void setUpNonPartisanTheme()
    {
        location.setBackgroundResource(R.color.extra_dark_grey);
        cs.setBackgroundResource(R.color.dark_grey);
        getWindow().setNavigationBarColor(getColor(R.color.dark_grey));
    }

    void loadProfilePicture(String URL)
    {
        Log.d(TAG, "bp: loadProfilePicture: URL: " + URL);

        Picasso.get()
                .load(URL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(dp);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

}
