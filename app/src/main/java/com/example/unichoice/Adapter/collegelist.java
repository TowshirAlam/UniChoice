package com.example.unichoice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unichoice.DataClass;
import com.example.unichoice.DetailActivity;
import com.example.unichoice.R;
import com.example.unichoice.databinding.CollegelistItemBinding;

import java.util.ArrayList;
import java.util.List;

public class collegelist extends RecyclerView.Adapter<collegelist.collegelistViewHolder> {
    private List<DataClass> datalist;
    private Context context;

    public collegelist(Context context, List<DataClass> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public collegelistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CollegelistItemBinding binding = CollegelistItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new collegelistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull collegelistViewHolder holder, int position) {
        DataClass data = datalist.get(position);
        holder.bind(data);
        holder.cardView.setOnClickListener(view -> {
            DataClass.setCollegeName2(data.getCollegeName());
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("collegeName", data.getCollegeName());
            intent.putExtra("collegeImage", data.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    public void updateList(List<DataClass> newList) {
        datalist = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class collegelistViewHolder extends RecyclerView.ViewHolder {
        private CollegelistItemBinding binding;
        private TextView collegeName;
        private ImageView imageView;
        private CardView cardView;

        public collegelistViewHolder(CollegelistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            collegeName = binding.collegename;
            imageView = binding.collegeimage;
            cardView = binding.collegeView;
        }

        public void bind(DataClass data) {
            collegeName.setText(data.getCollegeName());
            Glide.with(imageView.getContext())
                    .load(data.getImageUrl())
                    .placeholder(R.drawable.placeholder) // Placeholder image
                    .into(imageView);
        }
    }
}
