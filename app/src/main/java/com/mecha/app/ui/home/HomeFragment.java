package com.mecha.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mecha.app.MainViewModel;
import com.mecha.app.R;

public class HomeFragment extends Fragment {

    private MainViewModel mainViewModel;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textView = view.findViewById(R.id.textView);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getSomeData().observe(getViewLifecycleOwner(), data -> {
            textView.setText(data);
        });

        return view;
    }
}