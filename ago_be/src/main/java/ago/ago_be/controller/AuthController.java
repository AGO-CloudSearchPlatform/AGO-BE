package ago.ago_be.controller;

import ago.ago_be.dto.UserRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 중복 회원 예외 발생시 처리하는 컨트롤러 추가 필요!!
    @PostMapping("/join")
    public UserResponseDto create(@RequestBody UserRequestDto userRequestDto) {
        return authService.join(userRequestDto);
    }
}
