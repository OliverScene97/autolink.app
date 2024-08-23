package com.mecha.app.ui.profile;

public class UserProfile {

    public String avatarUrl;
    public String carManufacturer;
    public String carModel;
    public String bodyType;
    public String carYear;
    public String vinNumber;
    public String nickName;

    // Пустой конструктор необходим для Firebase
    public UserProfile() {
    }

    public UserProfile(String avatarUrl, String carManufacturer, String carModel, String bodyType, String carYear, String vinNumber, String nickName) {
        this.avatarUrl = avatarUrl;
        this.carManufacturer = carManufacturer;
        this.carModel = carModel;
        this.bodyType = bodyType;
        this.carYear = carYear;
        this.vinNumber = vinNumber;
        this.nickName = nickName;
    }

    // Геттеры и сеттеры
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCarManufacturer() {
        return carManufacturer;
    }

    public void setCarManufacturer(String carManufacturer) {
        this.carManufacturer = carManufacturer;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
