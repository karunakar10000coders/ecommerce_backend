package com.ecommerce.config;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@ecommerce.com").isEmpty()) {
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            admin = userRepository.save(admin);
            cartRepository.save(Cart.builder().user(admin).build());
        }

        ensureCategories();

        if (productRepository.count() == 0) {
            Category electronics = categoryRepository.findByName("Electronics").orElseThrow();
            Category clothing = categoryRepository.findByName("Clothing").orElseThrow();
            Category books = categoryRepository.findByName("Books").orElseThrow();
            Category shoes = categoryRepository.findByName("Shoes").orElseThrow();
            Category furniture = categoryRepository.findByName("Furniture").orElseThrow();

            // Electronics (7)
            saveProduct("Wireless Headphones", "High-quality wireless headphones with noise cancellation",
                    "2999.00", 50, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400", electronics);
            saveProduct("Smart Watch", "Fitness tracking smart watch with heart rate monitor",
                    "4999.00", 30, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400", electronics);
            saveProduct("Bluetooth Speaker", "Portable waterproof speaker with 12-hour battery life",
                    "2499.00", 45, "https://images.unsplash.com/photo-1608043152359-9429f8b60b5e?w=400", electronics);
            saveProduct("Wireless Mouse", "Ergonomic wireless mouse with precision tracking",
                    "1299.00", 80, "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400", electronics);
            saveProduct("USB-C Hub", "7-in-1 USB-C hub with HDMI, USB 3.0 and SD card reader",
                    "1899.00", 60, "https://images.unsplash.com/photo-1625948515291-69613efd103f?w=400", electronics);
            saveProduct("Portable Power Bank", "20000mAh fast-charging power bank with dual USB ports",
                    "1599.00", 70, "https://images.unsplash.com/photo-1609091839311-9d67040e2d31?w=400", electronics);
            saveProduct("4K Webcam", "Full HD webcam with built-in microphone for video calls",
                    "3499.00", 35, "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400", electronics);

            // Clothing (4)
            saveProduct("Cotton T-Shirt", "Comfortable cotton t-shirt available in multiple colors",
                    "599.00", 100, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400", clothing);
            saveProduct("Denim Jeans", "Classic fit denim jeans with stretch comfort",
                    "1499.00", 75, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400", clothing);
            saveProduct("Hooded Sweatshirt", "Warm fleece hoodie perfect for casual wear",
                    "1299.00", 60, "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400", clothing);
            saveProduct("Summer Dress", "Lightweight floral summer dress with breathable fabric",
                    "1799.00", 40, "https://images.unsplash.com/photo-1595777457583-95e059d581b2?w=400", clothing);

            // Books (4)
            saveProduct("Java Programming Book", "Complete guide to Java programming from basics to advanced",
                    "899.00", 75, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", books);
            saveProduct("React Development Guide", "Modern React.js development with hooks and best practices",
                    "799.00", 55, "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", books);
            saveProduct("Data Structures & Algorithms", "Essential DSA concepts for coding interviews",
                    "999.00", 65, "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400", books);
            saveProduct("Fiction Novel Collection", "Bestselling fiction novels bundle of 3 books",
                    "649.00", 90, "https://images.unsplash.com/photo-1519682337058-a94d519337bc?w=400", books);

            // Shoes (5)
            saveProduct("Running Sneakers", "Lightweight running shoes with cushioned sole",
                    "2999.00", 50, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", shoes);
            saveProduct("Leather Formal Shoes", "Premium leather formal shoes for office and events",
                    "3999.00", 35, "https://images.unsplash.com/photo-1614252239476-1cfb49c73d50?w=400", shoes);
            saveProduct("Casual Slip-Ons", "Comfortable slip-on shoes for everyday wear",
                    "1999.00", 60, "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=400", shoes);
            saveProduct("Sports Sandals", "Durable sports sandals with adjustable straps",
                    "1499.00", 45, "https://images.unsplash.com/photo-1603487742391-ab9726f33290?w=400", shoes);
            saveProduct("Hiking Boots", "Waterproof hiking boots with ankle support",
                    "4499.00", 25, "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=400", shoes);

            // Furniture (5)
            saveProduct("Ergonomic Office Chair", "Adjustable office chair with lumbar support",
                    "8999.00", 20, "https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400", furniture);
            saveProduct("Study Desk", "Minimalist wooden study desk with drawer storage",
                    "5999.00", 15, "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400", furniture);
            saveProduct("Bookshelf", "5-tier open bookshelf for home or office",
                    "3499.00", 30, "https://images.unsplash.com/photo-1594620302200-9a762244a156?w=400", furniture);
            saveProduct("Queen Size Bed Frame", "Sturdy metal bed frame with headboard",
                    "12999.00", 10, "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=400", furniture);
            saveProduct("Coffee Table", "Modern glass-top coffee table for living room",
                    "4499.00", 18, "https://images.unsplash.com/photo-1532372320572-cda25653a26d?w=400", furniture);
        }
    }

    private void ensureCategories() {
        getOrCreateCategory("Electronics", "Electronic devices and gadgets");
        getOrCreateCategory("Clothing", "Fashion and apparel");
        getOrCreateCategory("Books", "Books and literature");
        getOrCreateCategory("Shoes", "Footwear for every occasion");
        getOrCreateCategory("Furniture", "Home and office furniture");
    }

    private Category getOrCreateCategory(String name, String description) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .description(description)
                        .build()));
    }

    private void saveProduct(String name, String description, String price,
                             int stock, String imageUrl, Category category) {
        productRepository.save(Product.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .stock(stock)
                .imageUrl(imageUrl)
                .category(category)
                .build());
    }
}
