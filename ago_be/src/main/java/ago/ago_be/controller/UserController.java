package ago.ago_be.controller;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.dto.ChangePasswordRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserResponseDto getUser(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.getUserInfo(principal.getUser().getId());
    }

    @PostMapping("/nickname")
    public UserResponseDto updateNickname(@RequestBody UserResponseDto userResponseDto) {
        return userService.changeNickname(userResponseDto);
    }

    @PostMapping("/password")
    public UserResponseDto updatePassword(@RequestBody ChangePasswordRequestDto requestDto, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.changePassword(principal.getUser().getId(), requestDto.getExPassword(), requestDto.getNewPassword());
    }
}
