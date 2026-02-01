package uz.zafar.onlineshoptelegrambot.dto.gson;

public class LocationStatus {
    private boolean success;
    private String message;
    private AddressDto address;
    private String error;

    public static LocationStatus success(AddressDto address) {
        LocationStatus status = new LocationStatus();
        status.setSuccess(true);
        status.setAddress(address);
        status.setMessage("Manzil muvaffaqiyatli aniqlandi");
        return status;
    }
    public static LocationStatus error(String errorMessage) {
        LocationStatus status = new LocationStatus();
        status.setSuccess(false);
        status.setError(errorMessage);
        status.setMessage("Manzil aniqlashda xatolik");
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
