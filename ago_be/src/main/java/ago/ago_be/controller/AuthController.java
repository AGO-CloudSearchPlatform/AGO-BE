package ago.ago_be.controller;

import ago.ago_be.domain.User;
import ago.ago_be.dto.UserRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.jwt.JwtProperties;
import ago.ago_be.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RestTemplate restTemplate;

    // 중복 회원 예외 발생시 처리하는 컨트롤러 추가 필요!!
    @PostMapping("/join")
    public UserResponseDto create(@RequestBody UserRequestDto userRequestDto) {
        User user = authService.join(userRequestDto);
//        String BaseURL = "http://ec2-43-200-174-150.ap-northeast-2.compute.amazonaws.com:8080";
//        String path = "/user" + user.getId();
//        URI uri = UriComponentsBuilder
//                .fromUriString(BaseURL)
//                .path(path)
//                .encode()
//                .build()
//                .toUri();
//        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, HttpEntity.EMPTY, String.class);
//        System.out.println("responseEntity.getStatusCode() = " + responseEntity.getStatusCode());
//        System.out.println("responseEntity.getBody() = " + responseEntity.getBody());

        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
        }
        String refreshToken = header.replace(JwtProperties.TOKEN_PREFIX, "");
        Map<String, String> tokens = authService.refresh(refreshToken);
        response.setHeader(JwtProperties.AT_HEADER, tokens.get(JwtProperties.AT_HEADER));
        response.setHeader(JwtProperties.RT_HEADER, tokens.get(JwtProperties.RT_HEADER));
        return ResponseEntity.ok(tokens);
    }
}
