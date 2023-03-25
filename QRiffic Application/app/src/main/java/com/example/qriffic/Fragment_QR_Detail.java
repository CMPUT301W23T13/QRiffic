package com.example.qriffic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    QRDetailAdapter instanceAdapter;
    QRData instance;
    private ArrayList<QRCode> instanceList;

    public Fragment_QR_Detail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String QRID = getArguments().getString("QRID");
        Toast.makeText(getContext(), "QRCode is " + QRID, Toast.LENGTH_SHORT).show();
        DBAccessor dba = new DBAccessor();
        QRData qrData = new QRData();
        qrData.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                instance = qrData;
            }
            @Override
            public void onFetchFailure() {
                Toast.makeText(getContext(), "Failed to fetch QR data", Toast.LENGTH_SHORT).show();
            }
        });
        dba.getQRData(qrData, QRID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            //get the proper thing from the user profile bundle, whatever that ends up being
            String toastString = getArguments().getString("QRID");
            Toast.makeText(getContext(), "ID is" + toastString, Toast.LENGTH_SHORT).show();
        }


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_detail, container, false);


        setMainImage(view);
        setScoreAndName(view);
        populateList(view);


        return view;
    }

    private void setMainImage(View view) {
        String highurl = "https://www.gravatar.com/avatar/" + "FILL ME IN WITH A CORRECT REFERENCE TO THE QRMON'S SCORE" + "?s=55&d=identicon&r=PG%22";
        Glide.with(getContext())
                .load(highurl)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .into((ImageView) view.findViewById(R.id.qr_detail_image));
    }

    private void setScoreAndName(View view) {
        TextView name = view.findViewById(R.id.qr_detail_name);
        TextView score = view.findViewById(R.id.qr_detail_score);
        name.setText("TEST QR MON NAME");
        score.setText("9999999999");
    }

    private void populateList(View view) {
        qrDetailList = view.findViewById(R.id.qr_detail_list);
        instanceList = new ArrayList<>();

        FirebaseApp.initializeApp(getContext());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("qr")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        instanceList.clear();

                        //Change this code to modify the data that is queried from the database
                        for (QueryDocumentSnapshot doc : value) {
                            QRCode qrCode = doc.toObject(QRCode.class);
                            instanceList.add(qrCode);
                        }

                        instanceAdapter.notifyDataSetChanged();
                    }
                });
        instanceAdapter = new QRDetailAdapter(getContext(), instanceList);
        qrDetailList.setAdapter(instanceAdapter);
    }

}