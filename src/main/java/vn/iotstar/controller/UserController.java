package vn.iotstar.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                            Model model) {
        Page<UserDTO> usersPage = userService.findAll(keyword, page, size);
        model.addAttribute("users", usersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("size", size);
        return "users/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        UserDTO dto = new UserDTO();
        dto.setEnabled(true);
        dto.setRoleName("ROLE_USER");
        model.addAttribute("userDTO", dto);
        model.addAttribute("mode", "create");
        return "users/form";
    }

    @PostMapping("/create")
    public String handleCreate(@Valid @ModelAttribute("userDTO") UserDTO dto,
                               BindingResult result,
                               RedirectAttributes redirect,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mode", "create");
            return "users/form";
        }
        try {
            userService.create(dto);
            redirect.addFlashAttribute("success", "Tạo tài khoản thành công!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("mode", "create");
            return "users/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        UserDTO dto = userService.findById(id);
        model.addAttribute("userDTO", dto);
        model.addAttribute("mode", "edit");
        return "users/form";
    }

    @PostMapping("/edit/{id}")
    public String handleUpdate(@PathVariable("id") Long id,
                               @ModelAttribute("userDTO") UserDTO dto,
                               RedirectAttributes redirect,
                               Model model) {
        try {
            userService.update(id, dto);
            redirect.addFlashAttribute("success", "Cập nhật tài khoản #" + id + " thành công!");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("mode", "edit");
            return "users/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            userService.delete(id);
            redirect.addFlashAttribute("success", "Đã xóa người dùng #" + id + "!");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
