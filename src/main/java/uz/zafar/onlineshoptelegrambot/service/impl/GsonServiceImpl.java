package uz.zafar.onlineshoptelegrambot.service.impl;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.gson.IpWhoIsResponseDto;
import uz.zafar.onlineshoptelegrambot.service.GsonService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GsonServiceImpl implements GsonService {

    private final Gson gson = new Gson();

    @Override
    public ResponseDto<IpWhoIsResponseDto> getLocation(String ip) throws Exception {
        String targetIp = ip;
        if (ip == null || ip.isEmpty() || ip.trim().isEmpty()) {
            targetIp = getPublicIpAddress();
        }

        targetIp = targetIp.trim();

        String apiUrl = "https://ipwho.is/" + targetIp;
        String jsonResponse = sendGetRequest(apiUrl);

        IpWhoIsResponseDto responseDto = gson.fromJson(jsonResponse, IpWhoIsResponseDto.class);

        if (responseDto != null && responseDto.isSuccess()) {
            return ResponseDto.success(responseDto);
        } else {
            throw new Exception("IP lookup failed for address: " + targetIp);
        }
    }

    @Override
    public ResponseDto<IpWhoIsResponseDto> getLocation(HttpServletRequest request) throws Exception {
        {
            String clientIp = getClientIpAddress(request);

            if (clientIp == null || clientIp.isEmpty() || clientIp.equals("127.0.0.1")) {
                clientIp = getPublicIpAddress();
            }

            String apiUrl = "https://ipwho.is/" + clientIp;
            String jsonResponse = sendGetRequest(apiUrl);

            IpWhoIsResponseDto responseDto = gson.fromJson(jsonResponse, IpWhoIsResponseDto.class);
            return ResponseDto.success(
                    responseDto
            );
        }
    }


    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    private String getPublicIpAddress() throws Exception {
        String[] ipServices = {
                "https://api.ipify.org",
                "https://icanhazip.com",
                "https://checkip.amazonaws.com"
        };

        for (String service : ipServices) {
            try {
                return sendGetRequest(service).trim();
            } catch (Exception ignored) {
            }
        }
        throw new Exception("Unable to get public IP address");
    }

    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new Exception("HTTP request failed with code: " + responseCode);
        }
    }
}