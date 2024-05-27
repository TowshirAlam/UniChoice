package com.example.unichoice.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unichoice.DataClass;
import com.example.unichoice.databinding.FragmentCourseBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CourseFragment extends Fragment {
    private FragmentCourseBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCourseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String collegeName = DataClass.getCollegeName2();
        if (collegeName != null) {
            loadPdfFromUrl(collegeName);
        } else {
            Toast.makeText(getActivity(), "College name is not available", Toast.LENGTH_LONG).show();
        }
    }

    private void loadPdfFromUrl(String collegeName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("College");
        reference.child(collegeName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(getContext(), "Successfully Read", Toast.LENGTH_SHORT).show();
                        DataSnapshot dataSnapshot = task.getResult();
                        String pdfUrl = String.valueOf(dataSnapshot.child("course").getValue());
                        new RetrievePdfStream().execute(pdfUrl);
                    } else {
                        Toast.makeText(getContext(), "College Doesn't Exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to read data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if (inputStream != null) {
                binding.pdfView.fromStream(inputStream).load();
            } else {
                Toast.makeText(getActivity(), "Failed to load PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
