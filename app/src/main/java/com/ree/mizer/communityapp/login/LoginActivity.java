package com.ree.mizer.communityapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;
import com.ree.mizer.communityapp.roles.household.HouseHoldDashActivity;
import com.ree.mizer.communityapp.roles.tribal.TribalDashActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //my views
    private TextInputLayout edtUsername, edtPassword;
    TextView txtHeritage;
    private Button bntLogin;

    String codeSent;

    //firebase
    private FirebaseAuth mAuth;
    DatabaseReference userReference;

    ArrayList<User> searchedUserList;

    ///User by phone number
    boolean phoneUserFound = false;
    String userFoundNumber;


    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        txtHeritage = findViewById(R.id.tv_heritage);

        edtPassword = findViewById(R.id.edt_password);
        edtUsername = findViewById(R.id.edt_username);
        bntLogin = findViewById(R.id.bt_login);

        if(!isConnected(this)){
            showCustomDialog();
        }

        bntLogin.setOnClickListener(v -> {
            String username = edtUsername.getEditText().getText().toString().trim();
            String password = edtPassword.getEditText().getText().toString().trim();
            if(username.isEmpty()){
                edtUsername.setError("Enter username");
            }else if(password.isEmpty()){
                edtPassword.setError("Enter password");
            }else{
                login(username,password);
            }
        });

        txtHeritage.setMovementMethod(LinkMovementMethod.getInstance());
        txtHeritage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //alert
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Mamaila Heritage website");
                builder.setMessage("Do you want to visit Mamaila Heritage Website?\nhttps://www.mamailanetwork.co.za/bohwa/");
                builder.setIcon(R.drawable.haritage_ico);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse("https://www.mamailanetwork.co.za/bohwa/"));
                        startActivity(browserIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //end of alert dialog
            }
        });
    }

    //signInWithEmailAndPassword
    public void login(String username, String password){
        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();
        mAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        loadingBar.dismiss();
                        //clear text before login
                        edtUsername.getEditText().setText("");
                        edtPassword.getEditText().setText("");

                        //Search user if they are active or not
                        searchUser(username);

                    }else{
                        Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });
    }

    public void signInWithCellphoneNumber(View view){
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.custom_otp);
        dialog.setCancelable(false);

        PinView pinView = dialog.findViewById(R.id.pin_view);
        Button btnVerify = dialog.findViewById(R.id.custom_btn_verify);
        Button btnCancel = dialog.findViewById(R.id.custom_btn_cancel);
        Button btnSendOTP = dialog.findViewById(R.id.custom_btn_send_otp);
        TextInputLayout txtNumber = dialog.findViewById(R.id.login_text_phone_number);
        TextView tvResetOPT = dialog.findViewById(R.id.custom_tv_resend);
        TextView tvTitle = dialog.findViewById(R.id.custom_tv_title2);
        dialog.show();

        //pinView.setText(verificationID);

        btnSendOTP.setOnClickListener(v->{
            String phoneNumber = txtNumber.getEditText().getText().toString().trim();
            if(!phoneNumber.isEmpty()) {
                //if (searchUserByNumber(phoneNumber)) {
                    sendVerificationCode("+27"+phoneNumber);
                    Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "signInWithCellphoneNumber: ****** sendOPT CLICKED");

                    //Make views visible
                    tvTitle.setVisibility(View.VISIBLE);
                    pinView.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.VISIBLE);
                //}

            }else{
                Toast.makeText(this, "Enter Phone number first", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerify.setOnClickListener(v->{
            String verificationCode = pinView.getText().toString();
            if(verificationCode.isEmpty()){
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
            }else{
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        });

        btnCancel.setOnClickListener(v->{
            dialog.dismiss();
        });
    }

    public void displayCellPhoneUnderConstruction(View view){
        Snackbar.make(view, "Coming soon", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void sendVerificationCode(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginUserByNumber(userFoundNumber);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void searchUser(String name) {

        Query query = userReference.orderByChild("email").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    searchedUserList = new ArrayList<>();
                    FirebaseMessaging.getInstance().subscribeToTopic("all");
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        searchedUserList.add(user);
                        if(searchedUserList.get(0).getStatus() == null|| searchedUserList.get(0).getStatus().equalsIgnoreCase("Not active")){
                            displayDialog();

                        }else if(searchedUserList.get(0).getStatus().equalsIgnoreCase("active")){

                            if(searchedUserList.get(0).getRole().equals("Admin")){
                                startActivity(new Intent(LoginActivity.this, AdminDashActivity.class));
                            }else if(searchedUserList.get(0).getRole().equals("Tribal") || searchedUserList.get(0).getRole().equals("Tribal Authority")){
                                startActivity(new Intent(LoginActivity.this, TribalDashActivity.class));
                            }else if(searchedUserList.get(0).getRole().equals("Household")){
                                startActivity(new Intent(LoginActivity.this, HouseHoldDashActivity.class));
                            }
                        }
                    }

                }else{
                    Log.d("user","No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean searchUserByNumber(String phone) {

        Query query = userReference.orderByChild("phoneNumber").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    phoneUserFound=true;
                    User user = snapshot.getValue(User.class);
                    userFoundNumber = user.getPhoneNumber();
                    Toast.makeText(LoginActivity.this, "User found: "+user.getSurname(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return phoneUserFound;
    }

    private void loginUserByNumber(String phone) {

        Query query = userReference.orderByChild("phoneNumber").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    searchedUserList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        searchedUserList.add(user);
                        if(searchedUserList.get(0).getStatus().equalsIgnoreCase("Not active")){
                            displayDialog();

                        }else if(searchedUserList.get(0).getStatus().equalsIgnoreCase("active")){

                            if(searchedUserList.get(0).getRole().equals("Admin")){
                                startActivity(new Intent(LoginActivity.this, AdminDashActivity.class));
                            }else if(searchedUserList.get(0).getRole().equals("Tribal") || searchedUserList.get(0).getRole().equals("Tribal Authority")){

                                startActivity(new Intent(LoginActivity.this, TribalDashActivity.class));
                            }else if(searchedUserList.get(0).getRole().equals("Household")){
                                startActivity(new Intent(LoginActivity.this, HouseHoldDashActivity.class));
                            }
                        }
                    }

                }else{
                    Log.d("user","No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void register(View view){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    private void displayDialog(){
        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Inactive account");
        builder.setMessage("You account is not active, please wait for Admin to verify you before you can login");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog

        alertDialog.show();
    }

    public void mamailaWebsite(View view){

    }

    //Checking if user connected to the internet
    private boolean isConnected(LoginActivity login) {
        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifiConn != null && wifiConn.isConnected() || mobileConn !=null && mobileConn.isConnected()){
            return true;
        }
        else{
            return false;
        }

    }

    //Dialog shows if user is not connected to internet
    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please connect to the internet to proceed")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_ADD_NETWORKS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void forgotPassword(View view){
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

}