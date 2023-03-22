package com.example.qriffic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTempAddQr#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTempAddQr extends Fragment implements LocationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LocationManager locationManager;
    private double currLongitude;
    private double currLatitude;
    private String currCity;

    public FragmentTempAddQr() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTempAddQr.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTempAddQr newInstance(String param1, String param2) {
        FragmentTempAddQr fragment = new FragmentTempAddQr();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_temp_add_qr, container, false);

        // get username from the bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        String activeUsername = bundle.getString("username");

        DBAccessor dba = new DBAccessor();

        // get reference to the button, EditText, and TextView
        Button addQR = view.findViewById(R.id.button_add_qr);
        EditText qrCode = view.findViewById(R.id.editText_enter_qr);
        TextView temp = view.findViewById(R.id.textView_temp);
        Switch storeQrGeoLocation = view.findViewById(R.id.switch_store_qr_geolocation);

        // get reference to the LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if not, request permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        // when the button is clicked, the contents of the qrCode EditText is displayed in the temp TextView
        // and a QRCode object is created and stored in the database
        addQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the EditText is empty, prompt user to enter something
                if (qrCode.getText().toString().isEmpty()) {
                    temp.setText("Please enter a QR code");
                }
                // otherwise, QRCode object info on screen
                else {
                    QRCode tempQR;
                    // if the switch is on, store the QRCode's geolocation
                    if (storeQrGeoLocation.isChecked()) {

                        // get the current location (check for location permissions first)
                        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, FragmentTempAddQr.this);
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

                        // when switch is set to on, store the QRCode's geolocation
                        // TODO: get current user's username instead of "Matlock"
                        GeoLocation geoLocation = new GeoLocation(currLatitude, currLongitude, currCity);
                        tempQR = new QRCode(qrCode.getText().toString(), geoLocation, activeUsername);
                    } else {
                        // when switch is set to off, store as N/A
                        GeoLocation geoLocation = new GeoLocation(9999, 9999, "N/A");
                        tempQR = new QRCode(qrCode.getText().toString(), geoLocation, activeUsername);
                    }

                    // display QRCode info on screen
                    String hash = tempQR.getIdHash();
                    String last6 = hash.substring(hash.length() - 6);
                    String newText = "last6hex: " + last6 +
                            "\nname: " + tempQR.getName() +
                            "\nscore: " + Integer.toString(tempQR.getScore()) +
                            "\nlongitude: " + tempQR.getGeoLocation().getLongitude() +
                            "\nlatitude: " + tempQR.getGeoLocation().getLatitude() +
                            "\ncity: " + tempQR.getGeoLocation().getCity();
                    temp.setText(newText);
                    // update current player's captured QRCode collection in database
                    dba.addQR(activeUsername, tempQR);

                    // generate QR code image
                    String url = "https://www.gravatar.com/avatar/" + tempQR.getScore() + "?s=55&d=identicon&r=PG%22";
                    Glide.with(getContext())
                            .load(url)
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into((ImageView) view.findViewById(R.id.image_identicon));
                }
            }
        });

        return view;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
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