package com.example.bt4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SwipeRefreshLayout swiper;
    private RecyclerView rv;
    private TextView location, no_location;
    private ArrayList<Official> officialArrayList = new ArrayList<>();
    private OfficialAdapter officialAdapter;
    private static final String TAG = "MainActivity";
    private  static volatile String ConvertedAddress;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupComponents();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                getLocation();
                swiper.setRefreshing(false);
            }
        });
        officialAdapter = new OfficialAdapter(officialArrayList, this);
        rv.setAdapter(officialAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

    }
    public void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                try {
                                    convert(location);
                                    if (!ConvertedAddress.equals(""))
                                    {
                                        new OfficialLoader(MainActivity.this).execute(ConvertedAddress);
                                    }
                                    else
                                    {
                                        LocationDialog(getString(R.string.locationErrorMsg1), 0);
                                        Log.i("ConvertedPostal", ConvertedAddress);
                                    }
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
        }
    }
    public void setupComponents()
    {
        swiper = findViewById(R.id.swiper);
        rv = findViewById(R.id.recycler);
        location = findViewById(R.id.location);
        no_location = findViewById(R.id.location404);
        ConvertedAddress = "";
    }
    public void convert(Location l) throws IOException, InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                    String cityName = addresses.get(0).getLocality();
                    String PostalCode = addresses.get(0).getPostalCode();
                    String stateName = addresses.get(0).getAdminArea();
                    ConvertedAddress = PostalCode;
                    Log.i("DuringThreadP", ConvertedAddress);
                    location.setText(cityName + "," + stateName+ "," + PostalCode);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();
    }

    private void LocationDialog(String message, int dismissFlag)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();

        if (dismissFlag == 0)
        {
            builder.setIcon(R.drawable.ic_location_error);
            builder.setTitle("Location Error!");
            builder.setMessage(message);
            dialog = builder.create();
            dialog.show();
        }
        else if (dismissFlag == 1)
            dialog.dismiss();
    }


    public void updateOfficialData(ArrayList<Official> tempList)
    {
        officialArrayList.clear();
        if(tempList.size()!=0)
        {
            officialArrayList.addAll(tempList);
            no_location.setVisibility(View.GONE);
        }
        else
        {
            location.setText(getText(R.string.no_locs));
            no_location.setVisibility(View.VISIBLE);
        }
        officialAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.opt_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                search();
                break;
            case R.id.about:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void search()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);
        builder.setIcon(R.drawable.ic_search_accent);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String searchString = et.getText().toString().trim();
                if(!searchString.equals(""))
                {
                    location.setText("");
                    new OfficialLoader(MainActivity.this).execute(searchString);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please Enter Location!", Toast.LENGTH_SHORT).show();
                    search();
                }
            }
        });
        builder.setMessage(R.string.searchMsg);
        builder.setTitle(R.string.searchTitle);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view)
    {
        int position = rv.getChildAdapterPosition(view);
        Official temp = officialArrayList.get(position);
        Intent i = new Intent(this,OfficialActivity.class);
        i.putExtra("location", location.getText());
        i.putExtra("official",temp);
        startActivity(i);

    }
    @Override
    protected void onPause()
    {
        super.onPause();
    }


}
