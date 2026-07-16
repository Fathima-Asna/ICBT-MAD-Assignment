package com.printxpress.backend.config;

import com.printxpress.backend.model.Product;
import com.printxpress.backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Populates a handful of sample products on startup so the app has something to display.
 * Only runs when app.seed-data=true (see application.properties), so it never touches
 * a production Firestore database by accident.
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedProducts(ProductService productService,
                                           @Value("${app.seed-data:false}") boolean seedData) {
        return args -> {
            if (!seedData) {
                return;
            }
            if (!productService.findAll().isEmpty()) {
                log.info("Products collection already has data, skipping seed.");
                return;
            }
            log.info("Seeding sample products into Firestore...");
            productService.save(Product.builder()
                    .category("Business Cards")
                    .name("Standard Business Cards")
                    .basePrice(15.99)
                    .specs("3.5\" x 2\", 300gsm matte, box of 250")
                    .type("card")
                    .color("full-color")
                    .weight("300gsm")
                    .build());
            productService.save(Product.builder()
                    .category("Flyers")
                    .name("A5 Flyers")
                    .basePrice(29.99)
                    .specs("A5, 150gsm gloss, pack of 100")
                    .type("flyer")
                    .color("full-color")
                    .weight("150gsm")
                    .build());
            productService.save(Product.builder()
                    .category("Posters")
                    .name("A2 Poster")
                    .basePrice(9.99)
                    .specs("A2, 200gsm satin")
                    .type("poster")
                    .color("full-color")
                    .weight("200gsm")
                    .build());
            productService.save(Product.builder()
                    .category("Banners")
                    .name("Vinyl Banner 3x6ft")
                    .basePrice(49.99)
                    .specs("3ft x 6ft, weatherproof vinyl, grommets included")
                    .type("banner")
                    .color("full-color")
                    .weight("13oz vinyl")
                    .build());
            log.info("Seeding complete.");
        };
    }
}
