package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qriffic.databinding.ProfileCreateBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FragmentProfileCreate extends Fragment {
    private ProfileCreateBinding binding;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private TextView textViewUsernameWarning;
    private UsernamePersistent usernamePersistent;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = ProfileCreateBinding.inflate(inflater, container, false);
        usernamePersistent = new UsernamePersistent(getActivity().getApplicationContext());

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        editTextUsername = view.findViewById(R.id.edit_username);
        editTextEmail = view.findViewById(R.id.edit_email);
        editTextPhone = view.findViewById(R.id.edit_phone);
        textViewUsernameWarning = view.findViewById(R.id.username_warning);

        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //write to file, and then navigate back to QRDex

                if (editTextUsername.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                            new ArrayList<>());
                        dba.setPlayer(profile);

                        usernamePersistent.saveUsername(profile.getUsername());

                        //todo: this should be unnecessary now that we're using a persistent username
                        Bundle bundle = new Bundle();
                        bundle.putString("username", profile.getUsername());
                        //


                        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                        NavHostFragment.findNavController(FragmentProfileCreate.this)
                            .navigate(R.id.action_ProfileCreate_to_userProfile, bundle);
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