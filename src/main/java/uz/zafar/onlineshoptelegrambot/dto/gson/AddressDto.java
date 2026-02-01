package uz.zafar.onlineshoptelegrambot.dto.gson;

public class AddressDto {
    private String fullAddress;
    private String country;
    private String countryCode;
    private String region;
    private String city;
    private City cityEnum;

    public City getCityEnum() {
        return cityEnum;
    }

    public void setCityEnum(City cityEnum) {
        this.cityEnum = cityEnum;
    }

    private String district;
    private String street;
    private String houseNumber;
    private String postalCode;
    private boolean isInUzbekistan;
    private Double latitude;
    private Double longitude;

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isInUzbekistan() {
        return isInUzbekistan;
    }

    public void setInUzbekistan(boolean inUzbekistan) {
        isInUzbekistan = inUzbekistan;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
