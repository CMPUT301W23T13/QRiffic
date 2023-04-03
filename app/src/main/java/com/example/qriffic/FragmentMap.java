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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleMap map;
    MapView mapView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Marker marker;
    private boolean mLocationPermissionGranted = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Check if user has granted location permission
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.ACCESS_FINE_LOCATION}, 1);

        }

    }

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
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

        // Enable the "My Location" button on the map
        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                map.setMyLocationEnabled(true);
            }
        } else {
            map.setMyLocationEnabled(false);
        }

//        map.setMyLocationEnabled(true);
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

        //initialize an array for storing qr
        List<QRData> QRData = new ArrayList<QRData>();


        // Get the list of locations from the database
        //initialize an array for storing qr

        List<String> idHash = new ArrayList<String>();
        QRCode qrCode = new QRCode();

        HashMap<String, Object> data = new HashMap<>();


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
                                            HashMap<String, Object> user = (HashMap<String, Object>) qrData.getUsers().get(key);
//
                                            //get the lat and lon
                                            HashMap<String, Object> geoLocation = (HashMap<String, Object>) user.get("geoLocation");

//

                                            //convert all the value to double
                                            Double latitude = (Double) geoLocation.get("latitude");
                                            Double longitude = (Double) geoLocation.get("longitude");


                                            //add the lat and lon to the markerLatLngList
                                            List<LatLng> markerLatLngList = new ArrayList<>();

                                            //if lat long != 9999.0 and !=0


                                            if ((latitude != 9999.0 && longitude != 9999.0) && (latitude != 0.0 && longitude != 0.0)) {
                                                markerLatLngList.add(new LatLng(longitude, latitude));
                                            }
                                            // Add a marker for each LatLng using a loop
                                            for (LatLng latLng : markerLatLngList) {
                                                marker = map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                                                marker.setTag(id);

                                                //marker click listener
                                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                    @Override
                                                    public boolean onMarkerClick(@NonNull Marker marker) {
                                                        System.out.println("marker id" + marker.getTag());

                                                        //make bundle for id
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("QRID", marker.getTag().toString());
                                                        System.out.println("bundle id" + bundle.getString("QRID"));
                                                        //navigate to qr detail fragment
                                                        View view = getView();

                                                        Navigation.findNavController(view).navigate(R.id.action_nav_map_to_nav_QRDetail, bundle);


                                                        return false;
                                                    }
                                                });


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
                                DBA.getQRData(qrData, id);


                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    if (ContextCompat.checkSelfPermission(getContext(), permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }
                } else {

                    mLocationPermissionGranted = false;

                }
            }
        }
    }


    @Override
    public void onMyLocationClick(@NonNull android.location.Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();


    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Toast.makeText(getContext(), "Marker clicked", Toast.LENGTH_SHORT).show();
        return false;
    }
}