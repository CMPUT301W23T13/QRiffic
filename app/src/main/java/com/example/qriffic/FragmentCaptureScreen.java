package com.example.qriffic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentCaptureScreen extends Fragment implements LocationListener {

    private UsernamePersistent usernamePersistent;
    private double currLongitude;
    private double currLatitude;
    private String currCity;
    private QRCode qrCode;
    private String username;
    private String rawString;
    private FloatingActionButton captureButton;
    private LinearLayout locationImageLayout;
    private ImageView locationImageView;
    private EditText commentEditText;
    private TextView nameTextView;
    private TextView scoreTextView;
    private LinearLayout geoLocationLayout;
    private TextView geoLocationTextView;
    private TextView congratsTextView;
    private Bitmap locationImage;
    private Boolean locationFlag = false;


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
        View view = inflater.inflate(R.layout.fragment_new_qrmon, container, false);

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
                NavController controller = Navigation.findNavController(v);
                controller.popBackStack();
                controller.popBackStack();
            }
        });

        // when the user clicks the location photo, open the camera
        // and save the photo as the locationImage Bitmap
        // display the photo in locationImageView
        locationImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            }
        });

        geoLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String monsterLat = String.format("%.2f", qrCode.getGeoLocation().getLatitude());
                String monsterLong = String.format("%.2f", qrCode.getGeoLocation().getLongitude());
                String monsterCity = qrCode.getGeoLocation().getCity();
                String geolocationText = "Latitude: " + monsterLat + "\nLongitude: " +
                        monsterLong + "\nCity: " + monsterCity;
                geoLocationTextView.setText(geolocationText);
                geoLocationLayout.setVisibility(View.GONE);
                geoLocationTextView.setVisibility(View.VISIBLE);
                locationFlag = true;
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
                    locationImage = (Bitmap) bundle.get("data");

                    // convert the locationImage to a jpeg
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    locationImage.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] byteArray = stream.toByteArray();
                    locationImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    // print the size of the locationImage in kilobytes
                    System.out.println("locationImage size: " + byteArray.length / 1024 + " kb");

                    // print the width and height of the locationImage in pixels
                    System.out.println("locationImage width: " + locationImage.getWidth());
                    System.out.println("locationImage height: " + locationImage.getHeight());

                    // display the locationImage in locationImageView
                    locationImageView.setImageBitmap(locationImage);
                    locationImageLayout.setVisibility(View.GONE);
                    locationImageView.setVisibility(View.VISIBLE);
                }
            });

    private void initViewsAndValues(View view) {
        Bundle bundle = getArguments();
        rawString = bundle.getString("barcode_data");
        usernamePersistent = new UsernamePersistent(requireActivity());
        username = usernamePersistent.fetchUsername();
        congratsTextView = view.findViewById(R.id.qr_add_congrats);
        captureButton = view.findViewById(R.id.confirm_fab);
        locationImageLayout = view.findViewById(R.id.qr_add_image_button);
        locationImageView = view.findViewById(R.id.qr_add_location_image);
        nameTextView = view.findViewById(R.id.qr_add_name);
        scoreTextView = view.findViewById(R.id.qr_add_score);
        commentEditText = view.findViewById(R.id.qr_add_comment_text);
        geoLocationLayout = view.findViewById(R.id.qr_add_geolocation);
        geoLocationTextView = view.findViewById(R.id.qr_add_geo_text);
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
            if (address.getLocality() != null) {
                currCity = address.getLocality();
            }
            else {
                currCity = "N/A";
            }
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
                .into((ImageView) view.findViewById(R.id.qr_add_image));
    }

    private void uploadToDB() {
        if (!locationFlag) {
            qrCode.setGeoLocation(new GeoLocation(9999, 9999, "N/A"));
        }
        String locationImageBase64 = bitmapToBase64(locationImage);
        qrCode.setLocationImage(locationImageBase64);
        qrCode.setComment(commentEditText.getText().toString());
        // update player's captured list and QRs collection in DB
        DBA.addQR(username, qrCode);
    }

    private void displayUpdatedText() {
        String monsterName = qrCode.getName();
        String monsterScore = String.valueOf(qrCode.getScore());

        String congratsText = "Congrats! You found a new " + monsterName + "! " +
                "What would you like to do?";

        nameTextView.setText(monsterName);
        scoreTextView.setText(monsterScore);
        congratsTextView.setText(congratsText);
    }

    public String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
