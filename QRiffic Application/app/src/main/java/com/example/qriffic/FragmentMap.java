package com.example.qriffic;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleMap map;
    MapView mapView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        //return view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync((OnMapReadyCallback) this);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //add zoom control
        map.getUiSettings().setZoomControlsEnabled(true);
        //add compass
        map.getUiSettings().setCompassEnabled(true);
        //add my location button
//        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        //enable my location
        if (ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                // Remove the listener to prevent position updates continuously
                map.setOnMyLocationChangeListener(null);
            }
        });








        // Get the list of locations from the database

        dba = new DBAccessor();
        //initialize an array for storing qr
        List<QRData> QRData = new ArrayList<QRData>();






        // Get the list of locations from the database
        //initialize an array for storing qr

        List<String> idHash = new ArrayList<String>();
        QRCode qrCode = new QRCode();

        HashMap<String,Object> data = new HashMap<>();





        db.collection("QRs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

//
                                String id = (String) document.getData().get("idHash");
//

                                QRData qrData = new QRData();
                                //make a list for lat and lon
                                List<Double> lat = new ArrayList<Double>();
                                List<Double> lon = new ArrayList<Double>();

                                qrData.addListener(new fetchListener() {
                                    @Override
                                    public void onFetchComplete() {


                                        //get the value from hashmap Users without knowing the key

                                        for (String key : qrData.getUsers().keySet()) {
//
                                            HashMap<String,Object> user = (HashMap<String, Object>) qrData.getUsers().get(key);
//
                                            //get the lat and lon
                                            HashMap<String,Object> geoLocation = (HashMap<String, Object>) user.get("geoLocation");

//

                                            //convert all the value to double
                                            Double latitude = (Double) geoLocation.get("latitude");
                                            Double longitude = (Double) geoLocation.get("longitude");


                                           //add the lat and lon to the markerLatLngList
                                            List<LatLng> markerLatLngList = new ArrayList<>();

                                            //if lat long != 9999.0
                                            if(latitude != 9999.0 && longitude != 9999.0){
                                                markerLatLngList.add(new LatLng(longitude,latitude));
                                            }
                                            // Add a marker for each LatLng using a loop
                                            for (LatLng latLng : markerLatLngList) {
                                                map.addMarker(new MarkerOptions().position(latLng));
                                            }

                                            //move the camera to the first marker
                                            CameraUpdateFactory.newLatLng(new LatLng((Double) geoLocation.get("longitude"), (Double) geoLocation.get("latitude")));
                                            CameraUpdateFactory.zoomTo(15);

                                        }

                                    }

                                    @Override
                                    public void onFetchFailure() {

                                    }
                                });
                                dba.getQRData(qrData, id);


                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });





    }


    @Override
    public void onMyLocationClick(@NonNull android.location.Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}