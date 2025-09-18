package com.bankingcustomer.portal.controller;

import com.bankingcustomer.portal.entity.User;
import com.bankingcustomer.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DebugController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> result = new HashMap<>();
                result.put("username", user.getUsername());
                result.put("email", user.getEmail());
                result.put("role", user.getRole());
                result.put("enabled", user.isEnabled());
                result.put("accountNonExpired", user.isAccountNonExpired());
                result.put("accountNonLocked", user.isAccountNonLocked());
                result.put("credentialsNonExpired", user.isCredentialsNonExpired());
                result.put("passwordLength", user.getPassword().length());
                result.put("passwordStartsWith", user.getPassword().substring(0, Math.min(10, user.getPassword().length())));
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/password-check")
    public ResponseEntity<?> checkPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean matches = passwordEncoder.matches(password, user.getPassword());
                
                Map<String, Object> result = new HashMap<>();
                result.put("username", username);
                result.put("passwordMatches", matches);
                result.put("providedPassword", password);
                result.put("storedPasswordLength", user.getPassword().length());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/authorities/{username}")
    public ResponseEntity<?> getUserAuthorities(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> result = new HashMap<>();
                result.put("username", user.getUsername());
                result.put("role", user.getRole().name());
                result.put("authorities", user.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}