package com.example.qriffic;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FragmentCaptureScreen extends Fragment implements LocationListener {

    private UsernamePersistent usernamePersistent;
    private double currLongitude = 0.0;
    private double currLatitude = 0.0;
    private String currCity = null;
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
    private Button commentDel;
    private Button imageDel;
    private Button locationDel;
    private Boolean locationFlag = false;


    public FragmentCaptureScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Adds slide/fade transitions to the fragment
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
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
                qrCode.addListener(new fetchListener() {
                    @Override
                    public void onFetchComplete() {
                        // go back to the user profile screen
                        NavController controller = Navigation.findNavController(v);
                        controller.popBackStack();
                        controller.popBackStack();
                    }

                    @Override
                    public void onFetchFailure() {
                    }
                });
                uploadToDB();
            }
        });

        // when the user clicks the location photo, open the camera
        // and save the photo as the locationImage Bitmap
        // display the photo in locationImageView
        locationImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if permission is not granted, request permission, if denied, display a toast
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CAMERA}, 1);
                    Toast.makeText(requireContext(), "Please grant camera permission to use this feature",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityResultLauncher.launch(intent);
                }
            }
        });

        geoLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currCity != null) {
                    String monsterLat = String.format("%.2f", qrCode.getGeoLocation().getLatitude());
                    String monsterLong = String.format("%.2f", qrCode.getGeoLocation().getLongitude());
                    String monsterCity = qrCode.getGeoLocation().getCity();
                    String geolocationText = "Latitude: " + monsterLat + "\nLongitude: " +
                            monsterLong + "\nCity: " + monsterCity;
                    geoLocationTextView.setText(geolocationText);
                    geoLocationLayout.setVisibility(View.GONE);
                    geoLocationTextView.setVisibility(View.VISIBLE);
                    locationFlag = true;
                } else {
                    Toast.makeText(requireContext(), "Please enable location services and re-scan the QR code to use this feature",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        commentDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditText.setText("");
            }
        });

        imageDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationImageLayout.setVisibility(View.VISIBLE);
                locationImageView.setVisibility(View.GONE);
                locationImage = null;
            }
        });

        locationDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocationLayout.setVisibility(View.VISIBLE);
                geoLocationTextView.setVisibility(View.GONE);
                locationFlag = false;
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
        commentDel = view.findViewById(R.id.qr_add_comment_del);
        imageDel = view.findViewById(R.id.qr_add_photo_del);
        locationDel = view.findViewById(R.id.qr_add_geo_del);
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
            } else {
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
            } else {
                currCity = "N/A";
            }
        }
    }

    private void createNewQRCode() {
        GeoLocation geoLocation = new GeoLocation(currLatitude, currLongitude, currCity);
        qrCode = new QRCode(rawString, geoLocation, username, null, null);
    }

    private void generateIdenticon(View view) {
        if (Objects.equals(qrCode.getIdHash(), "2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b")) {
            // draw iq.jpg as the identicon
            Drawable iq = ContextCompat.getDrawable(requireActivity(), R.drawable.iq);
            ((ImageView) view.findViewById(R.id.qr_add_image)).setImageDrawable(iq);
        } else {
            String url = "https://www.gravatar.com/avatar/" + qrCode.getIdHash() +
                    "?s=55&d=identicon&r=PG%22";
            Glide.with(getContext())
                    .load(url)
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into((ImageView) view.findViewById(R.id.qr_add_image));
        }
    }

    private void uploadToDB() {
        if (!locationFlag) {
            qrCode.setGeoLocation(new GeoLocation(9999, 9999, "N/A"));
        }
        String locationImageBase64 = bitmapToBase64(locationImage);
        qrCode.setLocationImage(locationImageBase64);
        qrCode.setComment(commentEditText.getText().toString());
        // update player's captured list and QRs collection in DB
        PlayerProfile player = new PlayerProfile();
        player.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                boolean newFlag = player.addQRCode(qrCode);
                if (newFlag) {
                    CharSequence text = "QRMon successfully added!";
                    Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    CharSequence text = "QRMon data has been updated";
                    Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
                    toast.show();
                }
                DBA.addQR(player, qrCode);
            }

            @Override
            public void onFetchFailure() {
            }
        });
        DBA.getPlayer(player, username);


    }

    private void displayUpdatedText() {
        String monsterName = qrCode.getName();
        String monsterScore = String.valueOf(qrCode.getScore());

        String congratsText = "Congrats! You found a new " + monsterName + "! " +
                "What would you like to do?";

        String warningText = "You have already scanned this QR code before. "
                + "If you choose to add it again, you will lose your previous comment, photo,"
                + " and location information.";

        nameTextView.setText(monsterName);
        scoreTextView.setText(monsterScore + "pts");
        congratsTextView.setText(congratsText);
        PlayerProfile player = new PlayerProfile();
        player.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                Collection<QRCode> previousQRCodes = player.getCaptured().values();
                for (QRCode eachQR : previousQRCodes) {
                    if (eachQR.getIdHash().equals(qrCode.getIdHash())) {
                        congratsTextView.setText(warningText);
                        congratsTextView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DF5B5B")));
                    }
                }
            }

            @Override
            public void onFetchFailure() {
            }
        });
        DBA.getPlayer(player, username);
    }

    public String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
