package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qriffic.databinding.ProfileCreateBinding;

import java.util.ArrayList;

public class FragmentProfileCreate extends Fragment {
    private ProfileCreateBinding binding;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private TextView textViewUsernameWarning;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = ProfileCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextUsername = view.findViewById(R.id.edit_username);
        editTextEmail = view.findViewById(R.id.edit_email);
        editTextPhone = view.findViewById(R.id.edit_phone);
        textViewUsernameWarning = view.findViewById(R.id.username_warning);

        //fetch secretID from main activity
        String secretID;
        if (getArguments() != null){
            secretID = getArguments().getString("secretID");
            Toast.makeText(getContext(), secretID, Toast.LENGTH_SHORT).show();
        }
        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //write to file, and then navigate back to QRDex

                DBAccessor dba = new DBAccessor();

                PlayerProfile profile = new PlayerProfile();
                profile.addListener(new fetchListener() {
                    @Override
                    public void onFetchComplete() {
                        // the query was able to fetch a PlayerProfile,
                        // so the username is already taken
                        editTextUsername.setText("");
                        textViewUsernameWarning.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFetchFailure() {
                        // the query was unable to fetch a PlayerProfile,
                        // so the username is available
                        PlayerProfile profile = new PlayerProfile(
                            editTextUsername.getText().toString(),
                            null,
                            editTextEmail.getText().toString(),
                            editTextPhone.getText().toString(),
                            0,
                            0,
                            new ArrayList<>());
                        dba.setPlayer(profile);

                        NavHostFragment.findNavController(FragmentProfileCreate.this)
                            .navigate(R.id.action_ProfileCreate_to_QRDex);
                    }
                });

                dba.getPlayer(profile, editTextUsername.getText().toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}