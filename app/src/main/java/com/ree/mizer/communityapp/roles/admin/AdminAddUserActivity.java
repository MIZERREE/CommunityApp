package com.ree.mizer.communityapp.roles.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ree.mizer.communityapp.ConfirmationActivity;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.login.RegisterActivity;
import com.ree.mizer.communityapp.pojos.History;
import com.ree.mizer.communityapp.pojos.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminAddUserActivity extends AppCompatActivity {
    //firebase
    private FirebaseAuth mAuth;

    //my views
    private TextInputLayout edFullNames, edSurname, edAddress, edPhoneNumber, edEmail, edVillage, edPassword, edConfPassword;
    private Button btnRegister;
    private TextView tvDOReg;
    private DatePicker dtPcDOD;
    Spinner spn_Group, spnRole;

    String TAG = "Community App: Add user";
    String userID, fullNames, surname, address, phoneNumber, dateOfBirth, dateOfRegistration, email, village, group, password,role, status;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);
        //instantiate firebase
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        edFullNames = findViewById(R.id.add_text_name);
        edSurname = findViewById(R.id.add_text_surname);
        edAddress = findViewById(R.id.add_text_address);
        edPhoneNumber = findViewById(R.id.add_text_phone);
        edEmail = findViewById(R.id.add_text_email);
        edVillage = findViewById(R.id.add_text_village);
        edPassword = findViewById(R.id.add_text_password);
        edConfPassword = findViewById(R.id.add_text_conf_password);
        btnRegister = findViewById(R.id.add_button);
        tvDOReg = findViewById(R.id.add_date_of_reg);
        dtPcDOD = findViewById(R.id.add_date_of_birth);
        spn_Group = findViewById(R.id.add_sp_group);
        spnRole = findViewById(R.id.add_sp_role);

        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvDOReg.setText(currentDate);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> groupAdapter =  ArrayAdapter.createFromResource(this,R.array.community_groups,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> roleAdapter =  ArrayAdapter.createFromResource(this,R.array.community_roles,android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_Group.setAdapter(groupAdapter);
        spnRole.setAdapter(roleAdapter);

        spn_Group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = spn_Group.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                group = spn_Group.getItemAtPosition(0).toString();
            }
        });

        spnRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                role = spnRole.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                role = "Household";
            }
        });

        btnRegister.setOnClickListener(v -> {
            if (validateFullName() && validateSurname() && validateAddress() && validatePhoneNumber() && validateEmail() && validateVillage() && validatePassword() && validateConfPassword()) {
                dateOfBirth = validateDOB();
                dateOfRegistration = validateDOReg();
                status = "Active";
                // Write a message to the database
                signUp(edEmail.getEditText().getText().toString().trim(),edPassword.getEditText().getText().toString().trim());

            } else {
                return;
            }
        });
    }

    //creating user with email and password
    private void signUp(String email, String password){
        loadingBar.setTitle("User Registration");
        loadingBar.setMessage("Please wait while we register NEW USER");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Log.d(TAG,"createUserWithEmailAndPassword:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            storeNewUserData();
                        }else{
                            Log.d(TAG,"createUserWithEmailAndPassword:failure", task.getException());
                            myAlert(task.getException().toString());
                            Toast.makeText(AdminAddUserActivity.this, "Error, something went wrong: "+task.getException(), Toast.LENGTH_SHORT).show();
                            edAddress.getEditText().setText(task.getException().toString());
                        }
                        loadingBar.dismiss();
                    }
                }).addOnFailureListener(v -> {myAlert("User registration failed try again later");});

    }

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
                    myAlert("User account created and activated");
                    this.finish();
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

        if (val.isEmpty()) {
            edFullNames.setError("Field can not be empty");
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

    public boolean validateEmail() {
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
            edVillage.setError("Village must not contain any special characters");
            return false;
        } else {
            edVillage.setError(null);
            edVillage.setErrorEnabled(false);
            village = edVillage.getEditText().getText().toString().trim();
            return true;
        }

    }
}