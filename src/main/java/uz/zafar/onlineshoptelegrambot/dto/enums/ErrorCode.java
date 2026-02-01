package uz.zafar.onlineshoptelegrambot.dto.enums;

public enum ErrorCode {
    NONE("Muvaffaqiyatli", "Успешно", "Success", "Муваффақиятли"),
    NOT_CONFIRMED_ACCOUNT("Hisob tasdiqlanmagan", "Аккаунт не подтвержден", "Account not confirmed", "Ҳисоб тасдиқланмаган"),
    BAD_CREDENTIALS("Login yoki parol xato", "Неверные учетные данные", "Invalid credentials", "Логин ёки парол хато"),
    INVALID_VERIFICATION_CODE("Tasdiqlash kodi noto'g'ri", "Неверный код подтверждения", "Invalid verification code", "Тасдиқлаш коди нотўғри"),
    EMAIL_ALREADY_EXISTS("Email mavjud", "Email уже существует", "Email already exists", "Электрон почта мавжуд"),
    USERNAME_ALREADY_EXISTS("Username mavjud", "Имя пользователя уже существует", "Username already exists", "Фойдаланувчи номи мавжуд"),
    PASSWORDS_NOT_MATCH("Parollar mos emas", "Пароли не совпадают", "Passwords do not match", "Пароллар мос эмас"),
    INVALID_USERNAME("Username noto'g'ri", "Неверное имя пользователя", "Invalid username", "Фойдаланувчи номи нотўғри"),
    UNAUTHORIZED(
            "Login yoki parol noto‘g‘ri. Iltimos, qayta urinib ko‘ring",
            "Логин ёки парол нотўғри. Илтимос, қайта уриниб кўринг",
            "Логин или пароль неверны. Пожалуйста, попробуйте снова",
            "Invalid username or password. Please try again"
    ),
    NOT_FOUND_BOT_SELLER(
            "Sotuvchi topilmadi",
            "Сотувчи топилмади",
            "Продавец не найден",
            "Seller not found"
    ), NOT_FOUND_SELLER(
            "Sotuvchi topilmadi",
            "Сотувчи топилмади",
            "Продавец не найден",
            "Seller not found"
    ), REJECTED(
            "Rad etilgan",
            "Рад этилган",
            "Отклонено",
            "Rejected"
    ), SELLER_ALREADY_EXISTS(
            "Sotuvchi allaqachon mavjud",
            "Сотувчи аллақачон мавжуд",
            "Продавец уже существует",
            "Seller already exists"
    ),

    FILE_IS_EMPTY("Fayl bo'sh", "Файл бош", "Файл пустой", "File is empty"),
    FILE_TOO_LARGE("Fayl hajmi juda katta", "Файл ҳажми жуда катта", "Файл слишком большой", "File too large"),
    INVALID_FILE_TYPE("Noto'g'ri fayl formati", "Нотўғри файл формати", "Неверный формат файла", "Invalid file type"),
    FILE_UPLOAD_ERROR("Fayl yuklashda xato", "Файл юклашда хато", "Ошибка загрузки файла", "File upload error"),
    FILE_NOT_FOUND("Fayl topilmadi", "Файл топилмади", "Файл не найден", "File not found"),
    FILE_DELETE_ERROR("Faylni o'chirishda xato", "Файлни ўчиришда хато", "Ошибка удаления файла", "File delete error"), NOT_FOUND_EMAIL(
            "Bunday email mavjud emas",
            "Такой адрес электронной почты не существует",
            "Такой адрес электронной почты не существует",
            "This email address does not exist"
    ),
    NOT_FOUND_SHOP("Shop topilmadi", "Шоп топилмади", "Shop not found", "Shop не найден"),
    ERROR("exception", "exception", "exception", "exception"),
    NOT_FOUND_CATEGORY("Kategoriya topilmadi",
            "Категория топилмади",
            "Категория не найдена",
            "Category not found"),
    NOT_FOUND_PRODUCT(
            "Mahsulot topilmadi",
            "Маҳсулот топилмади",
            "Product not found",
            "Товар не найден"
    ),
    REQUIRED_PRODUCT_TYPES(
            "Mahsulot turlari majburiy",
            "Маҳсулот турлари мажбурий",
            "Product types are required",
            "Типы товаров обязательны"
    ),
    REQUIRED_PRODUCT_TYPE_IMAGES(
            "Mahsulot turi rasmlari majburiy",
            "Маҳсулот тури расмлари мажбурий",
            "Product type images are required",
            "Изображения типа товара обязательны"
    ), SHOP_NOT_CONFIRMED_ADMIN(
            "Do‘kon administrator tomonidan tasdiqlanmagan",
            "Дўкон администратор томонидан тасдиқланмаган",
            "The shop has not been confirmed by the administrator",
            "Магазин не подтверждён администратором"
    ),
    NOT_FOUND_DISCOUNT("Chegirma topilmadi",
            "Чегирма топилмади",
            "Скидка не найдена",
            "Discount not found"), ALREADY_DISCOUNT_EXISTS("Bu mahsulot uchun chegirma allaqachon mavjud", "Бу маҳсулот учун чегирма аллақачон мавжуд", "Для этого товара скидка уже существует", "A discount already exists for this product"),
    TYPE_NOT_FOUND("Mahsulot turi topilmadi, Mahsulot turi topilmadi, Mahsulot turi topilmadi, Mahsulot turi topilmadi",
            "Маҳсулот тури топилмади, Маҳсулот тури топилмади, Маҳсулот тури топилмади, Маҳсулот тури топилмади",
            "Тип товара не найден, Тип товара не найден, Тип товара не найден, Тип товара не найден", "roduct type not found, Product type not found, Product type not found, Product type not found"),
    NOT_FOUND_IMAGE(
            "Rasm topilmadi, Rasm topilmadi, Rasm topilmadi, Rasm topilmadi",
            "Расм топилмади, Расм топилмади, Расм топилмади, Расм топилмади",
            "Изображение не найдено, Изображение не найдено, Изображение не найдено, Изображение не найдено",
            "Image not found, Image not found, Image not found, Image not found"
    ), NOT_FOUND_CUSTOMER(
            "Mijoz topilmadi",
            "Мижоз топилмади",
            "Клиент не найден",
            "Customer not found"
    ), NOT_FOUND_SUBSCRIPTION_PLAN(
            "Obuna rejasi topilmadi",        // UZ - O'zbek
            "Обунa режаси топилмади",       // CYR - O'zbek kirill
            "План подписки не найден",       // RU - Rus
            "Subscription plan not found"     // EN - Ingliz
    ), OUT_OF_STOCK(
            "Mahsulot omborda yetarli emas",
            "Маҳсулот омборда етарли эмас",
            "Товара нет в достаточном количестве",
            "Product is out of stock"
    ),
    ;
    private final String descriptionUz;
    private final String descriptionCyrillic;
    private final String descriptionRu;
    private final String descriptionEn;

    ErrorCode(String descriptionUz, String descriptionCyrillic, String descriptionRu, String descriptionEn) {
        this.descriptionUz = descriptionUz;
        this.descriptionCyrillic = descriptionCyrillic;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }


    public String getDescriptionUz() {
        return descriptionUz;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getDescriptionCyrillic() {
        return descriptionCyrillic;
    }
}
