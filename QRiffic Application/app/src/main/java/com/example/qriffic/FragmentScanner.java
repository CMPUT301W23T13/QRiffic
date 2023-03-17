package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class FragmentScanner extends Fragment {

    private DecoratedBarcodeView barcodeView;

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
        barcodeView = view.findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}