package com.example.android.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private TextView verifyNumberTv,waitMessageTv;
    private RelativeLayout layout1,layout2;
    private EditText phoneNumberEt,enterCode;
    private Button next,verifyCode,resend;
    private String phoneNumber;
    private CountryCodePicker cpp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cpp=(CountryCodePicker) findViewById(R.id.countryCodePicker);
        layout1=(RelativeLayout) findViewById(R.id.layout1);
        layout2=(RelativeLayout) findViewById(R.id.layout2);
        phoneNumberEt=(EditText) findViewById(R.id.phonenumber);
        verifyNumberTv=(TextView) findViewById(R.id.verifyNumber);
        waitMessageTv=(TextView) findViewById(R.id.waitMesseage_TV);
        enterCode=(EditText) findViewById(R.id.enterCode);
        next= (Button) findViewById(R.id.verify_next);
        resend=(Button) findViewById(R.id.resend);
        mAuth= FirebaseAuth.getInstance();
        verifyCode=(Button) findViewById(R.id.verify_code);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              cpp.registerCarrierNumberEditText(phoneNumberEt);
              layout1.setVisibility(View.INVISIBLE);

              phoneNumber="+" + cpp.getFullNumber();
              verifyNumberTv.append(" "+phoneNumber);
              waitMessageTv.append(phoneNumber);
              Log.i("phonenumber",phoneNumber);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        LoginActivity.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks

                layout2.setVisibility(View.VISIBLE);
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i("onVerificationCompleted","Automatically code detected");
                 signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i("error",Log.getStackTraceString(e));
            }
            public void onCodeSent(String verificationId,
                                  PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                Log.i("onCodeSent","Code has been sent");
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }

        };


        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = enterCode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        LoginActivity.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks

                layout2.setVisibility(View.VISIBLE);
            }

        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("tasksuccesfull","loged in sucessfully");
                            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(getApplicationContext(),"signInWithCredential:failure",Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



}
