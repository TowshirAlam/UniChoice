package com.example.unichoice.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.unichoice.Login;
import com.example.unichoice.R;
import com.example.unichoice.ReadWriteUserDetails;
import com.example.unichoice.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    String fullName = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    String doB = readUserDetails.doB;
                    String gender = readUserDetails.gender;
                    String mobile = readUserDetails.mobile;

                    textViewWelcome.setText("Welcome, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    loadProfileImage(firebaseUser);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadProfileImage(FirebaseUser firebaseUser) {
        StorageReference profileRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + firebaseUser.getUid() + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(requireContext()).load(uri).into(imageView);
        }).addOnFailureListener(e -> {
            imageView.setImageResource(R.drawable.ic_profile); // Set a default profile picture if not available
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewWelcome = view.findViewById(R.id.textView_show_welcome);
        textViewFullName = view.findViewById(R.id.textView_show_full_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        textViewDoB = view.findViewById(R.id.textView_show_dob);
        textViewGender = view.findViewById(R.id.textView_show_gender);
        textViewMobile = view.findViewById(R.id.textView_show_mobile);
        progressBar = view.findViewById(R.id.progress_bar);

        Toolbar actionBar = binding.myAppBar;
        ((AppCompatActivity) getActivity()).setSupportActionBar(actionBar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        imageView = view.findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(v -> openImageChooser());

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Something went wrong! User's details are not available at the moment",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imageChooserLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imageChooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    startCrop(imageUri);
                }
            }
    );

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = "SampleCropImage.jpg";
        destinationFileName = destinationFileName + "_" + System.currentTimeMillis();
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(requireContext().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(450, 450);
        uCrop.start(requireContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    imageView.setImageURI(resultUri);
                    imageUri = resultUri;
                    uploadProfilePicture();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(getContext(), "Crop error: " + cropError, Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfilePicture() {
        if (imageUri != null) {
            FirebaseUser user = authProfile.getCurrentUser();
            if (user != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/" + user.getUid() + ".jpg");
                storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getActivity(), "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to Upload Profile Picture", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.common_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_update_profile) {
            openImageChooser();
        } else if (id == R.id.menu_update_email) {
            Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_settings) {
            Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_delete_profile) {
            Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Something went wrong. Profile", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}