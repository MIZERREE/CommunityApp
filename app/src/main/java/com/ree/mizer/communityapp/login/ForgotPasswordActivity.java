package com.ree.mizer.communityapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.ree.mizer.communityapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button nextBtn;
    private TextInputLayout emailTextLayout;
    private TextView tvMsg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        nextBtn = findViewById(R.id.forget_password_next_button);
        emailTextLayout = findViewById(R.id.forget_pass_edit_text);
        tvMsg = findViewById(R.id.reset_pass_msg);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextLayout.getEditText().getText().toString().trim();

                if (!validateEmail()) {
                    return;
                }

                resetUserPassword(email);

            }
        });
    }

    public void resetUserPassword(String email) {
        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage("verifying..");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Reset password instructions has sent to your email",
                                    Toast.LENGTH_SHORT).show();
                            tvMsg.setTextColor(Color.GREEN);
                            tvMsg.setText("Reset password instructions has sent to your email");
                            emailTextLayout.getEditText().setText("");
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Email don't exist", Toast.LENGTH_SHORT).show();
                            tvMsg.setTextColor(Color.RED);
                            tvMsg.setText("Email don't exist");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }

    public boolean validateEmail() {
        String val = emailTextLayout.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            emailTextLayout.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            emailTextLayout.setError("Invalid Email");
            return false;
        } else {
            emailTextLayout.setError(null);
            emailTextLayout.setErrorEnabled(false);
            return true;
        }

    }

    //checking if email already registered
    boolean userExists;
    public boolean isUser(){
        mAuth.fetchSignInMethodsForEmail(emailTextLayout.getEditText().getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.getResult().getSignInMethods().isEmpty()){
                            userExists = false;
                        }else{
                            userExists = true;
                        }
                    }
                });
        return userExists;
    }

    public void callBackFromForgetPass(View view) {
        finish();
    }
}