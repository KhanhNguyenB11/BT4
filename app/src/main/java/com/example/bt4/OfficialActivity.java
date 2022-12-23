package com.example.bt4;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OfficialActivity extends AppCompatActivity
{
    private ConstraintLayout cs, dc;
    private TextView title, name, party, address,addressLab, email, emailLab, url, urlLab, phone, phoneLab, location;
    private ImageView dp, fb_icn, twt_icn, ytb_icn, gp_icn;
    private Channel fbHandle, twitterHandle, youtubeHandle, gplusHandle;
    private Official temp;
    private static final String TAG = "OfficialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        setupComponents();
        setUpLocation();
        fillData();
    }
    void setupComponents()
    {
        cs = findViewById(R.id.constrainedLayout);
        dc = findViewById(R.id.detailsCard);
        location = findViewById(R.id.location);
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        address = findViewById(R.id.address);
        addressLab = findViewById(R.id.addrressLabel);
        email = findViewById(R.id.email);
        emailLab = findViewById(R.id.emailLabel);
        url = findViewById(R.id.url);
        urlLab = findViewById(R.id.urlLabel);
        phone = findViewById(R.id.phone);
        phoneLab = findViewById(R.id.phoneLabel);
        dp = findViewById(R.id.dp);
        fb_icn = findViewById(R.id.facebook);
        twt_icn = findViewById(R.id.twitter);
        ytb_icn = findViewById(R.id.youtube);
        gp_icn = findViewById(R.id.gplus);
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
            ArrayList<Channel> channels;
            assert temp != null;
            title.setText(temp.getTitle());
            name.setText(temp.getName());
            party.setText(temp.getParty());
            if(!temp.getAddress().equals(""))
            {
                address.setText(temp.getAddress().trim());
                address.setLinkTextColor(getColor(R.color.white));
            }
            else
            {
                addressLab.setVisibility(View.GONE);
                address.setVisibility(View.GONE);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,0,0);
                phoneLab.setLayoutParams(params);
            }

            if(!temp.getPhones().equals(""))
            {
                phone.setText(temp.getPhones());
                phone.setLinkTextColor(getColor(R.color.white));
            }
            else
            {
                phoneLab.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
            }

            if(!temp.getEmails().equals(""))
            {
                email.setText(temp.getEmails());
                email.setLinkTextColor(getColor(R.color.white));

            }
            else
            {
                emailLab.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
            }

            if(!temp.getUrls().equals(""))
            {
                url.setText(temp.getUrls());
                url.setLinkTextColor(getColor(R.color.white));

            }
            else
            {
                urlLab.setVisibility(View.GONE);
                url.setVisibility(View.GONE);
            }


            if(temp.getParty().trim().toLowerCase().contains("democratic"))
                setUpDemocraticTheme();
            else if(temp.getParty().trim().toLowerCase().contains("republican"))
                setUpRepublicanTheme();
            else
                setUpNonPartisanTheme();

            loadProfilePicture(temp.getPhotoURL().trim());

            channels = temp.getChannels();

            if( channels.size() > 0 )
            {
                for(Channel single_channel : channels )
                {
                    if(single_channel.getType().equals("Facebook"))
                    {
                        fbHandle = single_channel;
                        fb_icn.setVisibility(View.VISIBLE);

                    }
                    if(single_channel.getType().equals("Twitter"))
                    {
                        twitterHandle = single_channel;
                        twt_icn.setVisibility(View.VISIBLE);

                    }
                    if(single_channel.getType().equals("GooglePlus"))
                    {
                        gplusHandle = single_channel;
                        gp_icn.setVisibility(View.VISIBLE);
                    }
                    if(single_channel.getType().equals("YouTube"))
                    {
                        youtubeHandle = single_channel;
                        ytb_icn.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    public void twitterClicked(View v)
    {
        Intent intent;
        String id = twitterHandle.getId();
        try
        {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + id));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        catch (Exception e)
        {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + id));
        }
        startActivity(intent);

    }

    public void facebookClicked(View v)
    {
        String id = fbHandle.getId();
        String FACEBOOK_URL = "https://www.facebook.com/" + id;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try
        {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850)
            {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
            else
            {
                urlToUse = "fb://page/" + id;
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);

    }

    public void googlePlusClicked(View v)
    {
        String id = gplusHandle.getId();
        Intent intent;
        try
        {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", id);
            startActivity(intent);

        }
        catch (ActivityNotFoundException e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));

        }
    }

    public void youTubeClicked(View v) {
        String id = youtubeHandle.getId();
        Intent intent;
        try
        {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + id));
            startActivity(intent);

        }
        catch (ActivityNotFoundException e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + id)));

        }
    }
    void setUpDemocraticTheme()
    {
        location.setBackgroundResource(R.color.dark_blue);
        cs.setBackgroundResource(R.color.blue);
        dc.setBackgroundResource(R.drawable.dem_details_bg);
        getWindow().setNavigationBarColor(getColor(R.color.blue));
    }

    void setUpRepublicanTheme()
    {
        location.setBackgroundResource(R.color.dark_red);
        cs.setBackgroundResource(R.color.red);
        dc.setBackgroundResource(R.drawable.rep_details_bg);
        getWindow().setNavigationBarColor(getColor(R.color.red));
    }
    void setUpNonPartisanTheme()
    {
        location.setBackgroundResource(R.color.colorPrimaryDark);
        cs.setBackgroundResource(R.color.dark_grey);
        dc.setBackgroundResource(R.drawable.np_details_bg);
        dp.setBackgroundResource(R.drawable.dp_background_non);
        getWindow().setNavigationBarColor(getColor(R.color.dark_grey));
    }

    void loadProfilePicture(String URL)
    {
        if(URL.equals(""))
        {
            dp.setImageResource(R.drawable.default_dp);
        }
        else
        {
            Log.d(TAG, "bp: loadProfilePicture: URL: " + URL);
            Picasso.get().setLoggingEnabled(true);
            Picasso.get()
                    .load(URL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.brokenimage)
                    .fit()
                    .into(dp);
        }
    }

    public void expandImage(View v)
    {
        if(!temp.getPhotoURL().equals(""))
        {
            Intent i = new Intent(this,PhotoDetailActivity.class);
            i.putExtra("location", location.getText());
            i.putExtra("official",temp);
            startActivity(i);
        }
        else
            Toast.makeText(this, "No Profile Picture", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
