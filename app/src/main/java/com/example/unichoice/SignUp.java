package com.example.unichoice;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private EditText fullname, email, password, moblie, DoB;
    private RadioGroup radidoGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;

    private ProgressBar progressBar;
    private Button register;
    private DatePickerDialog picker;
    private static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullname = findViewById(R.id.edittext_fulname);
        email = findViewById(R.id.edittext_email);
        password = findViewById(R.id.edittext_password);
        DoB = findViewById(R.id.dob);
        moblie = findViewById(R.id.edittext_phone);
        progressBar = findViewById(R.id.progress_Bar);

        radidoGroupRegisterGender = findViewById(R.id.radio_gender);
        radidoGroupRegisterGender.clearCheck();

        //Setting up DatePicker on EditText
        DoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                //Date Picker Dialog
                picker = new DatePickerDialog(SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        register = findViewById(R.id.signupbtn);
        TextView alreadyhavebtn = findViewById(R.id.alreadyhavebtn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Obtaining the entered data
                String textFullName = fullname.getText().toString();
                String textEmail = email.getText().toString();
                String textDoB = DoB.getText().toString();
                String textMobile = moblie.getText().toString();
                String textPwd = password.getText().toString();
                String textGender;  //Can't obtain the value before verifying if ant button was selected or not.

                //Validate moblie no. using Matcher and Pattern (Regular Expression);
                String moblieRegex = "[6-9][0-9]{9}";  //First no. can be {6,7,8,9} and rest 9 numbers can be any.
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(moblieRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

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
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(SignUp.this, "Please Enter your Date of Birth", Toast.LENGTH_SHORT).show();
                    DoB.setError("Date of Birth is required");
                    DoB.requestFocus();
                } else if (radidoGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignUp.this, "Please Select your Gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is Required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(SignUp.this, "Please Enter your Moblie no.", Toast.LENGTH_SHORT).show();
                    moblie.setError("Mobile no. is required");
                    moblie.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(SignUp.this, "Please Enter your Moblie no.", Toast.LENGTH_SHORT).show();
                    moblie.setError("Valid Mobile no. is required");
                    moblie.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(SignUp.this, "Please Enter your Moblie no.", Toast.LENGTH_SHORT).show();
                    moblie.setError("Mobile no. is not Valid");
                    moblie.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(SignUp.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password is required");
                    password.requestFocus();
                } else if (textPwd.length() <= 6) {
                    Toast.makeText(SignUp.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password too weak");
                    password.requestFocus();
                } else {
//                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    textGender="Male";
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
                }
            }
        });

        alreadyhavebtn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });
    }

    //Register user using Credentials
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
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
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile);

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