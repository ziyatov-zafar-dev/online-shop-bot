package uz.zafar.onlineshoptelegrambot.service;

import jakarta.servlet.http.HttpServletRequest;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.gson.IpWhoIsResponseDto;

public interface GsonService {
    ResponseDto<IpWhoIsResponseDto> getLocation(HttpServletRequest request) throws Exception;
    ResponseDto<IpWhoIsResponseDto> getLocation(String ip) throws Exception;
}
