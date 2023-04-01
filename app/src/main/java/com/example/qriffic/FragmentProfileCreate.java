package com.example.qriffic;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qriffic.databinding.ProfileCreateBinding;

import java.util.HashMap;

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

        ((MainActivity)getActivity()).hideToolbar();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

                if (editTextUsername.getText().toString().length() > 16) {
                    Toast.makeText(getContext(), "Username must be less than 16 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                This block references the following stackoverflow post:
                Link: https://stackoverflow.com/questions/1391970/how-to-convert-a-string-to-charsequence
                Author: Ruthwik
                Date: 27/03/2023
                 */
                if (editTextEmail.getText().length() > 0
                        && !Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
                    Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextPhone.getText().length() > 0
                        && !Patterns.PHONE.matcher(editTextPhone.getText().toString()).matches()) {
                    Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

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

                        String email = editTextEmail.getText().toString();
                        if (email.equals("")) {
                            email = null;
                        }
                        String phone = editTextPhone.getText().toString();
                        if (phone.equals("")) {
                            phone = null;
                        }

                        PlayerProfile profile = new PlayerProfile(
                            editTextUsername.getText().toString(),
                            null,
                            email,
                            phone,
                            new HashMap<String, QRCode>());
                        DBA.setPlayer(profile);

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

                DBA.getPlayer(profile, editTextUsername.getText().toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ((MainActivity)getActivity()).showToolbar();
    }


}