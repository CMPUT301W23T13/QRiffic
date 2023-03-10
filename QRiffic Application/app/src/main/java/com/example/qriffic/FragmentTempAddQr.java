package com.example.qriffic;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


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

    private TextView locationText;
    private LocationManager locationManager;
    private double currLongitude;
    private double currLatitude;

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

        // get reference to the button, EditText, and TextView
        Button addQR = view.findViewById(R.id.button_add_qr);
        EditText qrCode = view.findViewById(R.id.editText_enter_qr);
        TextView temp = view.findViewById(R.id.textView_temp);

        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return view;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        onLocationChanged(location);


        // when the button is clicked, the contents of the qrCode EditText is displayed in the temp TextView
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
                    GeoLocation geoLocation = new GeoLocation(currLongitude, currLatitude);
                    tempQR = new QRCode(qrCode.getText().toString(), geoLocation, "Matlock");
                    String hash = tempQR.getIdHash();
                    String last6 = hash.substring(hash.length() - 6);
                    temp.setText("last6hex: " + last6 +
                                "\nname: " + tempQR.getName() +
                                "\nscore: " + Integer.toString(tempQR.getScore()) +
                                "\nlongitude: " + tempQR.getGeoLocation().getLongitude() +
                                "\nlatitude: " + tempQR.getGeoLocation().getLatitude());
                    // update Player's captured ArrayList in database
                    // update QRCode collection in database

                }
            }
        });

        return view;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        currLongitude = longitude;
        currLatitude = latitude;
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
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
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