package vn.iotstar.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.security.CustomUserDetails;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.UserService;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listProducts(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "size", required = false, defaultValue = "6") int size,
                               Model model) {
        Page<ProductDTO> productPage = productService.findAll(keyword, page, size);
        model.addAttribute("products", productPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("size", size);
        return "products/list";
    }

    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        ProductDTO dto = new ProductDTO();
        if (userDetails != null) {
            dto.setUserId(userDetails.getId());
        }
        model.addAttribute("productDTO", dto);
        model.addAttribute("mode", "create");
        model.addAttribute("usersList", userService.findAll("", 0, 100).getContent());
        return "products/form";
    }

    @PostMapping("/create")
    public String handleCreate(@Valid @ModelAttribute("productDTO") ProductDTO dto,
                               BindingResult result,
                               @RequestParam(value = "image", required = false) MultipartFile image,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirect,
                               Model model) {
        if (dto.getUserId() == null && userDetails != null) {
            dto.setUserId(userDetails.getId());
        }
        if (result.hasErrors()) {
            model.addAttribute("mode", "create");
            model.addAttribute("usersList", userService.findAll("", 0, 100).getContent());
            return "products/form";
        }
        try {
            productService.create(dto, image);
            redirect.addFlashAttribute("success", "Thêm sản phẩm thành công!");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("mode", "create");
            model.addAttribute("usersList", userService.findAll("", 0, 100).getContent());
            return "products/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        ProductDTO dto = productService.findById(id);
        model.addAttribute("productDTO", dto);
        model.addAttribute("mode", "edit");
        model.addAttribute("usersList", userService.findAll("", 0, 100).getContent());
        return "products/form";
    }

    @PostMapping("/edit/{id}")
    public String handleUpdate(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("productDTO") ProductDTO dto,
                               BindingResult result,
                               @RequestParam(value = "image", required = false) MultipartFile image,
                               RedirectAttributes redirect,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("usersList", userService.findAll("", 0, 100).getContent());
            return "products/form";
        }
        try {
            productService.update(id, dto, image);
            redirect.addFlashAttribute("success", "Cập nhật sản phẩm #" + id + " thành công!");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("mode", "edit");
            model.addAttribute("usersList", userService.findAll("", 0, 100).getContent());
            return "products/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            productService.delete(id);
            redirect.addFlashAttribute("success", "Đã xóa sản phẩm #" + id + "!");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
