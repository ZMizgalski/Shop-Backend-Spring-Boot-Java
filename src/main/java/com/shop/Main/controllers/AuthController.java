package com.shop.Main.controllers;

import com.shop.Main.models.ERoles;
import com.shop.Main.models.Roles;
import com.shop.Main.models.Users;
import com.shop.Main.payload.requests.LoginRequest;
import com.shop.Main.payload.requests.RegistrationRequest;
import com.shop.Main.payload.responses.JwtResponse;
import com.shop.Main.payload.responses.MessageResponse;
import com.shop.Main.repositories.RoleRepo;
import com.shop.Main.repositories.UserRepo;
import com.shop.Main.security.jwt.JwtUtils;
import com.shop.Main.security.servieces.UserDetailsImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles
                )
        );
    }

    @SneakyThrows
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {

        if (userRepo.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Username exists!"));

        }

        if (userRepo.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email exists!"));

        }

        Users users = new Users(
                registrationRequest.getUsername(),
                registrationRequest.getEmail(),
                passwordEncoder.encode(registrationRequest.getPassword())
        );

        Set<String> rolesSet = registrationRequest.getRoles();
        Set<Roles> roles = new HashSet<>();

        if(rolesSet == null) {
            Roles role = roleRepo.findByName(ERoles.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(role);
        } else {
            for (String role: rolesSet) {
                switch (role) {
                    case "admin":
                        // throw new Exception("Rejestracja kont admina została wyłączona");
                        Roles adRole = roleRepo.findByName(ERoles.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role not found"));
                        roles.add(adRole);
                    case "moderator":
                        // throw new Exception("Rejestracja kont admina została wyłączona");
                        Roles mdRole = roleRepo.findByName(ERoles.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Role not found"));
                        roles.add(mdRole);
                    default:
                        Roles usRole = roleRepo.findByName(ERoles.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role not found"));
                        roles.add(usRole);
                }
            }
        }
        users.setRoles(roles);
        userRepo.save(users);
        return ResponseEntity.ok(new MessageResponse("User Registered"));
    }
}
