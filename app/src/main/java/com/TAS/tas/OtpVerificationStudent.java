package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OtpVerificationStudent extends AppCompatActivity {

    EditText phoneNumber,otp1,otp2,otp3,otp4,otp5,otp6;
    TextView getotp,submitotp,resendotp;
    FirebaseAuth fAuth;
    LottieAnimationView lottieAnimationView;
    LinearLayout linearLayout1, linearLayout2;
    TextView textView,textView2;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationid;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        fAuth = FirebaseAuth.getInstance();

        phoneNumber = findViewById(R.id.phnn);
        getotp = findViewById(R.id.getotp);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        textView = findViewById(R.id.txtphn);
        textView2 =findViewById(R.id.txtresent);


        submitotp = findViewById(R.id.otpenter);
        resendotp = findViewById(R.id.otpresend);

        linearLayout1 = findViewById(R.id.layout1);
        linearLayout2 = findViewById(R.id.layout2);

        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.GONE);

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait..");
        pd.setCanceledOnTouchOutside(false);




        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.GONE);
                Toast.makeText(OtpVerificationStudent.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationid = s;
                token = forceResendingToken;
                pd.dismiss();
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                resendotp.setVisibility(View.GONE);
                textView.setText("Sent to the mobile no "+phoneNumber.getText().toString().trim());
                textView2.setText("You can resend your otp after 120 seconds");
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.GONE);
                resendotp.setVisibility(View.VISIBLE);
            }
        };


        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone ="+91"+ phoneNumber.getText().toString();
                if (phoneNumber.getText().toString().isEmpty()){
                    phoneNumber.setError("Enter Mobile no");
                }
                else {
                    verifyphoneno(phone);
                }
            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "+91"+phoneNumber.getText().toString();
                if (phoneNumber.getText().toString().isEmpty()){
                    phoneNumber.setError("Enter Mobile no");
                }
                else {
                    verifyphonenoresendotp(phone, token);
                }
            }
        });

        submitotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otp1.getText().toString() + otp2.getText().toString()+ otp3.getText().toString() + otp4.getText().toString()+otp5.getText().toString()+ otp6.getText().toString();
                if (!otp1.getText().toString().trim().isEmpty() && !otp2.getText().toString().trim().isEmpty() && !otp3.getText().toString().trim().isEmpty()
                        && !otp4.getText().toString().trim().isEmpty() && !otp5.getText().toString().trim().isEmpty() && !otp6.getText().toString().trim().isEmpty() ){
                    pd.setMessage("Verifying code");
                    pd.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid,otp);
                    authenticateUser(credential);
                }
                else {
                    Toast.makeText(OtpVerificationStudent.this,"Please enter the complete code",Toast.LENGTH_SHORT);
                }
            }
        });


        numberOtopMove();
    }

    private void numberOtopMove() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void verifyphonenoresendotp(String phone,PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth).setActivity(this)
                .setPhoneNumber(phone).setTimeout(120L, TimeUnit.SECONDS).setCallbacks(callbacks)
                .setForceResendingToken(token).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyphoneno(String phoneno) {
        pd.setMessage("verifying phone number");
        pd.show();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth).setActivity(this)
                .setPhoneNumber(phoneno).setTimeout(120L, TimeUnit.SECONDS).setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void authenticateUser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String phno = phoneNumber.getText().toString();
                DatabaseReference firebaseDatabase =   FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                firebaseDatabase.child("phno").setValue(phno);
                Intent intent = new Intent(OtpVerificationStudent.this, Student_login.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtpVerificationStudent.this,"Mobile number not Verified",Toast.LENGTH_SHORT);
            }
        });

    }

}