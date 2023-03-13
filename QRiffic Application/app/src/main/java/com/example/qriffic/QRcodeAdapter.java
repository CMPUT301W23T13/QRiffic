package com.example.qriffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QRcodeAdapter extends ArrayAdapter<QRCode>{

    private Context context;
    private List<QRCode> qrCodes = new ArrayList<>();
    private int resource;
//    private String resourceId;


    public QRcodeAdapter(@NonNull Context context, @NonNull ArrayList<QRCode> objects) {
        super(context, 0, objects );
//        this.context = context;
//        this.qrCodes = objects;
//        this.resource = resource;
//        this.resourceId = textViewResourceId;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.profile_qr_list,
                    parent, false);
        } else {
            view = convertView;
        }

        QRCode qrCode = getItem(position);
        if (qrCode != null) {
            TextView nameTextView = view.findViewById(R.id.pListName);
            TextView scoreTextView = view.findViewById(R.id.pListScore);

            nameTextView.setText(qrCode.getName());
            scoreTextView.setText(String.valueOf(qrCode.getScore()));
        }

        return view;


    }

}
