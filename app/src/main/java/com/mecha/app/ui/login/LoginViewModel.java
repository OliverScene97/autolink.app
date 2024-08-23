package com.mecha.app.ui.login;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<VerificationState> verificationState = new MutableLiveData<>();
    private String verificationId;
    private static final String TAG = "LoginViewModel";

    public enum VerificationState {
        INITIAL, PROCESSING, SUCCESS, FAILED
    }

    public LiveData<VerificationState> getVerificationState() {
        return verificationState;
    }

    public void sendVerificationCode(String phoneNumber, Activity activity) {
        verificationState.setValue(VerificationState.PROCESSING);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    String code = credential.getSmsCode();
                    if (code != null) {
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e(TAG, "Verification failed", e);
                    verificationState.setValue(VerificationState.FAILED);
                }

                @Override
                public void onCodeSent(@NonNull String s,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    verificationId = s;
                    verificationState.setValue(VerificationState.INITIAL);
                }
            };

    public void verifyCode(String code) {
        verificationState.setValue(VerificationState.PROCESSING);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                verificationState.setValue(VerificationState.SUCCESS);
            } else {
                Log.e(TAG, "Verification failed", task.getException());
                verificationState.setValue(VerificationState.FAILED);
            }
        });
    }
}
