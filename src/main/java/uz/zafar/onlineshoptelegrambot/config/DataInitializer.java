/*
package uz.zafar.onlineshoptelegrambot.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.SubscriptionPlanRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;


    public DataInitializer(SubscriptionPlanRepository subscriptionPlanRepository, CategoryRepository categoryRepository, ProductTypeRepository productTypeRepository, ProductTypeImageRepository productTypeImageRepository, ShopRepository shopRepository, ProductRepository productRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.categoryRepository = categoryRepository;
        this.productTypeRepository = productTypeRepository;
        this.productTypeImageRepository = productTypeImageRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        initPlans();
        initCategories();
        initProducts();
    }

    private void initPlans() {
        if (subscriptionPlanRepository.findAll().isEmpty()) {
            String[] prices = {"200000", "140000", "150000", "200000", "140000", "150000", "200000", "140000", "150000"};
            int i = 0;
            for (SubscriptionPlanType value : SubscriptionPlanType.values()) {
                SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                subscriptionPlan.setName(value);
                subscriptionPlan.setDiscount(false);
                subscriptionPlan.setPrice((value == SubscriptionPlanType.EXPIRED || value == SubscriptionPlanType.TRIAL) ? BigDecimal.ZERO : new BigDecimal(prices[i]));
                subscriptionPlan.setCreatedAt(LocalDateTime.now());
                subscriptionPlan.setUpdatedAt(LocalDateTime.now());
                subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
                i++;
            }
        }
    }

    private void initProducts() {

        if (!productRepository.findAll().isEmpty()) return;

        Shop shop = shopRepository.findAll().get(0);
        Category android = categoryRepository.findBySlug("android").orElseThrow();

        Product samsung = product(
                "Samsung Galaxy S23",
                "Самсунг Галакси S23",
                "Samsung Galaxy S23",
                "Самсунг Галакси S23",
                "SKU-S23",
                android,
                shop
        );

        productRepository.save(samsung);

        // ================== PRODUCT TYPES ==================
        ProductType s23_128 = productType(
                samsung,
                "128GB Qora",
                "128GB Қора",
                "128GB Black",
                "128GB Чёрный",
                new BigDecimal("9500000"),
                15
        );

        ProductType s23_256 = productType(
                samsung,
                "256GB Kumush",
                "256GB Кумуш",
                "256GB Silver",
                "256GB Серебро",
                new BigDecimal("10200000"),
                10
        );

        productTypeRepository.saveAll(List.of(s23_128, s23_256));

        // ================== PRODUCT TYPE IMAGES ==================
        productTypeImageRepository.saveAll(List.of(
                image(s23_128, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf", true),
                image(s23_128, "https://images.unsplash.com/photo-1580910051074-7c9a7c1fdf33", false),

                image(s23_256, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf", true),
                image(s23_256, "https://images.unsplash.com/photo-1580910051074-7c9a7c1fdf33", false)
        ));
    }

    private ProductType productType(Product product, String uz, String cyr, String en, String ru,
                                    BigDecimal price, int stock) {
        ProductType pt = new ProductType();
        pt.setProduct(product);
        pt.setNameUz(uz);
        pt.setNameCyr(cyr);
        pt.setNameEn(en);
        pt.setNameRu(ru);
        pt.setPrice(price);
        pt.setStock(stock);
        pt.setDeleted(false);
        return pt;
    }

    private ProductTypeImage image(ProductType pt, String url, boolean main) {
        ProductTypeImage img = new ProductTypeImage();
        img.setProductType(pt);
        img.setImageUrl(url);
        img.setImgName("image-" + System.nanoTime());
        img.setImgSize(500_000L);
        img.setMain(main);
        img.setDeleted(false);
        return img;
    }

    public void initCategories() {

        if (!categoryRepository.findAll().isEmpty()) {
            return;
        }

        // ========== ROOT (6 ta) ==========
        Category electronics = rootCat("Elektronika", "Электроника", "Electronics", "Электроника", "electronics", 1, "https://images.unsplash.com/photo-1518770660439-4636190af475");

        Category clothes = rootCat("Kiyim-kechak", "Кийим-кечак", "Clothes", "Одежда", "clothes", 2, "https://images.unsplash.com/photo-1521335629791-ce4aec67dd47");

        Category home = rootCat("Uy-ro‘zg‘or", "Уй-рўзғор", "Home", "Дом", "home", 3, "https://images.unsplash.com/photo-1505691938895-1758d7feb511");

        Category sport = rootCat("Sport", "Спорт", "Sport", "Спорт", "sport", 4, "https://images.unsplash.com/photo-1517649763962-0c623066013b");

        Category kids = rootCat("Bolalar", "Болалар", "Kids", "Дети", "kids", 5, "https://images.unsplash.com/photo-1601758123927-1964c74c1a34");

        Category beauty = rootCat("Go‘zallik", "Гўзаллик", "Beauty", "Красота", "beauty", 6, "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9");

        categoryRepository.saveAll(List.of(electronics, clothes, home, sport, kids, beauty));

        // ========== LEVEL 2 ==========
        Category phones = childCat(electronics, "Telefonlar", "Телефонлар", "Phones", "Телефоны", "phones", 10, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9");

        Category laptops = childCat(electronics, "Noutbuklar", "Ноутбуклар", "Laptops", "Ноутбуки", "laptops", 11, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8");

        Category men = childCat(clothes, "Erkaklar", "Эркаклар", "Men", "Мужское", "men", 12, "https://images.unsplash.com/photo-1520975916090-3105956dac38");

        Category women = childCat(clothes, "Ayollar", "Аёллар", "Women", "Женское", "women", 13, "https://images.unsplash.com/photo-1483985988355-763728e1935b");

        categoryRepository.saveAll(List.of(phones, laptops, men, women));

        // ========== LEVEL 3 ==========
        Category android = childCat(phones, "Android", "Андроид", "Android", "Андроид", "android", 20, "https://images.unsplash.com/photo-1585060544812-6b45742d762f");

        Category iphone = childCat(phones, "iPhone", "Айфон", "iPhone", "Айфон", "iphone", 21, "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5");

        Category gaming = childCat(laptops, "Gaming", "Гейминг", "Gaming", "Игровые", "gaming", 22, "https://images.unsplash.com/photo-1603302576837-37561b2e2302");

        categoryRepository.saveAll(List.of(android, iphone, gaming));

        // ========== LEVEL 4 (chuqur) ==========
        categoryRepository.saveAll(List.of(childCat(gaming, "RTX Laptoplar", "RTX ноутбуклар", "RTX Laptops", "RTX ноутбуки", "rtx", 30, "https://images.unsplash.com/photo-1611078489935-0cb964de46d6"),

                childCat(gaming, "Core i9", "Core i9", "Core i9", "Core i9", "core-i9", 31, "https://images.unsplash.com/photo-1593642632823-8f785ba67e45")));
    }

    private Category rootCat(String uz, String cyr, String en, String ru, String slug, int order, String image) {
        Category c = new Category();
        c.setNameUz(uz);
        c.setNameCyr(cyr);
        c.setNameEn(en);
        c.setNameRu(ru);
        c.setSlug(slug);
        c.setOrderNumber(order);
        c.setImageUrl(image);
        c.setActive(true);
        c.setDescriptionUz(uz + " kategoriyasidagi mahsulotlar");
        c.setDescriptionCyr(cyr + " категориясидаги махсулотлар");
        c.setDescriptionRu("Товары категории " + ru);
        c.setDescriptionEn("Products from " + en + " category");
        return c;
    }

    private Product product(String uz, String ru, String en, String cyr,
                            String sku, Category category, Shop shop) {
        Product p = new Product();
        p.setNameUz(uz);
        p.setNameRu(ru);
        p.setNameEn(en);
        p.setNameCyr(cyr);
        p.setSku(sku);
        p.setStatus(ProductStatus.OPEN);
        p.setCategory(category);
        p.setShop(shop);
        p.setDescriptionUz(uz + " haqida to‘liq ma’lumot");
        p.setDescriptionCyr(cyr + " ҳақида тўлиқ маълумот");
        p.setDescriptionRu("Подробное описание " + ru);
        p.setDescriptionEn("Full description of " + en);
        return p;
    }

    private Category childCat(Category parent, String uz, String cyr, String en, String ru, String slug, int order, String image) {
        Category c = rootCat(uz, cyr, en, ru, slug, order, image);
        c.setParent(parent);
        return c;
    }


}
*/
package uz.zafar.onlineshoptelegrambot.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.UserRole;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.repositories.DiscountRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SubscriptionPlanRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DataInitializer(SubscriptionPlanRepository subscriptionPlanRepository, CategoryRepository categoryRepository, ProductTypeRepository productTypeRepository, ProductTypeImageRepository productTypeImageRepository, ShopRepository shopRepository, ProductRepository productRepository, DiscountRepository discountRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.categoryRepository = categoryRepository;
        this.productTypeRepository = productTypeRepository;
        this.productTypeImageRepository = productTypeImageRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
//        initAdmins();
        initSuperAdmin();
        initPlans();
//        initShop();
//        initCategories();
//        initProducts();
    }

    private void initAdmins() {
        if (userRepository.admins().isEmpty()) {
            User user = new User();
            user.setFullName("Ziyatov Zafar");
            user.setBirthdate(LocalDate.of(2003, 10, 4));
            user.setRole(UserRole.ADMIN);
            user.setPassword(passwordEncoder.encode("12345"));
            user.setUsername("admin");
            user.setEmail("salom@gmail.com");
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private void initSuperAdmin() {
        if (userRepository.superadmins().isEmpty()) {
            User user = new User();
            user.setFullName("Ziyatov Zafar");
            user.setBirthdate(LocalDate.of(2003, 10, 4));
            user.setRole(UserRole.SUPER_ADMIN);
            user.setPassword(passwordEncoder.encode("12345"));
            user.setUsername("superadmin");
            user.setEmail("xayr@gmail.com");
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private void initShop() {
        if (shopRepository.findAll().isEmpty()) {
            Shop shop = new Shop();
            shop.setNameUz("TechShop");
            shop.setNameRu("ТехШоп"); // Ruscha to'g'ri
            shop.setNameEn("TechShop");
            shop.setNameCyr("ТехШоп");
            shop.setActive(true);
            shop.setCreatedAt(LocalDateTime.now());
            shop.setUpdatedAt(LocalDateTime.now());
            shopRepository.save(shop);
        }
    }

    private void initPlans() {
        if (subscriptionPlanRepository.findAll().isEmpty()) {
            String[] prices = {"200000", "140000", "150000", "200000", "140000", "150000", "200000", "140000", "150000"};
            int i = 0;
            for (SubscriptionPlanType value : SubscriptionPlanType.values()) {
                SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                subscriptionPlan.setName(value);
                subscriptionPlan.setDiscount(false);
                subscriptionPlan.setPrice((value == SubscriptionPlanType.EXPIRED || value == SubscriptionPlanType.TRIAL) ? BigDecimal.ZERO : new BigDecimal(prices[i]));
                subscriptionPlan.setCreatedAt(LocalDateTime.now());
                subscriptionPlan.setUpdatedAt(LocalDateTime.now());
                subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
                i++;
            }
        }
    }

    private void initProducts() {
        if (!productRepository.findAll().isEmpty()) return;
        Shop shop = shopRepository.findAll().get(0);
        Category android = categoryRepository.findBySlug("android").orElseThrow();
        Category iphone = categoryRepository.findBySlug("iphone").orElseThrow();
        Category gaming = categoryRepository.findBySlug("gaming").orElseThrow();
        Category men = categoryRepository.findBySlug("men").orElseThrow();
        Category women = categoryRepository.findBySlug("women").orElseThrow();

        List<Product> products = new ArrayList<>();
        List<ProductType> productTypes = new ArrayList<>();
        List<ProductTypeImage> images = new ArrayList<>();

        // 1. Samsung Galaxy S23
        Product samsung = product(
                "Samsung Galaxy S23",
                "Samsung Galaxy S23", // Ruscha nom inglizcha bo'lishi mumkin, chunki bu brend nomi
                "Samsung Galaxy S23",
                "Samsung Galaxy S23",
                "SKU-S23-001",
                android,
                shop
        );
        products.add(samsung);

        ProductType s23_128 = productType(samsung, "128GB Qora", "128GB Қора", "128GB Black", "128GB Чёрный", new BigDecimal("9500000"), 15);
        ProductType s23_256 = productType(samsung, "256GB Kumush", "256GB Кумуш", "256GB Silver", "256GB Серебро", new BigDecimal("10200000"), 10);
        ProductType s23_512 = productType(samsung, "512GB Yashil", "512GB Яшил", "512GB Green", "512GB Зелёный", new BigDecimal("11500000"), 8);
        productTypes.addAll(List.of(s23_128, s23_256, s23_512));

        images.addAll(List.of(
                image(s23_128, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf", true),
                image(s23_128, "https://images.unsplash.com/photo-1580910051074-7c9a7c1fdf33", false),
                image(s23_256, "https://images.unsplash.com/photo-1610945254755-f06da8b9c87b", true),
                image(s23_256, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf", false),
                image(s23_512, "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd", true),
                image(s23_512, "https://images.unsplash.com/photo-1598327105854-c8674faddf74", false)
        ));

        // 2. iPhone 15 Pro
        Product iphone15 = product(
                "iPhone 15 Pro",
                "iPhone 15 Pro", // Brend nomi
                "iPhone 15 Pro",
                "iPhone 15 Pro",
                "SKU-IP15-001",
                iphone,
                shop
        );
        products.add(iphone15);

        ProductType ip15_128 = productType(iphone15, "128GB Titanium", "128GB Титан", "128GB Titanium", "128GB Титан", new BigDecimal("14500000"), 12);
        ProductType ip15_256 = productType(iphone15, "256GB Titanium", "256GB Титан", "256GB Titanium", "256GB Титан", new BigDecimal("15500000"), 9);
        ProductType ip15_512 = productType(iphone15, "512GB Titanium", "512GB Титан", "512GB Titanium", "512GB Титан", new BigDecimal("17500000"), 6);
        productTypes.addAll(List.of(ip15_128, ip15_256, ip15_512));

        images.addAll(List.of(
                image(ip15_128, "https://images.unsplash.com/photo-1695048133142-1a20484d2569", true),
                image(ip15_128, "https://images.unsplash.com/photo-1695048133146-1a20484d2569", false),
                image(ip15_256, "https://images.unsplash.com/photo-1695048133142-1a20484d2569", true),
                image(ip15_256, "https://images.unsplash.com/photo-1695048133156-1a20484d2569", false),
                image(ip15_512, "https://images.unsplash.com/photo-1695048133142-1a20484d2569", true),
                image(ip15_512, "https://images.unsplash.com/photo-1695048133148-1a20484d2569", false)
        ));

        // 3. ASUS ROG Strix Gaming Laptop
        Product asusRog = product(
                "ASUS ROG Strix G16",
                "ASUS ROG Strix G16",
                "ASUS ROG Strix G16",
                "ASUS ROG Strix G16",
                "SKU-ASUS-001",
                gaming,
                shop
        );
        products.add(asusRog);

        ProductType asus_i7 = productType(asusRog, "Core i7/RTX 4060", "Core i7/RTX 4060", "Core i7/RTX 4060", "Core i7/RTX 4060", new BigDecimal("18500000"), 7);
        ProductType asus_i9 = productType(asusRog, "Core i9/RTX 4070", "Core i9/RTX 4070", "Core i9/RTX 4070", "Core i9/RTX 4070", new BigDecimal("22500000"), 5);
        productTypes.addAll(List.of(asus_i7, asus_i9));

        images.addAll(List.of(
                image(asus_i7, "https://images.unsplash.com/photo-1603302576837-37561b2e2302", true),
                image(asus_i7, "https://images.unsplash.com/photo-1547082299-de196ea013d6", false),
                image(asus_i9, "https://images.unsplash.com/photo-1593640408182-31c70c8268f5", true),
                image(asus_i9, "https://images.unsplash.com/photo-1542751371-adc38448a05e", false)
        ));

        // 4. Nike Air Force 1
        Product nikeAF1 = product(
                "Nike Air Force 1",
                "Nike Air Force 1",
                "Nike Air Force 1",
                "Nike Air Force 1",
                "SKU-NIKE-001",
                men,
                shop
        );
        products.add(nikeAF1);

        ProductType nike_40 = productType(nikeAF1, "40 o'lcham Oq", "40 ўлчам Оқ", "Size 40 White", "Размер 40 Белый", new BigDecimal("1500000"), 20);
        ProductType nike_41 = productType(nikeAF1, "41 o'lcham Oq", "41 ўлчам Оқ", "Size 41 White", "Размер 41 Белый", new BigDecimal("1500000"), 18);
        ProductType nike_42 = productType(nikeAF1, "42 o'lcham Oq", "42 ўлчам Оқ", "Size 42 White", "Размер 42 Белый", new BigDecimal("1500000"), 15);
        ProductType nike_43 = productType(nikeAF1, "43 o'lcham Oq", "43 ўлчам Оқ", "Size 43 White", "Размер 43 Белый", new BigDecimal("1500000"), 12);
        productTypes.addAll(List.of(nike_40, nike_41, nike_42, nike_43));

        images.addAll(List.of(
                image(nike_40, "https://images.unsplash.com/photo-1549298916-b41d501d3772", true),
                image(nike_40, "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa", false),
                image(nike_41, "https://images.unsplash.com/photo-1549298916-b41d501d3772", true),
                image(nike_41, "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519", false)
        ));

        // 5. Zara Classic Coat
        Product zaraCoat = product(
                "Zara Classic Coat",
                "Zara Classic Coat",
                "Zara Classic Coat",
                "Zara Classic Coat",
                "SKU-ZARA-001",
                women,
                shop
        );
        products.add(zaraCoat);

        ProductType coat_s = productType(zaraCoat, "S o'lcham Qora", "S ўлчам Қора", "Size S Black", "Размер S Чёрный", new BigDecimal("850000"), 25);
        ProductType coat_m = productType(zaraCoat, "M o'lcham Qora", "M ўлчам Қора", "Size M Black", "Размер M Чёрный", new BigDecimal("850000"), 22);
        ProductType coat_l = productType(zaraCoat, "L o'lcham Qora", "L ўлчам Қора", "Size L Black", "Размер L Чёрный", new BigDecimal("850000"), 20);
        productTypes.addAll(List.of(coat_s, coat_m, coat_l));

        images.addAll(List.of(
                image(coat_s, "https://images.unsplash.com/photo-1595777457583-95e059d581b8", true),
                image(coat_s, "https://images.unsplash.com/photo-1490481651871-ab68de25d43d", false),
                image(coat_m, "https://images.unsplash.com/photo-1591047139829-d91aecb6caea", true),
                image(coat_m, "https://images.unsplash.com/photo-1539109136881-3be0616acf4b", false)
        ));

        // 6. Xiaomi Redmi Note 13
        Product xiaomi = product(
                "Xiaomi Redmi Note 13",
                "Xiaomi Redmi Note 13",
                "Xiaomi Redmi Note 13",
                "Xiaomi Redmi Note 13",
                "SKU-XIAOMI-001",
                android,
                shop
        );
        products.add(xiaomi);

        ProductType xiaomi_128 = productType(xiaomi, "128GB Ko'k", "128GB Кўк", "128GB Blue", "128GB Синий", new BigDecimal("4500000"), 30);
        ProductType xiaomi_256 = productType(xiaomi, "256GB Oq", "256GB Оқ", "256GB White", "256GB Белый", new BigDecimal("5200000"), 25);
        productTypes.addAll(List.of(xiaomi_128, xiaomi_256));

        images.addAll(List.of(
                image(xiaomi_128, "https://images.unsplash.com/photo-1598327105666-5b89351aff97", true),
                image(xiaomi_128, "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd", false),
                image(xiaomi_256, "https://images.unsplash.com/photo-1598327105854-c8674faddf74", true),
                image(xiaomi_256, "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd", false)
        ));

        // 7. MacBook Pro 16 M2
        Product macbook = product(
                "MacBook Pro 16 M2",
                "MacBook Pro 16 M2",
                "MacBook Pro 16 M2",
                "MacBook Pro 16 M2",
                "SKU-MBP-001",
                gaming,
                shop
        );
        products.add(macbook);

        ProductType mbp_512 = productType(macbook, "512GB Space Gray", "512GB Space Gray", "512GB Space Gray", "512GB Space Gray", new BigDecimal("27500000"), 8);
        ProductType mbp_1tb = productType(macbook, "1TB Space Gray", "1TB Space Gray", "1TB Space Gray", "1TB Space Gray", new BigDecimal("31500000"), 6);
        productTypes.addAll(List.of(mbp_512, mbp_1tb));

        images.addAll(List.of(
                image(mbp_512, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8", true),
                image(mbp_512, "https://images.unsplash.com/photo-1541807084-5c52b6b3adef", false),
                image(mbp_1tb, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853", true),
                image(mbp_1tb, "https://images.unsplash.com/photo-1518709268805-4e9042af2176", false)
        ));

        // 8. Adidas Ultraboost
        Product adidas = product(
                "Adidas Ultraboost 22",
                "Adidas Ultraboost 22",
                "Adidas Ultraboost 22",
                "Adidas Ultraboost 22",
                "SKU-ADIDAS-001",
                men,
                shop
        );
        products.add(adidas);

        ProductType adidas_42 = productType(adidas, "42 o'lcham Qora", "42 ўлчам Қора", "Size 42 Black", "Размер 42 Чёрный", new BigDecimal("2200000"), 15);
        ProductType adidas_43 = productType(adidas, "43 o'lcham Qora", "43 ўлчам Қора", "Size 43 Black", "Размер 43 Чёрный", new BigDecimal("2200000"), 12);
        ProductType adidas_44 = productType(adidas, "44 o'lcham Qora", "44 ўлчам Қора", "Size 44 Black", "Размер 44 Чёрный", new BigDecimal("2200000"), 10);
        productTypes.addAll(List.of(adidas_42, adidas_43, adidas_44));

        images.addAll(List.of(
                image(adidas_42, "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519", true),
                image(adidas_42, "https://images.unsplash.com/photo-1542291026-7eec264c27ff", false),
                image(adidas_43, "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa", true),
                image(adidas_43, "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a", false)
        ));

        // 9. H&M Summer Dress
        Product hmdress = product(
                "H&M Summer Dress",
                "H&M Summer Dress",
                "H&M Summer Dress",
                "H&M Summer Dress",
                "SKU-HM-001",
                women,
                shop
        );
        products.add(hmdress);

        ProductType dress_s = productType(hmdress, "S o'lcham Pushti", "S ўлчам Пушти", "Size S Pink", "Размер S Розовый", new BigDecimal("650000"), 30);
        ProductType dress_m = productType(hmdress, "M o'lcham Pushti", "M ўлчам Пушти", "Size M Pink", "Размер M Розовый", new BigDecimal("650000"), 28);
        ProductType dress_l = productType(hmdress, "L o'lcham Pushti", "L ўлчам Пушти", "Size L Pink", "Размер L Розовый", new BigDecimal("650000"), 25);
        productTypes.addAll(List.of(dress_s, dress_m, dress_l));

        images.addAll(List.of(
                image(dress_s, "https://images.unsplash.com/photo-1490481651871-ab68de25d43d", true),
                image(dress_s, "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1", false),
                image(dress_m, "https://images.unsplash.com/photo-1595777457583-95e059d581b8", true),
                image(dress_m, "https://images.unsplash.com/photo-1525507119028-ed4c629a60a3", false)
        ));

        // 10. Samsung Galaxy Tab S9
        Product tablet = product(
                "Samsung Galaxy Tab S9",
                "Samsung Galaxy Tab S9",
                "Samsung Galaxy Tab S9",
                "Samsung Galaxy Tab S9",
                "SKU-TAB-001",
                android,
                shop
        );
        products.add(tablet);

        ProductType tab_128 = productType(tablet, "128GB Graphite", "128GB Graphite", "128GB Graphite", "128GB Graphite", new BigDecimal("12500000"), 12);
        ProductType tab_256 = productType(tablet, "256GB Graphite", "256GB Graphite", "256GB Graphite", "256GB Graphite", new BigDecimal("14500000"), 9);
        productTypes.addAll(List.of(tab_128, tab_256));

        images.addAll(List.of(
                image(tab_128, "https://images.unsplash.com/photo-1561154464-82e9adf32764", true),
                image(tab_128, "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0", false),
                image(tab_256, "https://images.unsplash.com/photo-1569779213435-ba3167dde7cc", true),
                image(tab_256, "https://images.unsplash.com/photo-1542751110-97427bbecf20", false)
        ));

        // Save all data
        productRepository.saveAll(products);
        productTypeRepository.saveAll(productTypes);
        productTypeImageRepository.saveAll(images);

        System.out.println("Initialized: " + products.size() + " products, " + productTypes.size() + " product types, " + images.size() + " images");
    }

    private ProductType productType(Product product, String uz, String cyr, String en, String ru,
                                    BigDecimal price, int stock) {
        ProductType pt = new ProductType();
        pt.setProduct(product);
        pt.setNameUz(uz);
        pt.setNameCyr(cyr);
        pt.setNameEn(en);
        pt.setNameRu(ru);
        pt.setPrice(price);
        pt.setStock(stock);
        pt.setDeleted(false);
        pt.setCreatedAt(LocalDateTime.now());
        pt.setUpdatedAt(LocalDateTime.now());
        return pt;
    }

    private ProductTypeImage image(ProductType pt, String url, boolean main) {
        ProductTypeImage img = new ProductTypeImage();
        img.setProductType(pt);
        img.setImageUrl(url);
        img.setImgName("image-" + System.nanoTime());
        img.setImgSize(500_000L);
        img.setMain(main);
        img.setDeleted(false);
        img.setCreatedAt(LocalDateTime.now());
        img.setUpdatedAt(LocalDateTime.now());
        return img;
    }

    public void initCategories() {
        if (!categoryRepository.findAll().isEmpty()) {
            return;
        }

        // ========== ROOT (6 ta) ==========
        Category electronics = rootCat("Elektronika", "Электроника", "Electronics", "Электроника", "electronics", 1, "https://images.unsplash.com/photo-1518770660439-4636190af475");

        Category clothes = rootCat(
                "Kiyim-kechak",
                "Кийим-кечак",
                "Clothes",
                "Одежда",
                "clothes",
                2,
                "https://images.unsplash.com/photo-1520975916090-3105956dac38"
        );

        Category home = rootCat("Uy-ro'zg'or", "Уй-рўзғор", "Home", "Дом", "home", 3, "https://images.unsplash.com/photo-1505691938895-1758d7feb511");

        Category sport = rootCat("Sport", "Спорт", "Sports", "Спорт", "sport", 4, "https://images.unsplash.com/photo-1517649763962-0c623066013b");

        Category kids = rootCat(
                "Bolalar",
                "Болалар",
                "Kids",
                "Дети",
                "kids",
                5,
                "https://images.unsplash.com/photo-1516627145497-ae6968895b74"
        );

        Category beauty = rootCat("Go'zallik", "Гўзаллик", "Beauty", "Красота", "beauty", 6, "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9");

        categoryRepository.saveAll(List.of(electronics, clothes, home, sport, kids, beauty));

        // ========== LEVEL 2 ==========
        Category phones = childCat(electronics, "Telefonlar", "Телефонлар", "Phones", "Телефоны", "phones", 10, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9");

        Category laptops = childCat(electronics, "Noutbuklar", "Ноутбуклар", "Laptops", "Ноутбуки", "laptops", 11, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8");

        Category men = childCat(clothes, "Erkaklar", "Эркаклар", "Men", "Мужское", "men", 12, "https://images.unsplash.com/photo-1520975916090-3105956dac38");

        Category women = childCat(clothes, "Ayollar", "Аёллар", "Women", "Женское", "women", 13, "https://images.unsplash.com/photo-1483985988355-763728e1935b");

        categoryRepository.saveAll(List.of(phones, laptops, men, women));

        // ========== LEVEL 3 ==========
        Category android = childCat(phones, "Android", "Андроид", "Android", "Андроид", "android", 20, "https://images.unsplash.com/photo-1585060544812-6b45742d762f");

        Category iphone = childCat(phones, "iPhone", "Айфон", "iPhone", "Айфон", "iphone", 21, "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5");

        Category gaming = childCat(laptops, "Gaming", "Гейминг", "Gaming", "Игровые", "gaming", 22, "https://images.unsplash.com/photo-1603302576837-37561b2e2302");

        categoryRepository.saveAll(List.of(android, iphone, gaming));

        // ========== LEVEL 4 (chuqur) ==========
        categoryRepository.saveAll(List.of(
                childCat(gaming, "RTX Laptoplar", "RTX ноутбуклар", "RTX Laptops", "RTX ноутбуки", "rtx", 30, "https://images.unsplash.com/photo-1611078489935-0cb964de46d6"),
                childCat(gaming, "Core i9", "Core i9", "Core i9", "Core i9", "core-i9", 31, "https://images.unsplash.com/photo-1593642632823-8f785ba67e45")
        ));
    }

    private Category rootCat(String uz, String cyr, String en, String ru, String slug, int order, String image) {
        Category c = new Category();
        c.setNameUz(uz);
        c.setNameCyr(cyr);
        c.setNameEn(en);
        c.setNameRu(ru);
        c.setSlug(slug);
        c.setOrderNumber(order);
        c.setImageUrl(image);
        c.setActive(true);
        c.setDescriptionUz(uz + " kategoriyasidagi mahsulotlar");
        c.setDescriptionCyr(cyr + " категориясидаги махсулотлар");
        c.setDescriptionRu("Товары категории " + ru);
        c.setDescriptionEn("Products from " + en + " category");
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }

    private Product product(String uz, String ru, String en, String cyr,
                            String sku, Category category, Shop shop) {
        Product p = new Product();
        p.setNameUz(uz);
        p.setNameRu(ru);
        p.setNameEn(en);
        p.setNameCyr(cyr);
        p.setSku(sku);
        p.setStatus(ProductStatus.OPEN);
        p.setCategory(category);
        p.setShop(shop);
        p.setDescriptionUz(uz + " haqida to'liq ma'lumot");
        p.setDescriptionCyr(cyr + " ҳақида тўлиқ маълумот");
        p.setDescriptionRu("Подробное описание " + ru);
        p.setDescriptionEn("Full description of " + en);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return p;
    }

    private Category childCat(Category parent, String uz, String cyr, String en, String ru, String slug, int order, String image) {
        Category c = rootCat(uz, cyr, en, ru, slug, order, image);
        c.setParent(parent);
        return c;
    }
}