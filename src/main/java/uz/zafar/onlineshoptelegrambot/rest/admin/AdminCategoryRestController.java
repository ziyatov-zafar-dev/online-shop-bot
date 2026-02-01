package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.product.response.CategoryResponse;
import uz.zafar.onlineshoptelegrambot.mapper.CategoryMapper;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("api/admin/category")
public class AdminCategoryRestController {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public AdminCategoryRestController(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("list")
    public ResponseDto<?> list() {
        List<Category> parentCategories = categoryRepository.getParentCategories();
        return ResponseDto.success(categoryMapper.toList(parentCategories));
    }

    @GetMapping("find-by-id/{cid}")
    public ResponseDto<?> findById(@PathVariable("cid") UUID cid) {
        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        return ResponseDto.success(categoryMapper.toResponse(category));
    }

    @PostMapping("create")
    public ResponseDto<?> addCategory(@RequestBody CreateCategoryRequestDto req) {
        Category category = new Category();
        category.setNameUz(req.getNameUz());
        category.setNameCyr(req.getNameCyr());
        category.setNameEn(req.getNameEn());
        category.setNameRu(req.getNameRu());
        category.setSlug(toSlug(req.getNameEn()));
        category.setDescriptionUz(req.getDescriptionUz());
        category.setDescriptionCyr(req.descriptionCyr);
        category.setDescriptionEn(req.descriptionEn);
        category.setDescriptionRu(req.descriptionRu);
        category.setOrderNumber(req.orderNumber);
        category.setImageUrl(req.imageUrl);
        category.setActive(true);
        if (req.getParentId() == null) category.setParent(null);
        else category.setParent(categoryRepository.findById(req.parentId).orElse(null));
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);
        return ResponseDto.success(categoryMapper.toResponse(category));
    }


    @PutMapping("edit/{categoryId}")
    public ResponseDto<?> editCategory(@PathVariable("categoryId") UUID cid, @RequestBody CreateCategoryRequestDto req) {
        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        category.setNameUz(req.getNameUz());
        category.setNameCyr(req.getNameCyr());
        category.setNameEn(req.getNameEn());
        category.setNameRu(req.getNameRu());
        category.setSlug(toSlug(req.getNameEn()));
        category.setDescriptionUz(req.getDescriptionUz());
        category.setDescriptionCyr(req.descriptionCyr);
        category.setDescriptionEn(req.descriptionEn);
        category.setDescriptionRu(req.descriptionRu);
        category.setOrderNumber(req.orderNumber);
        category.setImageUrl(req.imageUrl);
        category.setActive(true);
        if (req.getParentId() == null) category.setParent(null);
        else category.setParent(categoryRepository.findById(req.parentId).orElse(null));
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);
        return ResponseDto.success(categoryMapper.toResponse(category));
    }

    @DeleteMapping("delete/{categoryId}")
    public ResponseDto<?> delete(@PathVariable("categoryId") UUID cid) {
        Category category = categoryRepository.findById(cid).orElse(null);
        if (category == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        category.setActive(false);
        category.setNameRu(UUID.randomUUID() + String.valueOf(System.currentTimeMillis()));
        category.setNameEn(UUID.randomUUID() + String.valueOf(System.currentTimeMillis()));
        category.setNameCyr(UUID.randomUUID() + String.valueOf(System.currentTimeMillis()));
        category.setNameUz(UUID.randomUUID() + String.valueOf(System.currentTimeMillis()));
        categoryRepository.save(category);
        return ResponseDto.success();
    }

    public String toSlug(String input) {
        if (input == null) return null;

        String slug = input.toLowerCase(Locale.ROOT);

        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // ü, ñ kabi belgilarni oddiy harfga aylantirish

        // 3️⃣ Bo‘sh joyni '-' bilan almashtirish
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // 4️⃣ Qolgan maxsus belgilarni olib tashlash
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // 5️⃣ Ko‘p tirelarni bitta tirega kamaytirish
        slug = slug.replaceAll("-{2,}", "-");

        // 6️⃣ Boshi va oxiridagi tirelarni olib tashlash
        slug = slug.replaceAll("^-|-$", "");
        return slug;
    }

    public static class CreateCategoryRequestDto {
        private String nameUz;
        private String nameCyr;
        private String nameEn;
        private String nameRu;
        private String descriptionUz;
        private String descriptionCyr;
        private String descriptionRu;
        private String descriptionEn;
        private Integer orderNumber;
        private String imageUrl;
        private UUID parentId;

        public String getNameUz() {
            return nameUz;
        }

        public void setNameUz(String nameUz) {
            this.nameUz = nameUz;
        }

        public String getNameCyr() {
            return nameCyr;
        }

        public void setNameCyr(String nameCyr) {
            this.nameCyr = nameCyr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getNameRu() {
            return nameRu;
        }

        public void setNameRu(String nameRu) {
            this.nameRu = nameRu;
        }

        public String getDescriptionUz() {
            return descriptionUz;
        }

        public void setDescriptionUz(String descriptionUz) {
            this.descriptionUz = descriptionUz;
        }

        public String getDescriptionCyr() {
            return descriptionCyr;
        }

        public void setDescriptionCyr(String descriptionCyr) {
            this.descriptionCyr = descriptionCyr;
        }

        public String getDescriptionRu() {
            return descriptionRu;
        }

        public void setDescriptionRu(String descriptionRu) {
            this.descriptionRu = descriptionRu;
        }

        public String getDescriptionEn() {
            return descriptionEn;
        }

        public void setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
        }

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public UUID getParentId() {
            return parentId;
        }

        public void setParentId(UUID parentId) {
            this.parentId = parentId;
        }
    }
}
