package com.ree.mizer.communityapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ree.mizer.communityapp.ConfirmationActivity;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.History;
import com.ree.mizer.communityapp.pojos.User;

public class RegisterActivity extends AppCompatActivity implements OnItemSelectedListener{
    //firebase
    private FirebaseAuth mAuth;

    private static final String TAG = "RegisterActivity";

    //my views
    private TextInputLayout edFullNames, edSurname, edAddress, edPhoneNumber, edEmail, edVillage, edPassword, edConfPassword;
    private Button btnRegister;
    private TextView tvDOReg;
    private DatePicker dtPcDOD;

    String userID, fullNames, surname, address , phoneNumber, dateOfBirth, dateOfRegistration, email = "no email address", village, group, password,role, status;
    String codeBySystem;
    String doesUserHaveEmail = "No";

    Spinner spnLoginMethod;
    String[] loginMethod = {"None","Email","Phone number"};
    String strLoginMethod="none";

    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //instantiate firebase
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        edFullNames = findViewById(R.id.register_text_name);
        edSurname = findViewById(R.id.register_text_surname);
        edAddress = findViewById(R.id.register_text_address);
        edPhoneNumber = findViewById(R.id.register_text_phone);
        edEmail = findViewById(R.id.register_text_email);
        edVillage = findViewById(R.id.register_text_village);
        edPassword = findViewById(R.id.register_text_password);
        edConfPassword = findViewById(R.id.register_text_conf_password);
        btnRegister = findViewById(R.id.register_button);
        tvDOReg = findViewById(R.id.register_date_of_reg);
        dtPcDOD = findViewById(R.id.register_date_of_birth);
        spnLoginMethod = findViewById(R.id.spn_login_method);

        // Spinner click listener
        spnLoginMethod.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loginMethod);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnLoginMethod.setAdapter(dataAdapter);

        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvDOReg.setText(currentDate);

        btnRegister.setOnClickListener(v -> {
            if (validateFullName() && validateSurname() && validateAddress() && validatePhoneNumber() && validateEmail() && validateVillage() && validatePassword() && validateConfPassword()) {
                dateOfBirth = validateDOB();
                dateOfRegistration = validateDOReg();
                group = "No group";
                status = "Not active";
                role = "Household";

                phoneSignUp();

            } else {
                return;
            }
        });


    }

    /*
    * User enters details
    * details are validated
    * the user will choose if they want email account
    * if no email provided then user wont have email account
    * user data is stored after the onComplete() on signUpUser() method
    * Then it will go to next activity after storing data
    * */

    //creating user with email and password
    //this method will be called once user provided and email address
    private void signUp(String email, String password){
        loadingBar.setTitle("User Registration");
        loadingBar.setMessage("Please wait while we register you");
        loadingBar.show();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Log.d(TAG,"createUserWithEmailAndPassword:success");
                            FirebaseUser  user = mAuth.getCurrentUser();
                            loadingBar.dismiss();

                        }else{
                            Log.d(TAG,"createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Error, something went wrong: "+task.getException(), Toast.LENGTH_SHORT).show();
                            edAddress.getEditText().setText(task.getException().toString());
                            loadingBar.dismiss();
                        }
                    }
                }).addOnFailureListener(v -> {myAlert("User registration failed try again later");});

    }

    //creating user with phone number
    //This is a primary sign in method

    private void sendVerificationCodeToUser(String phoneNumber) {
        //loading bar
        loadingBar.setTitle("Authenticating");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    loadingBar.setMessage("OTP sent ...");
                    loadingBar.dismiss();

                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;

                    if(s!=null){
                        openOTPDialog();
                        verifyCode(s);
                    }
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    loadingBar.dismiss();
                    String code = phoneAuthCredential.getSmsCode();
                    Log.d(TAG, "onVerificationCompleted: this is the getSMS code: ***"+code);
                    if(code!=null){
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.getMessage());
                }
            };

    private void verifyCode(String code) {
        loadingBar.setTitle("Verifying OTP");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            //If user has email then we create their email account
                            if(doesUserHaveEmail.equals("Yes")){
                                signUp(edEmail.getEditText().getText().toString().trim(),edPassword.getEditText().getText().toString().trim());
                            }
                            storeNewUserData();
                            // Update UI
                        } else {
                            loadingBar.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void openOTPDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_otp_register, viewGroup, false);
        Button btnVerify = dialogView.findViewById(R.id.custom_btn_verify_reg);
        Button btnCancel = dialogView.findViewById(R.id.custom_btn_cancel_reg);
        PinView pinView = dialogView.findViewById(R.id.pin_view_reg);
        //TextView tvResend = dialogView.findViewById(R.id.custom_tv_resend);

        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pinView.getText().toString();
                if(!code.isEmpty()){
                    verifyCode(code);
                }else{
                    Toast.makeText(RegisterActivity.this, "Enter OPT first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();

    }

    public void phoneSignUp(){
        sendVerificationCodeToUser("+27"+phoneNumber);
    }

    //end of phone number

    //creating new user
    private void storeNewUserData() {

        loadingBar.setTitle("User data");
        loadingBar.setMessage("adding data to database ...");
        loadingBar.show();

        String userID = mAuth.getCurrentUser().getUid();
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference userReference = rootNode.getReference("Users").child(userID);
        DatabaseReference historyRef = rootNode.getReference("History").push();

        User addNewUser = new User(userID, fullNames, surname, address, phoneNumber, dateOfBirth, dateOfRegistration, email, village, group, password,role, status);
        addNewUser.setHasAssets("No");
        addNewUser.setHasFines("No");
        userReference.setValue(addNewUser)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG,"adding user to database:success");
                    loadingBar.dismiss();
                    myAlert("User registered successfully");
                    //Start of firebase messaging****************************************************************
                    //Subscribe for Firebase messaging
                    FirebaseMessaging.getInstance().subscribeToTopic("activation:"+userID);
                    FirebaseMessaging.getInstance().subscribeToTopic("all");
                    //End of firebase messaging****************************************************************

                    startActivity(new Intent(RegisterActivity.this, ConfirmationActivity.class));
                    RegisterActivity.this.finish();
                })
                .addOnFailureListener(this, v ->{
                    loadingBar.dismiss();
                    Log.d(TAG,"adding user to database:failed");
                    myAlert("Something went wrong try again later");
                });

        //storing the history
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        History addHistory = new History(userID,currentDate,currentTime,"Registration");
        historyRef.setValue(addHistory);


    }

    //Alerting user of their registration
    private void myAlert(String message){
        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User registration");
        builder.setMessage(message);
        builder.setIcon(R.drawable.app_luanch);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //end of alert dialog
    }

    //validate views
    public boolean validateFullName() {
        String val = edFullNames.getEditText().getText().toString().trim();
        String checkFullName = "[a-zA-Z]+";

        if (val.isEmpty()) {
            edFullNames.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkFullName)) {
            edFullNames.setError("Full names must not contain any special characters or numbers");
            return false;
        } else {
            edFullNames.setError(null);
            edFullNames.setErrorEnabled(false);
            fullNames = edFullNames.getEditText().getText().toString().trim();
            return true;
        }

    }

    public boolean validateSurname() {
        String val = edSurname.getEditText().getText().toString().trim();
        String checkFullName = "[a-zA-Z]+";

        if (val.isEmpty()) {
            edSurname.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkFullName)) {
            edSurname.setError("Surnames must not contain any special characters or numbers");
            return false;
        } else {
            edSurname.setError(null);
            edSurname.setErrorEnabled(false);
            surname = edSurname.getEditText().getText().toString().trim();
            return true;
        }

    }

    public boolean validateAddress() {
        String val = edAddress.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            edAddress.setError("Field can not be empty");
            return false;
        } else {
            edAddress.setError(null);
            edAddress.setErrorEnabled(false);
            address = val;
            return true;
        }

    }

    public boolean validatePhoneNumber() {
        String val = edPhoneNumber.getEditText().getText().toString().trim();
        String checkPassword = "^" + ".{9,}";

        if (val.isEmpty()) {
            edPhoneNumber.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            edPhoneNumber.setError("Enter valid phone number");
            return false;
        }else {
            edPhoneNumber.setError(null);
            edPhoneNumber.setErrorEnabled(false);
            phoneNumber = edPhoneNumber.getEditText().getText().toString().trim();
            return true;
        }

    }

    public String validateDOB() {
        String val = edPhoneNumber.getEditText().getText().toString().trim();
        String checkPassword = "^" + ".{9,}";

        int day = dtPcDOD.getDayOfMonth();
        int month = dtPcDOD.getMonth();
        int year = dtPcDOD.getYear();

        String date = day+"/"+month+"/"+year;

        return date;

    }

    public String validateDOReg() {
        return  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    /*public boolean validateEmail() {
        String val = edEmail.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
        String checkPassword = "^" +
                "(?=.*[0-9])"+            //at least 1 digit
                "(?=.*[a-z])"+            //at least 1 lower case letter
                "(?=.*[A-Z])"+            //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +           //any letter
                "(?=.*[@#$%^&+=])" +         //at least 1 special character
                "(?=\\S+$)" +                //no white spaces
                "$";

        if (val.isEmpty()) {
            edEmail.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            edEmail.setError("Invalid Email");
            return false;
        } else {
            edEmail.setError(null);
            edEmail.setErrorEnabled(false);
            email = edEmail.getEditText().getText().toString().trim();
            return true;
        }

    }*/

    public boolean validateEmail() {
        String val = edEmail.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";

        if (!val.isEmpty()) {
            if (!val.matches(checkEmail)) {
                edEmail.setError("Invalid Email");
            } else {
                edEmail.setError(null);
                edEmail.setErrorEnabled(false);
                email = edEmail.getEditText().getText().toString().trim();
                doesUserHaveEmail = "Yes";
            }

        } else {
            doesUserHaveEmail = "No";
            email = "no email address";
        }

        return true;
    }

    public boolean validatePassword() {
        String val = edPassword.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])"+            //at least 1 digit
                "(?=.*[a-z])"+            //at least 1 lower case letter
                "(?=.*[A-Z])"+            //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +           //any letter
                "(?=.*[@#$%^&+=])" +         //at least 1 special character
                "(?=\\S+$)" +                //no white spaces
                ".{8,}" +                    //at least 8 characters
                "$";

        if (val.isEmpty()) {
            edPassword.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            edPassword.setError("Alphanumeric password, at least 1 special character and no spacing");
            return false;
        } else {
            edPassword.setError(null);
            edPassword.setErrorEnabled(false);
            return true;
        }

    }

    public boolean validateConfPassword(){
        String val = edConfPassword.getEditText().getText().toString().trim();

        if(val.isEmpty()){
            edConfPassword.setError("Field can not be empty");
            return false;
        }else if(!val.equals(edPassword.getEditText().getText().toString().trim())){
            edConfPassword.setError("Password does not match");
            return false;
        }else{
            edConfPassword.setError(null);
            edConfPassword.setErrorEnabled(false);
            password = edPassword.getEditText().getText().toString().trim();
            return true;
        }
    }

    public boolean validateVillage() {
        String val = edVillage.getEditText().getText().toString().trim();
        String checkFullName = "[a-zA-Z0-9]+";

        if (val.isEmpty()) {
            edVillage.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkFullName)) {
            edVillage.setError("Surnames must not contain any special characters");
            return false;
        } else {
            edVillage.setError(null);
            edVillage.setErrorEnabled(false);
            village = edVillage.getEditText().getText().toString().trim();
            return true;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        strLoginMethod = parent.getItemAtPosition(position).toString();
        edEmail.setEnabled(true);
        edPhoneNumber.setEnabled(true);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}