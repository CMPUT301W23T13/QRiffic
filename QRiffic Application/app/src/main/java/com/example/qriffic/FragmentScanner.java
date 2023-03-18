package com.example.qriffic;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class FragmentScanner extends Fragment {

    private DecoratedBarcodeView barcodeView;

    // camera permission stuff (see onResume())
    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    barcodeView.resume();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                }
            });

    private BarcodeCallback callback = new BarcodeCallback() {

        // This method is invoked when a barcode is detected
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result != null) {
                barcodeView.pause();
                barcodeView.setVisibility(View.GONE);
                barcodeView.getBarcodeView().pause();
                barcodeView.getBarcodeView().stopDecoding();
                Fragment fragment = new FragmentCaptureScreen();
                Bundle bundle = new Bundle();
                bundle.putString("barcode_data", result.getText());
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.capture_layout, fragment).commit();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

    // check if the app has permission to access the device's location for QRCode generation
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if not, request permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        barcodeView = view.findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermission.launch(Manifest.permission.CAMERA);
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}