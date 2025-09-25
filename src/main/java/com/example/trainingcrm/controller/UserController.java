package com.example.trainingcrm.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trainingcrm.dto.UserForm;
import com.example.trainingcrm.entity.User;
import com.example.trainingcrm.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private static final List<String> ROLES = List.of("ADMIN", "STAFF");

    private final UserService userService;

    @ModelAttribute("roles")
    public List<String> roles() {
        return ROLES;
    }

    @GetMapping
    public String list(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("isNew", true);
        return "users/form";
    }

    @PostMapping
    public String create(@Validated @ModelAttribute("userForm") UserForm userForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        validatePasswordForNew(userForm, bindingResult);
        validatePasswordLength(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            return "users/form";
        }
        try {
            userService.createUser(userForm);
        } catch (IllegalStateException ex) {
            bindingResult.rejectValue("username", "duplicate", ex.getMessage());
            model.addAttribute("isNew", true);
            return "users/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを作成しました。");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.getUser(id);
        UserForm form = new UserForm();
        form.setUserId(user.getUserId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setRole(user.getRole());
        form.setEnabled(user.getEnabled());
        model.addAttribute("userForm", form);
        model.addAttribute("isNew", false);
        return "users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Validated @ModelAttribute("userForm") UserForm userForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        validatePasswordLength(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            return "users/form";
        }
        try {
            userService.updateUser(id, userForm);
        } catch (IllegalStateException ex) {
            bindingResult.rejectValue("username", "duplicate", ex.getMessage());
            model.addAttribute("isNew", false);
            return "users/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを更新しました。");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを削除しました。");
        return "redirect:/admin/users";
    }

    private void validatePasswordForNew(UserForm userForm, BindingResult bindingResult) {
        if (userForm.getUserId() == null) {
            if (userForm.getPassword() == null || userForm.getPassword().isBlank()) {
                bindingResult.rejectValue("password", "NotBlank", "パスワードを入力してください。");
            }
        }
    }

    private void validatePasswordLength(UserForm userForm, BindingResult bindingResult) {
        if (StringUtils.hasText(userForm.getPassword()) && userForm.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "Size", "パスワードは6文字以上で入力してください。");
        }
    }
}
