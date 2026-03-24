package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "User Preferences", description = "User preference management using cookies")
public class PreferencesController {

    @Value("${app.version}")
    private String apiVersion;

    private static final String VIEW_PREFERENCE_COOKIE = "viewPreference";

    @Operation(summary = "Get the current view preference",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved view preference",
                            content = @Content(schema = @Schema(type = "string", example = "detailed"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/view")
    public ResponseEntity<String> getViewPreference(@CookieValue(name = VIEW_PREFERENCE_COOKIE, defaultValue = "detailed") String viewPreference) {
        return ResponseEntity.ok().header("X-API-Version", apiVersion).body(viewPreference);
    }

    @Operation(summary = "Set the view preference",
            responses = {
                    @ApiResponse(responseCode = "200", description = "View preference set successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid preference mode",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
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
