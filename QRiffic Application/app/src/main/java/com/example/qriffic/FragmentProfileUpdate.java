package com.example.qriffic;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qriffic.databinding.ProfileCreateBinding;

public class FragmentProfileUpdate extends Fragment {
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
        binding.enter.setText("Update");

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextUsername = view.findViewById(R.id.edit_username);
        editTextUsername.setEnabled(false);
        editTextEmail = view.findViewById(R.id.edit_email);
        editTextPhone = view.findViewById(R.id.edit_phone);
        textViewUsernameWarning = view.findViewById(R.id.username_warning);

        PlayerProfile profile = new PlayerProfile();
        profile.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                editTextUsername.setText(profile.getUsername());
                editTextEmail.setText(profile.getEmail());
                editTextPhone.setText(profile.getPhoneNum());
            }

            @Override
            public void onFetchFailure() {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        DBA.getPlayer(profile, usernamePersistent.fetchUsername());

        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //write to file, and then navigate back to QRDex

                if (editTextUsername.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextEmail.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                This block references the following stackoverflow post:
                Link: https://stackoverflow.com/questions/1391970/how-to-convert-a-string-to-charsequence
                Author: Ruthwik
                Date: 27/03/2023
                 */
                if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
                    Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextPhone.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.PHONE.matcher(editTextPhone.getText().toString()).matches()) {
                    Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }


                profile.setUsername(editTextUsername.getText().toString());
                profile.setEmail(editTextEmail.getText().toString());
                profile.setPhoneNum(editTextPhone.getText().toString());
                DBA.updateContactInfo(profile);

                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}