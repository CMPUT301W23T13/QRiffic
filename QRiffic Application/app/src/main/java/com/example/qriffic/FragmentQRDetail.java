package com.example.qriffic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This fragment is used to display the details of a QR code.
 */
public class FragmentQRDetail extends Fragment {

    ListView qrDetailList;

    public FragmentQRDetail() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        QRData instance = new QRData();

        if (getArguments() != null) {
            //get the proper thing from the user profile bundle, whatever that ends up being
            String toastString = getArguments().getString("QRID");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_detail, container, false);

        //fetch the QR data from the database
        String QRID = getArguments().getString("QRID");
        QRData qrData = new QRData();
        qrData.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                instance.setIdHash(qrData.getIdHash());
                instance.setName(qrData.getName());
                instance.setScore(qrData.getScore());
                instance.setUsers(qrData.getUsers());

                setMainImage(view, instance);
                setScoreAndName(view, instance);
                populateList(view, instance);
            }
            @Override
            public void onFetchFailure() {
                Toast.makeText(getContext(), "Failed to fetch QR data", Toast.LENGTH_SHORT).show();
            }
        });
        DBA.getQRData(qrData, QRID);

        return view;
    }

    private void setMainImage(View view, QRData instance) {
        String highurl = "https://www.gravatar.com/avatar/" + instance.getScore() + "?s=55&d=identicon&r=PG%22";
        Glide.with(getContext())
                .load(highurl)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .into((ImageView) view.findViewById(R.id.qr_detail_image));
    }

    private void setScoreAndName(View view, QRData instance) {
        TextView name = view.findViewById(R.id.qr_detail_name);
        TextView score = view.findViewById(R.id.qr_detail_score);
        name.setText(instance.getName());
        score.setText(String.format("%d", instance.getScore()));
    }

    private void populateList(View view, QRData instance) {
        qrDetailList = view.findViewById(R.id.qr_detail_list);
        ArrayList<HashMap<String,Object>> instanceList = new ArrayList<>();

        HashMap<String, HashMap<String, Object>> users = instance.getUsers();
        users.forEach((key, value) -> {
            instanceList.add(value);
        });


//        FirebaseApp.initializeApp(getContext());
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection("QRs").document(instance.getIdHash())
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        instanceList.clear();
//
//                        //Change this code to modify the data that is queried from the database
//                        for (QueryDocumentSnapshot doc : value) {
//                            QRCode qrCode = doc.toObject(QRCode.class);
//                            instanceList.add(qrCode);
//                        }
//
//                        instanceAdapter.notifyDataSetChanged();
//                    }
//                });
        QRDetailAdapter instanceAdapter = new QRDetailAdapter(getContext(), instanceList);
        qrDetailList.setAdapter(instanceAdapter);
    }

}