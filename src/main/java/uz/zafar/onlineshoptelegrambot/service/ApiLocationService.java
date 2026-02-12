package uz.zafar.onlineshoptelegrambot.service;

import com.google.gson.JsonObject;
import uz.zafar.onlineshoptelegrambot.dto.gson.LocationStatus;

public interface ApiLocationService {
    String getAddress(Double latitude, Double longitude);
    LocationStatus getAddressWithStatus(Double latitude, Double longitude);
    boolean isLocationInUzbekistan(Double latitude, Double longitude);
    JsonObject getDetailedLocationInfo(Double latitude, Double longitude);
}