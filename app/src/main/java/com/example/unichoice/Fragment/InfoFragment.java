package com.example.unichoice.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unichoice.DataClass;
import com.example.unichoice.databinding.FragmentInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;

    private void readData(String collegeName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("College");
        reference.child(collegeName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
//                        Toast.makeText(getContext(), "Successfully Read", Toast.LENGTH_SHORT).show();
                        DataSnapshot dataSnapshot = task.getResult();
                        String info = String.valueOf(dataSnapshot.child("Info").getValue());
                        binding.info.setText(info);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
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
