package uz.zafar.onlineshoptelegrambot.rest.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileUploadRestController {

    private final TelegramProperties telegramProperties;

    @Value("${file.upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${file.upload.max-size:104850000760}")
    private Long maxFileSize;

    public FileUploadRestController(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseDto<FileUploadResponseDto> upload(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Fayl bo'shligini tekshirish
            if (file.isEmpty()) {
                return ResponseDto.error(ErrorCode.FILE_IS_EMPTY);
            }

            // Fayl hajmini tekshirish
            if (file.getSize() > maxFileSize) {
                return ResponseDto.error(ErrorCode.FILE_TOO_LARGE);
            }

            // Fayl formatini tekshirish
            String originalFilename = file.getOriginalFilename();
            if (!isAllowedFileType(originalFilename)) {
                return ResponseDto.error(ErrorCode.INVALID_FILE_TYPE);
            }

            // Upload papkasini yaratish
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Xavfsiz fayl nomini yaratish
            String fileExtension = getFileExtension(originalFilename);
            String safeFileName = Util.generateSafeFileName(LocalDateTime.now().toString()) + UUID.randomUUID() +
                    (!fileExtension.isEmpty() ? "." + fileExtension : "");
            String last = safeFileName.substring(safeFileName.lastIndexOf('.'));
            safeFileName = safeFileName.replace("-", "");
            safeFileName = safeFileName.replace(".", "");
            safeFileName = safeFileName.concat(last);
            Path filePath = uploadPath.resolve(safeFileName);
            Files.copy(file.getInputStream(), filePath);
            String fileUrl = telegramProperties.getBaseUrl() + "/uploads/" + safeFileName;
            FileUploadResponseDto response = new FileUploadResponseDto();
            response.setFileName(file.getOriginalFilename());
            response.setFileSize(file.getSize());
            response.setFileUrl(fileUrl);
            response.setSavedFileName(safeFileName);
            return ResponseDto.success(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseDto.error(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseDto<Boolean> deleteFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDirectory, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return ResponseDto.success(true);
            }
            return ResponseDto.error(ErrorCode.FILE_NOT_FOUND);
        } catch (IOException e) {
            return ResponseDto.error(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    private boolean isAllowedFileType(String filename) {
        if (filename == null) return false;

        String extension = getFileExtension(filename);
        if (extension.isEmpty()) return false;

        String lowerExtension = extension.toLowerCase();
        String[] extensions = {
                "jpg", "jpeg", "png", "gif",
                "pdf", "doc", "docx", "xls",
                "xlsx", "webp", "mp4", "m4v", "mkv", "avi", "mov",
                "wmv", "flv", "webm", "3gp", "3g2",
                "mpeg", "mpg", "ts", "mts", "m2ts"
        };
        for (String allowed : extensions) {
            if (allowed.equals(lowerExtension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public static class FileUploadResponseDto {
        private String fileUrl;
        private String fileName;
        private String savedFileName;
        private Long fileSize;

        // Getter va Setter lar
        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getSavedFileName() {
            return savedFileName;
        }

        public void setSavedFileName(String savedFileName) {
            this.savedFileName = savedFileName;
        }
    }
}