package com.example.qriffic;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Objects;

public class FragmentScanner extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    private DecoratedBarcodeView barcodeView;

    // camera permission stuff (see onResume())
    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    barcodeView.resume();
                } else {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(requireContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                        NavController controller = Navigation.findNavController(getView());
                        controller.popBackStack();
                    }
                }
            });

    private BarcodeCallback callback = new BarcodeCallback() {

        // This method is invoked when a barcode is detected
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result != null) {
                barcodeView.pause();
                barcodeView.getBarcodeView().pause();
                barcodeView.getBarcodeView().stopDecoding();
                Bundle bundle = new Bundle();
                bundle.putString("barcode_data", result.getText());
                Navigation.findNavController(getView()).navigate(R.id.nav_capture_screen, bundle);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        // camera permission handling is done in onResume()
        barcodeView = view.findViewById(R.id.barcode_scanner);
        barcodeView.setStatusText("");
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