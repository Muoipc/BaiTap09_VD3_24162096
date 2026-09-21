package vn.iotstar.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.ProductService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductMapper mapper;

    private final Path uploadDir = Paths.get("uploads");

    public ProductServiceImpl() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException ignored) {
        }
    }

    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String newFilename = "prod_" + UUID.randomUUID().toString() + (ext.isEmpty() ? ".jpg" : "." + ext);
            Path target = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu ảnh sản phẩm: " + e.getMessage());
        }
    }

    @Override
    public Page<ProductDTO> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = (keyword == null || keyword.trim().isEmpty())
                ? productRepository.findAll(pageable)
                : productRepository.search(keyword.trim(), pageable);

        return products.map(mapper::toDTO);
    }

    @Override
    public Page<ProductDTO> findByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productRepository.findByUserId(userId, pageable).map(mapper::toDTO);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product không tồn tại: " + id));
        return mapper.toDTO(p);
    }

    @Override
    public ProductDTO create(ProductDTO dto, MultipartFile image) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại: " + dto.getUserId()));

        Product product = mapper.toEntity(dto);
        product.setUser(user);

        String savedImage = saveImage(image);
        if (savedImage != null) {
            product.setImageUrl(savedImage);
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
            product.setImageUrl(dto.getImageUrl());
        } else {
            product.setImageUrl("https://placehold.co/300x300?text=Product");
        }

        return mapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product không tồn tại: " + id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại: " + dto.getUserId()));
            product.setUser(user);
        }

        String savedImage = saveImage(image);
        if (savedImage != null) {
            product.setImageUrl(savedImage);
        }

        return mapper.toDTO(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public long countProductsByUserId(Long userId) {
        return productRepository.countByUserId(userId);
    }
}
