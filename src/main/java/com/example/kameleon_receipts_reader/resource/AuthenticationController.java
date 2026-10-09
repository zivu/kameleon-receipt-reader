package com.example.kameleon_receipts_reader.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://angular-test-970a4.web.app", "http://localhost:4200", "https://zivu.github.io"}, allowCredentials = "true")
public class AuthenticationController {

    /**
     * This endpoint used by frontend to check whether user is authenticated.
     * @param authentication injected info.
     * @return short info whether user is authentication and what's his/her name.
     */
    @GetMapping("/auth/me")
    public Object checkUserAuthentication(Authentication authentication) {
        log.info("User is authenticated={}", null != authentication);
        return Map.of(
                "authenticated", authentication != null,
                "name", authentication != null ? authentication.getName() : ""
        );
    }

}
