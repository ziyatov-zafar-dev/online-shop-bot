package uz.zafar.onlineshoptelegrambot.botservice;

import org.springframework.web.multipart.MultipartFile;
import uz.zafar.onlineshoptelegrambot.botservice.impl.FileServiceImpl;

public interface FileService {
    FileServiceImpl.ResponseDtoThis<FileServiceImpl.FileStore> upload(MultipartFile file, String dir);
}
