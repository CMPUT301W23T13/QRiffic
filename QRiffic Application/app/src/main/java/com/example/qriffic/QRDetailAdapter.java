package com.example.qriffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class QRDetailAdapter extends ArrayAdapter<QRCode> {
    private ArrayList<QRCode> instanceList;
    private Context context;

    public QRDetailAdapter(Context context, ArrayList<QRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.instanceList = qrCodes;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.qr_detail_list, parent, false);
        }

        QRCode instance = instanceList.get(position);

        TextView qrDetailListPID = view.findViewById(R.id.qr_detail_PID_text);
        TextView qrDetailComment = view.findViewById(R.id.qr_detail_comment_text);
        ImageView qrDetailLocationImage = view.findViewById(R.id.qr_detail_location_image);
        TextView qrDetailLocationText = view.findViewById(R.id.qr_detail_location_text);

        //replace with actual data once that is figured out
        qrDetailListPID.setText(instance.getIdHash());
        qrDetailComment.setText(instance.getIdHash());
        qrDetailLocationImage.setImageResource(R.drawable.ic_launcher_background);
        qrDetailLocationText.setText(instance.getIdHash());


        return view;
    }
}
