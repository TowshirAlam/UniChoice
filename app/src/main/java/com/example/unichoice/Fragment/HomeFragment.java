package com.example.unichoice.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.unichoice.R;
import com.example.unichoice.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.logo, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.no_profile_pic, ScaleTypes.FIT));

        binding.imageSlider.setImageList(imageList);
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT);
        binding.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void doubleClick(int position) {
                // Not yet implemented
            }

            @Override
            public void onItemSelected(int position) {
                SlideModel itemPosition = imageList.get(position);
                String itemMessage = "Selected Image " + position;
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show();
            }
        });

//        List<String> foodName = List.of("Burger", "Sandwich", "Ice Cream", "Tea");
//        List<String> price = List.of("$5", "$7", "$8", "$10");
//        List<Integer> popularFoodImages = List.of(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4);
//        PopularAdapter adapter = new PopularAdapter(foodName, price, popularFoodImages);
//        binding.popularRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        binding.popularRecyclerView.setAdapter(adapter);
    }
}