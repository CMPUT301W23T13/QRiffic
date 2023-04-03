package com.example.qriffic;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qriffic.databinding.ProfileCreateBinding;

public class FragmentProfileUpdate extends Fragment {
    private ProfileCreateBinding binding;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private TextView welcomeText;
    private UsernamePersistent usernamePersistent;
    private LinearLayout mainLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Adds transitions
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

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
        postponeEnterTransition();

        mainLayout = view.findViewById(R.id.linearLayout);
        welcomeText = view.findViewById(R.id.welcome_text);
        editTextUsername = view.findViewById(R.id.edit_username);
        editTextUsername.setEnabled(false);
        editTextEmail = view.findViewById(R.id.edit_email);
        editTextPhone = view.findViewById(R.id.edit_phone);

        welcomeText.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mainLayout.getLayoutParams();
        params.verticalBias = 0.4f;
        mainLayout.setLayoutParams(params);

        PlayerProfile profile = new PlayerProfile();
        profile.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                startPostponedEnterTransition();
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

                String email = editTextEmail.getText().toString();
                if (email.equals("")) {
                    email = null;
                }
                String phone = editTextPhone.getText().toString();
                if (phone.equals("")) {
                    phone = null;
                }

                profile.setUsername(editTextUsername.getText().toString());
                profile.setEmail(email);
                profile.setPhoneNum(phone);
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