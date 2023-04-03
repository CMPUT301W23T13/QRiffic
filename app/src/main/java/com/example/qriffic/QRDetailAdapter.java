package com.example.qriffic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class extends the ArrayAdapter class and adapts a ListView in FragmentQRDetail for QRmons represented by a map of their idHash and QRCode data
 */
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
        Bitmap bitmap = null;
        if (locationImage != null) {
            bitmap = base64ToBitmap(locationImage.toString());
            qrDetailLocationImage.setImageBitmap(bitmap);
        } else {
            qrDetailLocationImage.setVisibility(View.GONE);
        }

        if (PID != null) {
            qrDetailListPID.setText(PID.toString());
        } else {
            qrDetailListPID.setText("PID Missing");
        }

        if (comment != null) {
            if (comment != "") {
                qrDetailComment.setText(comment.toString());
            } else {
                qrDetailComment.setText("This user captured this QRMon!");
            }
        } else {
            qrDetailComment.setText("This user captured this QRMon!");
            qrDetailComment.setTextColor(Color.parseColor("#b5b5b5"));
        }

        if (geoLocation != null) {
            HashMap<String, Object> location = (HashMap<String, Object>) geoLocation;
            String locString = location.get("city").toString();
            if (locString.equals("N/A")) {
                view.findViewById(R.id.qr_detail_location_info).setVisibility(View.GONE);
                qrDetailLocationText.setVisibility(View.GONE);
            } else { qrDetailLocationText.setText(locString);}
        } else {
            qrDetailLocationText.setText("NULL LOCATION");
        }

        return view;
    }

    public Bitmap base64ToBitmap(String base64String) {
        if (base64String == null) {
            return null;
        }
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
