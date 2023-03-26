package com.example.qriffic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This fragment is used to display the details of a QR code and a feed from all
 * users who have scanned the QR code.
 */
public class FragmentQRDetail extends Fragment {

    ListView qrDetailList;

    public FragmentQRDetail() {
        // Required empty public constructor
    }


    /**
     * This method creates the view of the fragment.
     * @param inflater The inflater of the fragment.
     * @param container The container of the fragment.
     * @param savedInstanceState The saved instance state of the fragment.
     * @return The view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        QRData instance = new QRData();
        String QRID = getArguments().getString("QRID");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_detail, container, false);

        // Hide the delete button if the user is not the main user
        FloatingActionButton deleteButton = view.findViewById(R.id.fab_delete_qr);
        if (!getArguments().getBoolean("isUser")) {
            deleteButton.setVisibility(View.GONE);
        }

        // Set the delete button to delete the QR code from the database and navigate away
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBAccessor dba = new DBAccessor();
                QRCode qrCode = new QRCode();
                qrCode.setIdHash(QRID);
                UsernamePersistent usernamePersistent = new UsernamePersistent(getContext());
                qrCode.setUsername(usernamePersistent.fetchUsername());
                dba.deleteQR(qrCode);
                getActivity().onBackPressed();
            }
        });

        //fetch the QR data from the database
        DBAccessor dba = new DBAccessor();
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
        dba.getQRData(qrData, QRID);

        return view;
    }

    /**
     * This method sets the main image of the QR code.
     * @param view The view of the fragment.
     * @param instance The QRData object that contains the image.
     */
    private void setMainImage(View view, QRData instance) {
        String highurl = "https://www.gravatar.com/avatar/" + instance.getScore() + "?s=55&d=identicon&r=PG%22";
        Glide.with(getContext())
                .load(highurl)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .into((ImageView) view.findViewById(R.id.qr_detail_image));
    }

    /**
     * This method sets the name and score of the QR code.
     * @param view The view of the fragment.
     * @param instance The QRData object that contains the name and score.
     */
    private void setScoreAndName(View view, QRData instance) {
        TextView name = view.findViewById(R.id.qr_detail_name);
        TextView score = view.findViewById(R.id.qr_detail_score);
        name.setText(instance.getName());
        score.setText(String.format("%d", instance.getScore()));
    }


    /**
     * This method populates the list of users who have scanned the QR code.
     * @param view The view of the fragment.
     * @param instance The QRData object that contains the list of users.
     */
    private void populateList(View view, QRData instance) {
        qrDetailList = view.findViewById(R.id.qr_detail_list);
        ArrayList<HashMap<String,Object>> instanceList = new ArrayList<>();

        HashMap<String, HashMap<String, Object>> users = instance.getUsers();
        users.forEach((key, value) -> {
            instanceList.add(value);
        });

        QRDetailAdapter instanceAdapter = new QRDetailAdapter(getContext(), instanceList);
        qrDetailList.setAdapter(instanceAdapter);
    }

}