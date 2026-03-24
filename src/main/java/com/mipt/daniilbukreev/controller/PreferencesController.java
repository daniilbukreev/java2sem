package com.mipt.daniilbukreev.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

    @Value("${app.version}")
    private String apiVersion;

    private static final String VIEW_PREFERENCE_COOKIE = "viewPreference";

    @GetMapping("/view")
    public ResponseEntity<String> getViewPreference(@CookieValue(name = VIEW_PREFERENCE_COOKIE, defaultValue = "detailed") String viewPreference) {
        return ResponseEntity.ok().header("X-API-Version", apiVersion).body(viewPreference);
    }

    @PostMapping("/view")
    public ResponseEntity<Void> setViewPreference(@RequestParam String mode, HttpServletResponse response) {
        if (!Objects.equals(mode, "compact") && !Objects.equals(mode, "detailed")) {
            return ResponseEntity.badRequest().header("X-API-Version", apiVersion).build();
        }
        Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE, mode);
        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24 * 365);
        response.addCookie(cookie);
        return ResponseEntity.ok().header("X-API-Version", apiVersion).build();
    }
}
