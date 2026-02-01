package uz.zafar.onlineshoptelegrambot.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class SignInVerifyRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String code;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
