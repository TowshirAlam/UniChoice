package com.example.unichoice.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.unichoice.Adapter.collegelist;
import com.example.unichoice.DataClass;
import com.example.unichoice.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRecyclerView();
        loadImageSlider();
    }

    private void loadImageSlider() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference banner1 = storage.getReference().child("images/banner2.png");
        StorageReference banner2 = storage.getReference().child("images/banner1.png");
        StorageReference banner3 = storage.getReference().child("images/banner3.png");

        List<SlideModel> imageList = new ArrayList<>();
        addImageToSlider(banner1, imageList);
        addImageToSlider(banner2, imageList);
        addImageToSlider(banner3, imageList);
    }

    private void addImageToSlider(StorageReference imageRef, List<SlideModel> imageList) {
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageList.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                binding.imageSlider.setImageList(imageList);
                binding.imageSlider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void doubleClick(int position) {
                        // Not yet implemented
                    }

                    @Override
                    public void onItemSelected(int position) {
                        String itemMessage = "Selected Image " + position;
                        Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeRecyclerView() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("College List");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataClass> dataList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String logoUrl = dataSnapshot.child("Logo").getValue(String.class);
                    dataList.add(new DataClass(name, logoUrl));
                }

                collegelist adapter = new collegelist(getActivity(), dataList);
                binding.collegelistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.collegelistRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
