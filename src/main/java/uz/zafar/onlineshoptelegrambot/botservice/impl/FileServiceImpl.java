package uz.zafar.onlineshoptelegrambot.botservice.impl;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.botservice.FileService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService {
    private final TelegramProperties prop;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._ -]+$");
    private static final String[] ALLOWED_EXTENSIONS = {
            ".jpg", ".jpeg", ".png", ".gif", ".pdf", ".doc", ".docx",
            ".xls", ".xlsx", ".txt", ".mp4", ".mp3", ".zip", ".pptx", ".ppt"
    };
    @Value("${file.upload-dir:uploads}")
    private String BASE_UPLOAD_DIR;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;


    public FileServiceImpl(TelegramProperties prop) {
        this.prop = prop;
    }


    @Override

    public ResponseDtoThis<FileStore> upload(MultipartFile file, String dir) {
        String originalFileName = null;
        try {
            // 1. Fayl bo'shligini tekshirish
            if (file.isEmpty()) {
                return createErrorResponse("Fayl bo'sh", "Файл бўш",
                        "File is empty", "Файл пуст");
            }

            // 2. Fayl hajmini tekshirish (10 MB)
            if (file.getSize() > MAX_FILE_SIZE) {
                return createErrorResponse(
                        "Fayl hajmi 10 MB dan oshmasligi kerak",
                        "Файл ҳажми 10 МБ дан ошмаслиги керак",
                        "File size must not exceed 10 MB",
                        "Размер файла не должен превышать 10 МБ");
            }

            originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                return createErrorResponse("Fayl nomi noto'g'ri", "Файл номи нотўғри",
                        "Invalid file name", "Недопустимое имя файла");
            }

            if (!isSafeFilename(originalFileName)) {
                return createErrorResponse(
                        "Fayl nomida ruxsat etilmagan belgilar mavjud",
                        "Файл номида рухсат этилмаган белгилар мавжуд",
                        "File name contains invalid characters",
                        "Имя файла содержит недопустимые символы");
            }

            String fileExtension = getFileExtension(originalFileName).toLowerCase();
            if (!isAllowedExtension(fileExtension)) {
                String allowedFormats = String.join(", ", ALLOWED_EXTENSIONS);
                return createErrorResponse(
                        "Bu turdagi fayllarni yuklash mumkin emas. Ruxsat etilgan formatlar: " + allowedFormats,
                        "Бу турдаги файлларни юклаш мумкин эмас. Рухсат этилган форматлар: " + allowedFormats,
                        "This type of files cannot be uploaded. Allowed formats: " + allowedFormats,
                        "Этот тип файлов не может быть загружен. Разрешенные форматы: " + allowedFormats);
            }

            LocalDateTime now = LocalDateTime.now();

            Path filePath = createProductFilePath(now, originalFileName, dir);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            if (!Files.exists(filePath)) {
                return createErrorResponse("Fayl saqlanmadi",
                        "Файл сақланмади",
                        "File was not saved",
                        "Файл не был сохранен");
            }

            long actualFileSize = Files.size(filePath);
            if (actualFileSize > MAX_FILE_SIZE) {
                Files.deleteIfExists(filePath);
                return createErrorResponse("Fayl hajmi chegaradan oshib ketdi",
                        "Файл ҳажми чегарадан ошиб кетди",
                        "File size exceeded the limit",
                        "Размер файла превысил лимит");
            }

            String relativePath = getRelativePath(filePath);
            String fileUrl = getFileUrl(relativePath);

            FileStore store = new FileStore();
            store.setFileName(originalFileName);
            store.setSafeFileName(filePath.getFileName().toString());
            store.setFileUrl(prop.getBaseUrl() + fileUrl);
            store.setRelativePath(relativePath);
            store.setAbsolutePath(filePath.toAbsolutePath().toString());
            store.setSize(actualFileSize);
            store.setFileExtension(fileExtension);
            store.setUploadTime(now);
            store.setDirectoryStructure(getGeneralDirectoryStructure(now));
            store.setMimeType(file.getContentType());
            store.setFileType("general");

            return createSuccessResponse(store, "Fayl muvaffaqiyatli yuklandi",
                    "Файл муваффақиятли юкланди",
                    "File uploaded successfully",
                    "Файл успешно загружен");

        } catch (IOException e) {
            return createErrorResponse("Fayl yuklashda xatolik: " + e.getMessage(),
                    "Файл юклашда хатолик: " + e.getMessage(),
                    "File upload error: " + e.getMessage(),
                    "Ошибка загрузки файла: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Kutilmagan xatolik: " + e.getMessage(),
                    "Кутилмаган хатолик: " + e.getMessage(),
                    "Unexpected error: " + e.getMessage(),
                    "Неожиданная ошибка: " + e.getMessage());
        }

    }


    private Path createProductFilePath(LocalDateTime dateTime, String originalFilename, String dir) {
        String year = String.valueOf(dateTime.getYear());
        String month = String.format("%02d", dateTime.getMonthValue());
        String day = String.format("%02d", dateTime.getDayOfMonth());
        String hour = String.format("%02d", dateTime.getHour());
        String minute = String.format("%02d", dateTime.getMinute());
        String second = String.format("%02d", dateTime.getSecond());

        String fileExtension = getFileExtension(originalFilename);
        String safeFileName = generateSafeFileName(originalFilename);

        // Asosiy uploads papkasini absolute pathga o'giramiz
        Path baseDir = Paths.get(BASE_UPLOAD_DIR).toAbsolutePath();

        // To'liq yo'lni yaratish: BASE_UPLOAD_DIR/products/{year}/{month}/{day}/{hour}/{minute}/{second}/filename
        Path fullPath = Paths.get(
                baseDir.toString(),
                dir,
                year,
                month,
                day,
                hour,
                minute,
                second,
                safeFileName
        );

        // Agar fayl allaqachon mavjud bo'lsa, unique nom yaratish
        if (Files.exists(fullPath)) {
            safeFileName = generateUniqueSafeFileName(originalFilename);
            fullPath = Paths.get(
                    baseDir.toString(),
                    dir,
                    year,
                    month,
                    day,
                    hour,
                    minute,
                    second,
                    safeFileName
            );
        }

        return fullPath;
    }

    /**
     * Fayl nomini xavfsizligini tekshirish
     */
    private boolean isSafeFilename(String filename) {
        if (filename == null) return false;

        // Directory traversal hujumlarini oldini olish
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }

        // Asosiy fayl nomini (kengaytmasiz) olish
        String nameWithoutExtension = filename;
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            nameWithoutExtension = filename.substring(0, lastDotIndex);
        }

        // Faqat ruxsat etilgan belgilardan iboratligini tekshirish
        return SAFE_FILENAME_PATTERN.matcher(nameWithoutExtension).matches();
    }

    /**
     * Fayl kengaytmasini olish
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    /**
     * Ruxsat etilgan kengaytmalarni tekshirish
     */
    private boolean isAllowedExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Xavfsiz fayl nomi yaratish
     */
    private String generateSafeFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String nameWithoutExtension;

        if (originalFilename.contains(".")) {
            nameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        } else {
            nameWithoutExtension = originalFilename;
            extension = "";
        }

        String safeName = nameWithoutExtension
                .replaceAll("[^a-zA-Z0-9._ -]", "_")
                .replaceAll("_{2,}", "_")
                .trim();

        if (safeName.isEmpty()) {
            safeName = "file";
        }

        if (safeName.length() > 50) {
            safeName = safeName.substring(0, 50);
        }

        String uniqueId = UUID.randomUUID().toString().substring(0, 6);

        // Fayl nomi va UUID ni birlashtirish
        return String.format("%s_%s%s", safeName, uniqueId, extension);
    }

    /**
     * Takrorlanmas xavfsiz fayl nomi yaratish
     */
    private String generateUniqueSafeFileName(String originalFilename) {
        String baseName = generateSafeFileName(originalFilename);
        String extension = getFileExtension(baseName);
        String nameWithoutExtension;

        if (baseName.contains(".")) {
            nameWithoutExtension = baseName.substring(0, baseName.lastIndexOf('.'));
        } else {
            nameWithoutExtension = baseName;
            extension = "";
        }

        return String.format("%s_%s%s",
                nameWithoutExtension,
                UUID.randomUUID().toString().substring(0, 8),
                extension);
    }

    /**
     * Nisbiy yo'lni olish
     */
    private String getRelativePath(Path absolutePath) {
        try {
            Path baseDir = Paths.get(BASE_UPLOAD_DIR).toAbsolutePath();
            return baseDir.relativize(absolutePath).toString().replace("\\", "/");
        } catch (Exception e) {
            return absolutePath.getFileName().toString();
        }
    }

    /**
     * URL uchun to'liq yo'lni olish
     */
    private String getFileUrl(String relativePath) {
        // Agar contextPath bo'sh yoki "/" bo'lsa
        if (contextPath == null || contextPath.isEmpty() || contextPath.equals("/")) {
            return "/uploads/" + relativePath;
        } else {
            return contextPath + "/uploads/" + relativePath;
        }
    }

    /**
     * Umumiy fayllar uchun papka strukturasi
     */
    private String getGeneralDirectoryStructure(LocalDateTime dateTime) {
        return String.format("general/%d/%02d/%02d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth());
    }

    /**
     * Product fayllari uchun papka strukturasi
     */
    /**
     * Product fayllari uchun papka strukturasi
     */
    private String getProductDirectoryStructure(LocalDateTime dateTime) {
        return String.format("products/%d/%02d/%02d/%02d/%02d/%02d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond());
    }

    /**
     * Xatolik javobini yaratish
     */
    private ResponseDtoThis<FileStore> createErrorResponse(String uz, String uzCyr, String en, String ru) {
        return ResponseDtoThis.<FileStore>builder()
                .success(false)
                .messageUz(uz)
                .messageUzCyrillic(uzCyr)
                .messageEn(en)
                .messageRu(ru)
                .data(null)
                .build();
    }

    /**
     * Muvaffaqiyatli javobni yaratish
     */
    private ResponseDtoThis<FileStore> createSuccessResponse(FileStore store, String uz, String uzCyr, String en, String ru) {
        return ResponseDtoThis.<FileStore>builder()
                .success(true)
                .messageUz(uz)
                .messageUzCyrillic(uzCyr)
                .messageEn(en)
                .messageRu(ru)
                .data(store)
                .build();
    }

    /**
     * Fayl ma'lumotlari uchun DTO class
     */
    public static class FileStore {
        private String fileName;          // Original fayl nomi
        private String safeFileName;      // Xavfsiz saqlangan fayl nomi
        private String fileUrl;           // To'liq URL
        private String relativePath;      // Nisbiy yo'l
        private String absolutePath;      // To'liq yo'l
        private Long size;                // Fayl hajmi
        private String fileExtension;     // Fayl kengaytmasi
        private String mimeType;          // Fayl MIME turi
        private LocalDateTime uploadTime; // Yuklangan vaqt
        private String directoryStructure; // Papka strukturasi
        private String fileType;          // Fayl turi (general/product)

        // Getter va Setter metodlari
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getSafeFileName() {
            return safeFileName;
        }

        public void setSafeFileName(String safeFileName) {
            this.safeFileName = safeFileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public void setRelativePath(String relativePath) {
            this.relativePath = relativePath;
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        public void setAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public LocalDateTime getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(LocalDateTime uploadTime) {
            this.uploadTime = uploadTime;
        }

        public String getDirectoryStructure() {
            return directoryStructure;
        }

        public void setDirectoryStructure(String directoryStructure) {
            this.directoryStructure = directoryStructure;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        @Override
        public String toString() {
            return "FileStore{" +
                    "fileName='" + fileName + '\'' +
                    ", safeFileName='" + safeFileName + '\'' +
                    ", fileUrl='" + fileUrl + '\'' +
                    ", relativePath='" + relativePath + '\'' +
                    ", size=" + size +
                    ", fileExtension='" + fileExtension + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    ", uploadTime=" + uploadTime +
                    ", fileType='" + fileType + '\'' +
                    '}';
        }
    }

    public static class ResponseDtoThis<T> {
        private boolean success;
        private String messageUz;
        private String messageUzCyrillic;
        private String messageEn;
        private String messageRu;
        private T data;

        // Builder pattern uchun
        public static <T> ResponseDtoThisBuilder<T> builder() {
            return new ResponseDtoThisBuilder<>();
        }

        public static class ResponseDtoThisBuilder<T> {
            private boolean success;
            private String messageUz;
            private String messageUzCyrillic;
            private String messageEn;
            private String messageRu;
            private T data;

            public ResponseDtoThisBuilder<T> success(boolean success) {
                this.success = success;
                return this;
            }

            public ResponseDtoThisBuilder<T> messageUz(String messageUz) {
                this.messageUz = messageUz;
                return this;
            }

            public ResponseDtoThisBuilder<T> messageUzCyrillic(String messageUzCyrillic) {
                this.messageUzCyrillic = messageUzCyrillic;
                return this;
            }

            public ResponseDtoThisBuilder<T> messageEn(String messageEn) {
                this.messageEn = messageEn;
                return this;
            }

            public ResponseDtoThisBuilder<T> messageRu(String messageRu) {
                this.messageRu = messageRu;
                return this;
            }

            public ResponseDtoThisBuilder<T> data(T data) {
                this.data = data;
                return this;
            }

            public ResponseDtoThis<T> build() {
                ResponseDtoThis<T> response = new ResponseDtoThis<>();
                response.success = this.success;
                response.messageUz = this.messageUz;
                response.messageUzCyrillic = this.messageUzCyrillic;
                response.messageEn = this.messageEn;
                response.messageRu = this.messageRu;
                response.data = this.data;
                return response;
            }
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessageUz() {
            return messageUz;
        }

        public void setMessageUz(String messageUz) {
            this.messageUz = messageUz;
        }

        public String getMessageUzCyrillic() {
            return messageUzCyrillic;
        }

        public void setMessageUzCyrillic(String messageUzCyrillic) {
            this.messageUzCyrillic = messageUzCyrillic;
        }

        public String getMessageEn() {
            return messageEn;
        }

        public void setMessageEn(String messageEn) {
            this.messageEn = messageEn;
        }

        public String getMessageRu() {
            return messageRu;
        }

        public void setMessageRu(String messageRu) {
            this.messageRu = messageRu;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }


    public String getTelegramFilePath(String botToken, String fileId) throws Exception {
        String url = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());
            return json.getJSONObject("result").getString("file_path");
        }
    }


}
