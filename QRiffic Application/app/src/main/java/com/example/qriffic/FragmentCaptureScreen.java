package com.example.qriffic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FragmentCaptureScreen extends Fragment implements LocationListener {

    public FragmentCaptureScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture_screen, container, false);

        // get reference to the LocationManager
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if not, request permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Bundle bundle = getArguments();
        TextView textView = view.findViewById(R.id.textview_qr_code);
        String rawString = bundle.getString("barcode_data");
        String username = bundle.getString("username");
        double currLongitude = 9999;
        double currLatitude = 9999;
        String currCity = "Unknown";

        // get the current location (check for location permissions first)
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, FragmentCaptureScreen.this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currLongitude = location.getLongitude();
            currLatitude = location.getLatitude();
        }

        // get the city name from longitude and latitude
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(currLatitude, currLongitude, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            currCity = address.getLocality();
        }

        // create a new QRCode object
        GeoLocation geoLocation = new GeoLocation(currLatitude, currLongitude, currCity);
        QRCode tempQR = new QRCode(rawString, geoLocation, username);

        // display QRCode info on screen
        String hash = tempQR.getIdHash();
        String last6 = hash.substring(hash.length() - 6);
        String newText = "last6hex: " + last6 +
                "\nname: " + tempQR.getName() +
                "\nscore: " + tempQR.getScore() +
                "\nlongitude: " + tempQR.getGeoLocation().getLongitude() +
                "\nlatitude: " + tempQR.getGeoLocation().getLatitude() +
                "\ncity: " + tempQR.getGeoLocation().getCity();
        textView.setText(newText);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
