package com.example.qriffic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This fragment is used to display the details of a QR code.
 */
public class Fragment_QR_Detail extends Fragment {

    ListView qrDetailList;
    QRDetailFeed instanceAdapter;
    private ArrayList<QRCode> instanceList;

    public Fragment_QR_Detail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String toastString = getArguments().getString("QRID");
            Toast.makeText(getContext(), "ID is" + toastString, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_detail, container, false);
        qrDetailList = view.findViewById(R.id.qr_detail_list);
        instanceList = new ArrayList<>();

        FirebaseApp.initializeApp(getContext());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("qr")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        instanceList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            QRCode qrCode = doc.toObject(QRCode.class);
                            instanceList.add(qrCode);
                        }
                        instanceAdapter.notifyDataSetChanged();
                    }
                });


        instanceAdapter = new QRDetailFeed(getContext(), instanceList);
        qrDetailList.setAdapter(instanceAdapter);
        return view;
    }

}