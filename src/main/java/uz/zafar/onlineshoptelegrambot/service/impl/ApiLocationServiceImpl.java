package uz.zafar.onlineshoptelegrambot.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.dto.gson.AddressDto;
import uz.zafar.onlineshoptelegrambot.dto.gson.City;
import uz.zafar.onlineshoptelegrambot.dto.gson.LocationStatus;
import uz.zafar.onlineshoptelegrambot.service.ApiLocationService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class ApiLocationServiceImpl implements ApiLocationService {

    private static final Logger log = LoggerFactory.getLogger(ApiLocationServiceImpl.class);

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";
    private static final String USER_AGENT = "OnlineShopTelegramBot/1.0";
    private static final Gson gson = new Gson();

    // O'zbekistonning geografik chegaralari (taxminiy)
    private static final double UZ_MIN_LAT = 37.0;
    private static final double UZ_MAX_LAT = 45.0;
    private static final double UZ_MIN_LON = 56.0;
    private static final double UZ_MAX_LON = 73.0;

    // O'zbekistonni aniqlash uchun mamlakat kodlari va nomlari
    private static final String[] UZBEKISTAN_COUNTRY_CODES = {"UZ", "UZB"};
    private static final String[] UZBEKISTAN_COUNTRY_NAMES = {
            "Uzbekistan", "O'zbekiston", "Узбекистан", "Usbekistan"
    };

    @Override
    public LocationStatus getAddressWithStatus(Double latitude, Double longitude) {
        try {
            // 1. Koordinatalarni tekshirish
            if (!isValidCoordinates(latitude, longitude)) {
                return LocationStatus.error("Noto'g'ri koordinatalar: lat=" + latitude + ", lon=" + longitude);
            }

//            if (!isInUzbekistanBounds(latitude, longitude)) {
//                return LocationStatus.error("Bu joy O'zbekiston hududida emas");
//            }

            // 3. API orqali manzil ma'lumotlarini olish
            JsonObject locationData = fetchLocationData(latitude, longitude);

            if (locationData == null) {
                return LocationStatus.error("Manzil ma'lumotlarini olishda xatolik");
            }

            // 4. Xatolik tekshiruvi
            if (locationData.has("error")) {
                String error = locationData.get("error").getAsString();
                log.error("Nominatim API xatosi: {}", error);
                return LocationStatus.error("API xatosi: " + error);
            }

            AddressDto address = parseAddressData(locationData, latitude, longitude);

            // 6. O'zbekiston ekanligini aniq tekshirish
//            if (!isDefinitelyInUzbekistan(address)) {
//                return LocationStatus.error("Bu joy O'zbekiston hududida emas");
//            }

            return LocationStatus.success(address);

        } catch (Exception e) {
            log.error("Manzil aniqlashda xatolik: {}", e.getMessage(), e);
            return LocationStatus.error("Tizim xatosi: " + e.getMessage());
        }
    }

    @Override
    public String getAddress(Double latitude, Double longitude) {
        LocationStatus status = getAddressWithStatus(latitude, longitude);
        if (status.isSuccess() && status.getAddress().getFullAddress() != null) {
            return status.getAddress().getFullAddress();
        }
        if (!status.isSuccess()) return status.getError();
        return "error";
    }

    @Override
    public boolean isLocationInUzbekistan(Double latitude, Double longitude) {
        try {
            // 1. Asosiy chegaralarni tekshirish
            if (!isInUzbekistanBounds(latitude, longitude)) {
                return false;
            }

            // 2. API orqali aniq tekshirish
            JsonObject locationData = fetchLocationData(latitude, longitude);
            if (locationData == null || locationData.has("error")) {
                return false;
            }

            AddressDto address = parseAddressData(locationData, latitude, longitude);
            return isDefinitelyInUzbekistan(address);

        } catch (Exception e) {
            log.error("Hududni tekshirishda xatolik: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Cacheable(value = "locations", key = "#latitude + '_' + #longitude")
    public JsonObject getDetailedLocationInfo(Double latitude, Double longitude) {
        try {
            String urlString = String.format(Locale.US,
                    "%s?format=jsonv2&lat=%f&lon=%f&addressdetails=1&zoom=18&namedetails=1",
                    NOMINATIM_URL, latitude, longitude
            );

            return makeApiRequest(urlString);

        } catch (Exception e) {
            log.error("Batafsil ma'lumot olishda xatolik: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Koordinatalarni tekshirish
     */
    private boolean isValidCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        return !(latitude < -90 || latitude > 90 ||
                longitude < -180 || longitude > 180);
    }

    /**
     * O'zbekistonning asosiy chegaralarida ekanligini tekshirish
     */
    private boolean isInUzbekistanBounds(Double latitude, Double longitude) {
        return latitude >= UZ_MIN_LAT && latitude <= UZ_MAX_LAT &&
                longitude >= UZ_MIN_LON && longitude <= UZ_MAX_LON;
    }

    /**
     * API dan ma'lumotlarni olish
     */
    private JsonObject fetchLocationData(Double latitude, Double longitude) {
        try {
            String urlString = String.format(Locale.US,
                    "%s?format=jsonv2&lat=%f&lon=%f&accept-language=uz&addressdetails=1",
                    NOMINATIM_URL, latitude, longitude
            );

            return makeApiRequest(urlString);

        } catch (Exception e) {
            log.error("API so'rovida xatolik: {}", e.getMessage());
            return null;
        }
    }

    /**
     * API so'rovini amalga oshirish
     */
    private JsonObject makeApiRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
        conn.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));

        // Rate limiting uchun kutish (Nominatim cheklovlari)
        Thread.sleep(1000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            log.error("API javob kodi: {}", responseCode);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return gson.fromJson(response.toString(), JsonObject.class);
        }
    }

    /**
     * JSON ma'lumotlarini AddressDto ga aylantirish
     */
    private AddressDto parseAddressData(JsonObject jsonResponse, Double lat, Double lon) {
        AddressDto address = new AddressDto();
        address.setLatitude(lat);
        address.setLongitude(lon);

        // To'liq manzil
        if (jsonResponse.has("display_name")) {
            address.setFullAddress(jsonResponse.get("display_name").getAsString());
        }

        // Batafsil manzil komponentlari
        if (jsonResponse.has("address")) {
            JsonObject addressDetails = jsonResponse.getAsJsonObject("address");

            // Mamlakat
            if (addressDetails.has("country")) {
                address.setCountry(addressDetails.get("country").getAsString());
            }
            if (addressDetails.has("country_code")) {
                String countryCode = addressDetails.get("country_code").getAsString();
                if (countryCode != null) {
                    address.setCountryCode(countryCode.toUpperCase());
                }
            }

            // Viloyat/Region
            if (addressDetails.has("state") || addressDetails.has("region")) {
                String region = addressDetails.has("state") ?
                        addressDetails.get("state").getAsString() :
                        addressDetails.get("region").getAsString();
                address.setRegion(region);
            }

            // Shahar/Tuman
            if (addressDetails.has("city")) {
                address.setCity(addressDetails.get("city").getAsString());
            } else if (addressDetails.has("town")) {
                address.setCity(addressDetails.get("town").getAsString());
            } else if (addressDetails.has("village")) {
                address.setCity(addressDetails.get("village").getAsString());
            }

            // Tuman/District
            if (addressDetails.has("county") || addressDetails.has("district")) {
                String district = addressDetails.has("county") ?
                        addressDetails.get("county").getAsString() :
                        addressDetails.get("district").getAsString();
                address.setDistrict(district);
            }

            // Ko'cha
            if (addressDetails.has("road")) {
                address.setStreet(addressDetails.get("road").getAsString());
            }

            // Uy raqami
            if (addressDetails.has("house_number")) {
                address.setHouseNumber(addressDetails.get("house_number").getAsString());
            }

            // Pochta indeksi
            if (addressDetails.has("postcode")) {
                address.setPostalCode(addressDetails.get("postcode").getAsString());
            }
        }

        // O'zbekiston ekanligini tekshirish
        address.setInUzbekistan(isDefinitelyInUzbekistan(address));
        address.setCityEnum(detectCity(address));
        return address;
    }

    private City detectCity(AddressDto address) {

        String city = address.getCity() != null
                ? address.getCity().toLowerCase()
                : "";

        String region = address.getRegion() != null
                ? address.getRegion().toLowerCase()
                : "";

        // Avval city bo‘yicha
        if (city.contains("toshkent")) return City.TASHKENT_CITY;
        if (city.contains("samarqand")) return City.SAMARKAND;
        if (city.contains("buxoro")) return City.BUKHARA;
        if (city.contains("andijon")) return City.ANDIJAN;
        if (city.contains("farg")) return City.FERGANA;
        if (city.contains("namangan")) return City.NAMANGAN;
        if (city.contains("jizzax")) return City.JIZZAKH;
        if (city.contains("guliston")) return City.SIRDARYA;
        if (city.contains("termiz")) return City.SURKHANDARYA;
        if (city.contains("qarshi")) return City.KASHKADARYA;
        if (city.contains("urganch") || city.contains("xiva")) return City.KHOREZM;
        if (city.contains("navoiy")) return City.NAVOIY;
        if (city.contains("nukus")) return City.KARAKALPAKSTAN;

        // Agar city topilmasa → region bo‘yicha
        if (region.contains("toshkent")) return City.TASHKENT_REGION;
        if (region.contains("samarqand")) return City.SAMARKAND;
        if (region.contains("buxoro")) return City.BUKHARA;
        if (region.contains("andijon")) return City.ANDIJAN;
        if (region.contains("farg")) return City.FERGANA;
        if (region.contains("namangan")) return City.NAMANGAN;
        if (region.contains("jizzax")) return City.JIZZAKH;
        if (region.contains("sirdaryo")) return City.SIRDARYA;
        if (region.contains("surxon")) return City.SURKHANDARYA;
        if (region.contains("qashqadaryo")) return City.KASHKADARYA;
        if (region.contains("xorazm")) return City.KHOREZM;
        if (region.contains("navoiy")) return City.NAVOIY;
        if (region.contains("qoraqalpog")) return City.KARAKALPAKSTAN;

        return City.UNKNOWN;
    }

    /**
     * Aniq O'zbekiston ekanligini tekshirish
     */
    private boolean isDefinitelyInUzbekistan(AddressDto address) {
        // 1. Country Code bo'yicha tekshirish
        if (address.getCountryCode() != null && !address.getCountryCode().isEmpty()) {
            for (String code : UZBEKISTAN_COUNTRY_CODES) {
                if (code.equalsIgnoreCase(address.getCountryCode())) {
                    return true;
                }
            }
        }

        // 2. Country Name bo'yicha tekshirish
        if (address.getCountry() != null && !address.getCountry().isEmpty()) {
            String countryName = address.getCountry().toLowerCase();
            for (String name : UZBEKISTAN_COUNTRY_NAMES) {
                if (name.toLowerCase().equals(countryName)) {
                    return true;
                }
            }

            // Qo'shimcha tekshirishlar
            if (countryName.contains("uzbek") ||
                    countryName.contains("o'zbek") ||
                    countryName.contains("узбек")) {
                return true;
            }
        }

        // 3. Region/shahar nomlari orqali tekshirish (O'zbekiston uchun maxsus)
        if (address.getRegion() != null && !address.getRegion().isEmpty()) {
            String region = address.getRegion().toLowerCase();
            String[] uzRegions = {
                    "toshkent", "samarqand", "bukhara", "andijan", "fergana",
                    "namangan", "jizzakh", "sirdaryo", "surxondaryo", "qashqadaryo",
                    "xorazm", "navoiy", "qoraqalpog'iston", "karakalpakstan"
            };

            for (String uzRegion : uzRegions) {
                if (region.contains(uzRegion)) {
                    return true;
                }
            }
        }

        // 4. Shahar nomlari orqali tekshirish
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            String city = address.getCity().toLowerCase();
            String[] uzCities = {
                    "toshkent", "samarqand", "buxoro", "andijon", "farg'ona",
                    "namangan", "jizzax", "guliston", "termiz", "qarshi",
                    "urganch", "navoiy", "nukus", "xiva"
            };

            for (String uzCity : uzCities) {
                if (city.contains(uzCity)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void kk(String[] args) {
        ApiLocationServiceImpl service = new ApiLocationServiceImpl();

        // Test koordinatalari
        // Toshkent
        Double lat1 = 41.311081;
        Double lon1 = 69.240562;

        // Samarqand
        Double lat2 = 39.654167;
        Double lon2 = 66.959722;

        // Moskva (O'zbekiston emas)
        Double lat3 = 55.755826;
        Double lon3 = 37.617300;

        System.out.println(service.getAddress(lat1, lon1));
    }
}