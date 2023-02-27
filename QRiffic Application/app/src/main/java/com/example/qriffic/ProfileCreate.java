package com.example.qriffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.example.qriffic.databinding.ProfileCreateBinding;

public class ProfileCreate extends Fragment {

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

        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                NavHostFragment.findNavController(ProfileCreate.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);

            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}