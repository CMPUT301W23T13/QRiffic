package com.example.qriffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class QRDetailAdapter extends ArrayAdapter<HashMap<String, Object>> {
    private ArrayList<HashMap<String,Object>> instanceList;
    private Context context;

    public QRDetailAdapter(Context context, ArrayList<HashMap<String,Object>> dataByUser) {
        super(context, 0, dataByUser);
        this.instanceList = dataByUser;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.qr_detail_list, parent, false);
        }

        HashMap<String,Object> instance = instanceList.get(position);

        TextView qrDetailListPID = view.findViewById(R.id.qr_detail_PID_text);
        TextView qrDetailComment = view.findViewById(R.id.qr_detail_comment_text);
        ImageView qrDetailLocationImage = view.findViewById(R.id.qr_detail_location_image);
        TextView qrDetailLocationText = view.findViewById(R.id.qr_detail_location_text);

        //replace with actual data once that is figured out
        Object PID = instance.get("username");
        Object comment = instance.get("comment");
        Object locationImage = instance.get("locationImage");
        Object geoLocation = instance.get("geoLocation");

        if (PID != null) {
            qrDetailListPID.setText(PID.toString());
        } else {
            qrDetailListPID.setText("PID Missing");
        }

        if (comment != null) {
            qrDetailComment.setText(comment.toString());
        } else {
            qrDetailComment.setText("");
        }

        if (locationImage != null) {
            // TODO: 2023-03-25 replace with actual image
            qrDetailLocationImage.setImageResource(R.drawable.ic_launcher_background);
        } else {
            // TODO: 2023-03-25 replace with setting view visibility to gone
            qrDetailLocationImage.setImageResource(R.drawable.ic_launcher_background);
        }

        if (geoLocation != null) {
            HashMap<String, Object> location = (HashMap<String, Object>) geoLocation;
            String locString = location.get("city").toString();
            if (locString.equals("N/A")) {
                view.findViewById(R.id.qr_detail_location_icon).setVisibility(View.GONE);
                qrDetailLocationText.setVisibility(View.GONE);
            } else { qrDetailLocationText.setText(locString);}
        } else {
            qrDetailLocationText.setText("NULL LOCATION");
        }

        qrDetailLocationImage.setImageResource(R.drawable.ic_launcher_background);

        return view;
    }
}
