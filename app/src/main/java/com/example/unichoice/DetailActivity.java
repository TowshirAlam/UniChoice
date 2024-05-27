package com.example.unichoice;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.unichoice.Adapter.tabAdapter;
import com.example.unichoice.Fragment.ContactFragment;
import com.example.unichoice.Fragment.CourseFragment;
import com.example.unichoice.Fragment.InfoFragment;
import com.example.unichoice.databinding.ActivityDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private DatabaseReference reference;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageView;
    private TextView dBrochure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String collegeName = DataClass.getCollegeName2();

        // Fetch and display college details
        fetchCollegeDetails(collegeName);

        // Fetch and display college logo
        fetchCollegeLogo(collegeName);

        // Setup ViewPager and TabLayout
        setupViewPagerAndTabs();

        // Download brochure
         dBrochure=findViewById(R.id.btnDownloadBrochure);
                dBrochure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadBrochure(collegeName);
                    }
                });
    }

    private void fetchCollegeDetails(String collegeName) {
        reference = FirebaseDatabase.getInstance().getReference("College");
        reference.child(collegeName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String name = String.valueOf(dataSnapshot.child("collegename").getValue());
                        String rank = String.valueOf(dataSnapshot.child("rank").getValue());

                        binding.CollegeName.setText(name);
                        binding.tvCollegeRank.setText(rank);
                    } else {
                        Toast.makeText(DetailActivity.this, "College doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCollegeLogo(String collegeName) {
        imageView = findViewById(R.id.CollegeLogo);
        DatabaseReference logoReference = FirebaseDatabase.getInstance().getReference("College").child(collegeName).child("Logo");
        logoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String logoUrl = snapshot.getValue(String.class);
                if (logoUrl != null && !logoUrl.isEmpty()) {
                    Glide.with(DetailActivity.this).load(logoUrl).into(imageView);
                } else {
                    Toast.makeText(DetailActivity.this, "Logo not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to load logo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPagerAndTabs() {
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.view_pager);

        tabAdapter tabadapter = new tabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabadapter.addFragment(new InfoFragment(), "Info");
        tabadapter.addFragment(new CourseFragment(), "Course & Fees");
        tabadapter.addFragment(new ContactFragment(), "Contact");

        viewPager.setAdapter(tabadapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void downloadBrochure(String collegeName) {
        DatabaseReference brochureReference = FirebaseDatabase.getInstance().getReference("College").child(collegeName).child("Brochure");
        brochureReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String brochureUrl = snapshot.getValue(String.class);
                if (brochureUrl != null && !brochureUrl.isEmpty()) {
                    downloadFile(brochureUrl, "brochure_" + collegeName, ".pdf");
                } else {
                    Toast.makeText(DetailActivity.this, "Brochure not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to fetch brochure URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadFile(String url, String fileName, String fileExtension) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtension);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Downloading brochure...", Toast.LENGTH_LONG).show();
        }
    }
}
