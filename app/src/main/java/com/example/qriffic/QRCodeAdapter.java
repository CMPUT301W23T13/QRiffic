package com.example.qriffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the ArrayAdapter class and adapts for QRCode objects
 */
public class QRCodeAdapter extends ArrayAdapter<QRCode>{
    public QRCodeAdapter(@NonNull Context context, @NonNull ArrayList<QRCode> objects) {
        super(context, 0, objects );

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
            System.out.println("QRCode is not null");
            TextView nameTextView = view.findViewById(R.id.pListName);
            TextView scoreTextView = view.findViewById(R.id.pListScore);

            nameTextView.setText(qrCode.getName());
            System.out.println(qrCode.getName());
            scoreTextView.setText(String.valueOf(qrCode.getScore()) + "pts");

            // generate QR code image and load
            String hash = qrCode.getIdHash();
            System.out.println("QRCodeAdapter hash: "+hash);
            String url = "https://www.gravatar.com/avatar/" + qrCode.getIdHash() + "?s=55&d=identicon&r=PG%22";
            Glide.with(getContext())
                    .load(url)
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into((ImageView) view.findViewById(R.id.pListImage));

        }
        else {
            System.out.println("QRCodeAdapter QRCode is null at position "+position);
        }


        return view;


    }

}
