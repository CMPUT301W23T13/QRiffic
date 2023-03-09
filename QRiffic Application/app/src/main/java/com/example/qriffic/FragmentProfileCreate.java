package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qriffic.databinding.ProfileCreateBinding;

public class FragmentProfileCreate extends Fragment {
    private ProfileCreateBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = ProfileCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

                NavHostFragment.findNavController(FragmentProfileCreate.this)
                        .navigate(R.id.action_ProfileCreate_to_QRDex);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}