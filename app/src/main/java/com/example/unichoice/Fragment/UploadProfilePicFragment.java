package com.example.unichoice.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unichoice.R;
import com.example.unichoice.databinding.FragmentUploadProfilePic2Binding;

public class UploadProfilePicFragment extends Fragment {
    private FragmentUploadProfilePic2Binding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUploadProfilePic2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}