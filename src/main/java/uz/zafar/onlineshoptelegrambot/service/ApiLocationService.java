package uz.zafar.onlineshoptelegrambot.service;

import com.google.gson.JsonObject;
import uz.zafar.onlineshoptelegrambot.dto.gson.LocationStatus;

public interface ApiLocationService {

    /**
     * Koordinatalar orqali to'liq manzilni olish
     */
    String getAddress(Double latitude, Double longitude);

    /**
     * Koordinatalar orqali manzilni status bilan olish
     */
    LocationStatus getAddressWithStatus(Double latitude, Double longitude);

    /**
     * Joylashuv O'zbekistonda ekanligini tekshirish
     */
    boolean isLocationInUzbekistan(Double latitude, Double longitude);

    /**
     * Batafsil joylashuv ma'lumotlarini olish
     */
    JsonObject getDetailedLocationInfo(Double latitude, Double longitude);
}