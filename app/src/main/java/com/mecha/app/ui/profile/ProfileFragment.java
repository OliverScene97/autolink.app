package com.mecha.app.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.mecha.app.LoginActivity;
import com.mecha.app.R;
import com.squareup.picasso.Picasso;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class ProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private EditText nickNameEditText;
    private Button saveProfileButton;
    private Button logoutButton;
    private ImageButton addCarCardButton, editCarCardButton, deleteCarCardButton;
    private ViewGroup carCardContainer;
    private ProfileViewModel profileViewModel;
    private Uri avatarUri;

    // Новый способ для обработки результатов активности
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    avatarUri = result.getData().getData();
                    Picasso.get().load(avatarUri).into(avatarImageView);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация элементов интерфейса
        avatarImageView = view.findViewById(R.id.avatarImageView);
        nickNameEditText = view.findViewById(R.id.nickNameEditText);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        carCardContainer = view.findViewById(R.id.carCardContainer);

        // Инициализация кнопок управления карточками
        addCarCardButton = view.findViewById(R.id.addCarCardButton);
        editCarCardButton = view.findViewById(R.id.editCarCardButton);
        deleteCarCardButton = view.findViewById(R.id.deleteCarCardButton);

        // Инициализация ViewModel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Обработчик для выбора аватара
        avatarImageView.setOnClickListener(v -> openFileChooser());

        // Обработчик для сохранения профиля
        saveProfileButton.setOnClickListener(v -> saveProfile());

        // Обработчик для выхода из профиля
        logoutButton.setOnClickListener(v -> logout());

        // Обработчики для кнопок карточек
        addCarCardButton.setOnClickListener(v -> addNewCarCard());
        editCarCardButton.setOnClickListener(v -> editCarCard());
        deleteCarCardButton.setOnClickListener(v -> deleteCarCard());

        // Проверка существования профиля
        profileViewModel.checkProfileExists();

        // Обновление UI профиля при изменении данных в ViewModel
        profileViewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                nickNameEditText.setText(profile.getNickName());
                // Загрузка аватара с использованием Picasso
                Picasso.get().load(profile.getAvatarUrl()).into(avatarImageView);
                // Здесь можно также загрузить карточки автомобиля и отобразить их в carCardContainer
            } else {
                // Если профиль не найден, показываем сообщение
                Toast.makeText(getActivity(), "Профиль не найден. Пожалуйста, создайте новый.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void editCarCard() {
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);  // Используем новый способ запуска активности
    }

    private void saveProfile() {
        String nickName = nickNameEditText.getText().toString().trim();

        if (nickName.isEmpty() || avatarUri == null) {
            Toast.makeText(getActivity(), "Пожалуйста, заполните все поля и выберите аватар", Toast.LENGTH_SHORT).show();
            return;
        }

        profileViewModel.saveProfile(avatarUri, nickName);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (getActivity() != null) {
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void addNewCarCard() {
        // Раздуваем разметку карточки автомобиля
        View carCardView = getLayoutInflater().inflate(R.layout.car_card, carCardContainer, false);

        // Устанавливаем данные для новой карточки
        ImageView carImageView = carCardView.findViewById(R.id.car_image);
        TextView carNameTextView = carCardView.findViewById(R.id.car_name);
        TextView carModelTextView = carCardView.findViewById(R.id.car_model);

        // Здесь можно установить реальные данные, например, через ввод пользователя или по умолчанию
        carNameTextView.setText("Название авто");
        carModelTextView.setText("Модель авто");

        // Обработчик нажатия для переворачивания карточки
        carCardView.setOnClickListener(v -> flipCarCard(carCardView));

        // Добавляем новую карточку в контейнер
        carCardContainer.addView(carCardView);

        // Обновляем UI (если необходимо)
        carCardContainer.invalidate();
    }

    private void flipCarCard(View carCardView) {
        final View frontSide = carCardView.findViewById(R.id.card_front);
        final View backSide = carCardView.findViewById(R.id.card_back);

        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_right_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_left_in);

        flipOut.setTarget(carCardView);
        flipIn.setTarget(carCardView);

        flipOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (frontSide.getVisibility() == View.VISIBLE) {
                    frontSide.setVisibility(View.GONE);
                    backSide.setVisibility(View.VISIBLE);
                } else {
                    frontSide.setVisibility(View.VISIBLE);
                    backSide.setVisibility(View.GONE);
                }
                flipIn.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        flipOut.start();
    }


    private void deleteCarCard() {
        // Удаляем последнюю карточку, если она есть
        if (carCardContainer.getChildCount() > 0) {
            carCardContainer.removeViewAt(carCardContainer.getChildCount() - 1);
            Toast.makeText(getContext(), "Карточка удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Нет карточек для удаления", Toast.LENGTH_SHORT).show();
        }
    }
}
