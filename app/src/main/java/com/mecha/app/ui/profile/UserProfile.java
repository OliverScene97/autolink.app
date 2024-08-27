package com.mecha.app.ui.profile;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private String avatarUrl;
    private String nickName;
    private List<CarCard> carCards;

    // Пустой конструктор необходим для Firebase
    public UserProfile() {
        carCards = new ArrayList<>();
    }

    public UserProfile(String avatarUrl, String nickName, List<CarCard> carCards) {
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
        this.carCards = carCards != null ? carCards : new ArrayList<>();
    }

    // Геттеры и сеттеры
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<CarCard> getCarCards() {
        return carCards;
    }

    public void setCarCards(List<CarCard> carCards) {
        this.carCards = carCards;
    }

    // Добавление карточки
    public void addCarCard(CarCard carCard) {
        if (carCards == null) {
            carCards = new ArrayList<>();
        }
        carCards.add(carCard);
    }

    // Удаление карточки
    public void removeCarCard(CarCard carCard) {
        if (carCards != null) {
            carCards.remove(carCard);
        }
    }
}
