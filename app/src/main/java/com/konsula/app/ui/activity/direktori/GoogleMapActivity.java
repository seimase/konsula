package com.konsula.app.ui.activity.direktori;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.konsula.app.R;


/**
 * Created by konsula on 2/16/2016.
 */
public class GoogleMapActivity extends AppCompatActivity {

    private int width, height;
    private GoogleMap map;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        backButton = (ImageButton) findViewById(R.id.appointment_done_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSreenDimanstions();

        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            map.addMarker(new MarkerOptions().position(new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude")))
                    .title("" + getIntent().getExtras().getString("title"))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
            LatLng temp = new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(temp, 13);
            map.animateCamera(cameraUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSreenDimanstions()
    {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }

    private void initilizeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_LONG)
                        .show();
            }
        }

        // set map type
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(-6.211544, 106.845172));
        map.moveCamera(point);
    }
}
