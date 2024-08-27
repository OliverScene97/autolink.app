package com.mecha.app.ui.profile;

public class CarCard {
    private String carImageUrl;
    private String carName;
    private String carModel;
    private String carYear;
    private String vinNumber;
    private String engine;
    private String transmission;

    public CarCard(String carImageUrl, String carName, String carModel, String carYear, String vinNumber, String engine, String transmission) {
        this.carImageUrl = carImageUrl;
        this.carName = carName;
        this.carModel = carModel;
        this.carYear = carYear;
        this.vinNumber = vinNumber;
        this.engine = engine;
        this.transmission = transmission;
    }

    public String getCarImageUrl() {
        return carImageUrl;
    }

    public String getCarName() {
        return carName;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarYear() {
        return carYear;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public String getEngine() {
        return engine;
    }

    public String getTransmission() {
        return transmission;
    }
}
