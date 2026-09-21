package vn.iotstar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      ProductRepository productRepository,
                                      PasswordEncoder encoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            User admin = userRepository.findByUsername("phucadmin").orElseGet(() -> {
                User u = new User();
                u.setUsername("phucadmin");
                u.setEmail("phuc@iotstar.vn");
                u.setFullName("Nguyễn Song Hoàng Phúc (Admin)");
                u.setPassword(encoder.encode("123456"));
                u.setImages("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150");
                u.setEnabled(true);
                u.setCreatedAt(LocalDateTime.now());
                u.setRole(adminRole);
                return userRepository.save(u);
            });

            User user = userRepository.findByUsername("vanb").orElseGet(() -> {
                User u = new User();
                u.setUsername("vanb");
                u.setEmail("vanb@iotstar.vn");
                u.setFullName("Trần Văn B");
                u.setPassword(encoder.encode("123456"));
                u.setImages("https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150");
                u.setEnabled(true);
                u.setCreatedAt(LocalDateTime.now());
                u.setRole(userRole);
                return userRepository.save(u);
            });

            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("iPhone 16 Pro Max 256GB Titan Sa Mạc");
                p1.setDescription("Chip A18 Pro, khung viền Titan cao cấp");
                p1.setPrice(34990000.0);
                p1.setImageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400");
                p1.setUser(admin);
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setName("MacBook Pro 16 inch M3 Max");
                p2.setDescription("RAM 36GB, SSD 1TB, màn hình Liquid Retina XDR");
                p2.setPrice(79990000.0);
                p2.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400");
                p2.setUser(admin);
                productRepository.save(p2);

                Product p3 = new Product();
                p3.setName("iPad Pro M4 13 inch OLED");
                p3.setDescription("Ultra Retina XDR, thiết kế siêu mỏng");
                p3.setPrice(37990000.0);
                p3.setImageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400");
                p3.setUser(user);
                productRepository.save(p3);

                Product p4 = new Product();
                p4.setName("Tai nghe AirPods Pro 2 USB-C");
                p4.setDescription("Chống ồn chủ động gấp 2 lần");
                p4.setPrice(5990000.0);
                p4.setImageUrl("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=400");
                p4.setUser(user);
                productRepository.save(p4);
            }
        };
    }
}
