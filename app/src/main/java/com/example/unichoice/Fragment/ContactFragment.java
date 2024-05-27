package com.example.unichoice.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.unichoice.DataClass;
import com.example.unichoice.R;
import com.example.unichoice.databinding.FragmentContactBinding;
import com.example.unichoice.databinding.FragmentInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactFragment extends Fragment {
FragmentContactBinding binding;
    private void readData(String collegeName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("College");
        reference.child(collegeName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
//                        Toast.makeText(getContext(), "Successfully Read", Toast.LENGTH_SHORT).show();
                        DataSnapshot dataSnapshot = task.getResult();
                        String mob = String.valueOf(dataSnapshot.child("Mobile").getValue());
                        String mail = String.valueOf(dataSnapshot.child("Email").getValue());
                        String add = String.valueOf(dataSnapshot.child("Address").getValue());
                        binding.mobile.setText(mob);
                        binding.email.setText(mail);
                        binding.address.setText(add);
                    } else {
                        Toast.makeText(getContext(), "College Doesn't Exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to read data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String collegeName =DataClass.getCollegeName2();
        if (collegeName == null || collegeName.isEmpty()) {
            Toast.makeText(getActivity(), "College name is not available", Toast.LENGTH_LONG).show();
        } else {
            readData(collegeName);
        }
    }
}