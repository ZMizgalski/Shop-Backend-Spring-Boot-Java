package com.shop.Main.controllers;

import com.shop.Main.models.Token;
import com.shop.Main.models.Users;
import com.shop.Main.payload.requests.ForgotPasswordRequestModel;
import com.shop.Main.repositories.TokenRepo;
import com.shop.Main.repositories.UserRepo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/reset-password")
public class ForgotPasswordController {

    public Argon2PasswordEncoder passwordEncoder() {return new Argon2PasswordEncoder();}

    @Autowired
    UserRepo userRepo;

    @Autowired
    TokenRepo tokenRepo;

    @ModelAttribute("passwordResetForm")
    public ForgotPasswordRequestModel passwordRequestModel() { return new ForgotPasswordRequestModel(); }

    @GetMapping
    public String displayResetPassword(@RequestParam(required = false) String token, Model model) {

        Token resetToken = tokenRepo.findByToken(token);
        if (resetToken == null) {
            model.addAttribute("error", "Token wygasł lub nie został znaleziony, poproś o ponowne zresetowanie hasła.");
        } else if (resetToken.isExpired()) {
            model.addAttribute("error", "Token wygasł lub nie został znaleziony, poproś o ponowne zresetowanie hasła.");
        } else {
            model.addAttribute("token", resetToken.getToken());
        }

        return "ResetPasswordPage";
    }

    @PostMapping
    @Transactional
    public String handlePasswordReset(@ModelAttribute("passwordResetForm") @Valid ForgotPasswordRequestModel form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ".passwordResetForm", bindingResult);
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }


        Token token = tokenRepo.findByToken(form.getToken());
        Users userData = userRepo.findByEmail(token.getEmail());
        String updatedPassword = passwordEncoder().encode(form.getPassword());
        val id = userData.getId();
        userRepo.findById(id)
                .map(newUser -> {
                    newUser.setPassword(updatedPassword);
                    return userRepo.save(newUser);
                });
        tokenRepo.deleteByEmail(token.getEmail());
        return "redirect:/login";
    }
}
