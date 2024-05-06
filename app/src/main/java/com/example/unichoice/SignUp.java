package com.example.unichoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private EditText fullname, email, password, moblie;

    private ProgressBar progressBar;
    private Button register;
    private TextView alreadyhavebtn;
    private static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullname = findViewById(R.id.edittext_fulname);
        email = findViewById(R.id.edittext_email);
        password = findViewById(R.id.edittext_password);
        progressBar = findViewById(R.id.progress_Bar);

        register = findViewById(R.id.signupbtn);
        alreadyhavebtn = findViewById(R.id.alreadyhavebtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Obtaining the entered data
                String textFullName = fullname.getText().toString();
                String textEmail = email.getText().toString();
                String textPwd = password.getText().toString();

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(SignUp.this, "Please Enter your Full Name", Toast.LENGTH_SHORT).show();
                    fullname.setError("Full Name is required");
                    fullname.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(SignUp.this, "Please Enter your email", Toast.LENGTH_SHORT).show();
                    email.setError("Email is required");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignUp.this, "Please Enter your email", Toast.LENGTH_SHORT).show();
                    email.setError("Valid Email is required");
                    email.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(SignUp.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password is required");
                    password.requestFocus();
                } else if (textPwd.length() <= 6) {
                    Toast.makeText(SignUp.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password too weak");
                    password.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textPwd);
                }
            }
        });

        alreadyhavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });
    }

    //Register user using Credentials
    private void registerUser(String textFullName, String textEmail, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Update Display Name of User
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Fetching the user data to FireBase Realtime DataBase
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName,textEmail);

                    // Extracting User reference from Database for "Registered User"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                //Send Verification Email
                                firebaseUser.sendEmailVerification();

                                //Open User profile after successful registration
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                startActivity(intent);
                                // To Prevent User from returning Back to Register Activity on pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
                                finish(); //To close register Activity
                            } else {
                                Toast.makeText(SignUp.this, "Not Registered", Toast.LENGTH_LONG).show();
                            }
                            //Hide Progress Bar weather User is creation is successful or failed
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        password.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and symbols");
                        password.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        email.setError("Your email is invalid or already in use. Kindly re-enter.");
                        email.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        email.setError("User is already registered with this email. Use another email.");
                        email.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //Hide Progress Bar weather User is creation is successful or failed
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}