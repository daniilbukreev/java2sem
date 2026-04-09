package com.mipt.daniilbukreev.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "app.version=2.0.0")
@ActiveProfiles("test")
public class PreferencesControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetDefaultViewPreference() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/preferences/view", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("detailed");
    }

    @Test
    void testSetAndGetViewPreference() {
        ResponseEntity<Void> setResponse = restTemplate.postForEntity("/api/preferences/view?mode=compact", null, Void.class);
        assertThat(setResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String cookie = setResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(cookie).isNotNull().contains("viewPreference=compact");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> getResponse = restTemplate.exchange("/api/preferences/view", HttpMethod.GET, requestEntity, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isEqualTo("compact");
    }

    @Test
    void testSetInvalidPreferenceMode() {
        ResponseEntity<Void> response = restTemplate.postForEntity("/api/preferences/view?mode=invalid", null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
