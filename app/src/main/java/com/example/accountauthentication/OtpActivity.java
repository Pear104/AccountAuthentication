package com.example.accountauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mVerificationId;
    EditText phoneNumberField;
    EditText otpField;
    Button sendOtpButton;
    Button verifyButton;
    Button resendButton;
    View verifyLayout;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();

        phoneNumberField = findViewById(R.id.phoneNumberField);
        otpField = findViewById(R.id.otpField);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyButton = findViewById(R.id.verifyButton);
        resendButton = findViewById(R.id.resendButton);
        progressBar = findViewById(R.id.progressBar);
        verifyLayout = findViewById(R.id.verifyLayout);

        sendOtpButton.setOnClickListener(v -> {
            sendOtpCode();
        });


        verifyButton.setOnClickListener(v -> {
            String otp = otpField.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            signInWithPhoneAuthCredential(credential);
        });

        resendButton.setOnClickListener(v -> {
            sendOtpButton.performClick();
        });

    }

private void sendOtpCode() {
    String phoneNumber = phoneNumberField.getText().toString();
    progressBar.setVisibility(View.VISIBLE);
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // Ẩn spinner khi hoàn tất
                    progressBar.setVisibility(View.GONE);
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    // Ẩn spinner khi thất bại
                    progressBar.setVisibility(View.GONE);
                    sendOtpButton.setEnabled(true);
                    Toast.makeText(OtpActivity.this, "Xác thực thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    mVerificationId = verificationId;
                    progressBar.setVisibility(View.GONE);
                    phoneNumberField.setVisibility(View.GONE);
                    otpField.setVisibility(View.VISIBLE);
                    sendOtpButton.setVisibility(View.GONE);
                    verifyLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(OtpActivity.this, "Mã OTP đã gửi!", Toast.LENGTH_SHORT).show();
                    // Thêm logic để nhập OTP nếu cần
                }
            });
}

private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    // Hiển thị spinner khi xác thực
    progressBar.setVisibility(View.VISIBLE);

    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                // Ẩn spinner khi nhận phản hồi
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(OtpActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OtpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "Xác thực thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
}
}