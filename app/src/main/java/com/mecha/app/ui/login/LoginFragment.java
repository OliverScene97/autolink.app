package com.mecha.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mecha.app.LoginActivity;
import com.mecha.app.R;

public class LoginFragment extends Fragment {

    private EditText phoneEditText, codeEditText;
    private Button sendCodeButton, verifyCodeButton;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        phoneEditText = view.findViewById(R.id.phoneEditText);
        codeEditText = view.findViewById(R.id.codeEditText);
        sendCodeButton = view.findViewById(R.id.sendCodeButton);
        verifyCodeButton = view.findViewById(R.id.verifyCodeButton);
        progressBar = view.findViewById(R.id.progressBar);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getActivity(), "Введите номер телефона", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginViewModel.sendVerificationCode(phoneNumber, getActivity());
            }
        });

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getActivity(), "Введите код", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginViewModel.verifyCode(code);
            }
        });

        loginViewModel.getVerificationState().observe(getViewLifecycleOwner(), state -> {
            if (state == LoginViewModel.VerificationState.SUCCESS) {
                ((LoginActivity) getActivity()).onLoginSuccess();
            } else if (state == LoginViewModel.VerificationState.FAILED) {
                Toast.makeText(getActivity(), "Ошибка верификации", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(state == LoginViewModel.VerificationState.PROCESSING ? View.VISIBLE : View.GONE);
        });

        return view;
    }
}
