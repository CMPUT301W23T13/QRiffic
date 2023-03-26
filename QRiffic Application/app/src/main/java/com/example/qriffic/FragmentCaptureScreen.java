package com.example.qriffic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;

public class FragmentCaptureScreen extends Fragment implements LocationListener {

    private DBAccessor dba;
    private UsernamePersistent usernamePersistent;
    private double currLongitude;
    private double currLatitude;
    private String currCity;
    private QRCode qrCode;
    private String username;
    private String rawString;
    private Button captureButton;
    private Switch trackLocationSwitch;
    private ImageView locationImageView;
    private EditText commentEditText;
    private TextView nameScoreTextView;
    private TextView geoLocationTextView;
    private TextView congratsTextView;
    private Bitmap locationImage;


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

        initLocation();
        initViewsAndValues(view);
        createNewQRCode();
        generateIdenticon(view);
        displayUpdatedText();

        // when the user clicks the capture button, upload the QRCode to the database
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToDB();
                // go back to the user profile screen
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                Navigation.findNavController(view).navigate(R.id.nav_userProfile, bundle);
            }
        });

        // when the user clicks the location photo, open the camera
        // and save the photo as the locationImage Bitmap
        // display the photo in locationImageView
        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            }
        });


        // comments cannot be longer than 128 characters
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // check if the comment is longer than 128 characters
                if (s.length() > 128) {
                    commentEditText.setError("Comments cannot be longer than 128 characters");
                    String truncatedInput = s.subSequence(0, Math.min(s.length(), 128)).toString();
                    commentEditText.setText(truncatedInput);
                    commentEditText.setSelection(truncatedInput.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    locationImage = bitmap;
                    locationImageView.setImageBitmap(bitmap);
                }
            });

    private void initViewsAndValues(View view) {
        Bundle bundle = getArguments();
        //textView = view.findViewById(R.id.textview_qr_code);
        rawString = bundle.getString("barcode_data");
        usernamePersistent = new UsernamePersistent(requireActivity());
        username = usernamePersistent.fetchUsername();
        congratsTextView = view.findViewById(R.id.textview_congrats);
        captureButton = view.findViewById(R.id.button_capture);
        trackLocationSwitch = view.findViewById(R.id.switch_track_location);
        locationImageView = view.findViewById(R.id.imageview_location_image);
        nameScoreTextView = view.findViewById(R.id.textview_name_score);
        commentEditText = view.findViewById(R.id.edittext_comment);
        geoLocationTextView = view.findViewById(R.id.textview_geolocation);
    }

    private void initLocation() {
        // get reference to the LocationManager
        LocationManager locationManager = (LocationManager) requireActivity()
                .getSystemService(getActivity().LOCATION_SERVICE);
        // check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // if not, request permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        // get the current location (required to check for permissions again)
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, FragmentCaptureScreen.this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                currLongitude = location.getLongitude();
                currLatitude = location.getLatitude();
            }else {
                currLongitude = 0;
                currLatitude = 0;
            }
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
    }

    private void createNewQRCode() {
        GeoLocation geoLocation = new GeoLocation(currLatitude, currLongitude, currCity);
        qrCode = new QRCode(rawString, geoLocation, username, null, null);
    }

    private void generateIdenticon(View view) {
        String url = "https://www.gravatar.com/avatar/" + qrCode.getScore() +
                "?s=55&d=identicon&r=PG%22";
        Glide.with(getContext())
                .load(url)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .into((ImageView) view.findViewById(R.id.imageview_qr_code));
    }

    private void uploadToDB() {
        if (!trackLocationSwitch.isChecked()) {
            qrCode.setGeoLocation(new GeoLocation(9999, 9999, "N/A"));
        }
        //qrCode.setLocationImage(locationImage);
        //qrCode.setComment(commentEditText.getText().toString());
        //update QRCode collection in DB
        //DBA.setQR(qrCode.getIdHash(), qrCode);
        // update player's captured list and QRs collection in DB
        dba.addQR(username, qrCode);
    }

    private void displayUpdatedText() {
        String monsterName = qrCode.getName();
        String monsterScore = String.valueOf(qrCode.getScore());
        String monsterLat = String.valueOf(qrCode.getGeoLocation().getLatitude());
        String monsterLong = String.valueOf(qrCode.getGeoLocation().getLongitude());
        String monsterCity = qrCode.getGeoLocation().getCity();

        String nameScoreText = monsterName + "\n" + monsterScore + " pts";
        String congratsText = "Congrats! You found a new " + monsterName + "! " +
                "What would you like to do?";
        String geolocationText = " + Add geolocation\n Lat: " + monsterLat + "\n Long: " +
                monsterLong + "\n City: " + monsterCity;

        nameScoreTextView.setText(nameScoreText);
        congratsTextView.setText(congratsText);
        geoLocationTextView.setText(geolocationText);
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
