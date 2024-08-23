package com.mecha.app.ui.help;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.mecha.app.MainActivity;
import com.mecha.app.R;

public class HelpFragment extends Fragment {

    private EditText priceEditText;
    private EditText carManufacturerEditText;
    private EditText carModelEditText;
    private EditText carYearEditText;
    private EditText vinNumberEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        priceEditText = view.findViewById(R.id.priceEditText);
        carManufacturerEditText = view.findViewById(R.id.carManufacturerEditText);
        carModelEditText = view.findViewById(R.id.carModelEditText);
        carYearEditText = view.findViewById(R.id.carYearEditText);
        vinNumberEditText = view.findViewById(R.id.vinNumberEditText);

        // Показываем переключатель и надписи на главной активности
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            @SuppressLint("UseSwitchCompatOrMaterialCode") SwitchCompat helpSwitch = mainActivity.findViewById(R.id.switch_help);
            TextView textHaltura = mainActivity.findViewById(R.id.text_haltura);
            TextView textAvtoservis = mainActivity.findViewById(R.id.text_avtoservis);
            if (helpSwitch != null) {
                helpSwitch.setVisibility(View.VISIBLE);
                textHaltura.setVisibility(View.VISIBLE);
                textAvtoservis.setVisibility(View.VISIBLE);
                helpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Автосервис
                        priceEditText.setEnabled(false);
                        priceEditText.setHint("У автосервисов свои цены на ремонт");
                    } else {
                        // Халтура
                        priceEditText.setEnabled(true);
                        priceEditText.setHint("Предложите вашу цену");
                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Скрыть переключатель и надписи, когда фрагмент уничтожается
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            @SuppressLint("UseSwitchCompatOrMaterialCode") SwitchCompat helpSwitch = mainActivity.findViewById(R.id.switch_help);
            TextView textHaltura = mainActivity.findViewById(R.id.text_haltura);
            TextView textAvtoservis = mainActivity.findViewById(R.id.text_avtoservis);
            if (helpSwitch != null) {
                helpSwitch.setVisibility(View.GONE);
                textHaltura.setVisibility(View.GONE);
                textAvtoservis.setVisibility(View.GONE);
            }
        }
    }
}
