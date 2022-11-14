package ago.ago_be.controller.v2;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.dto.ChangeNicknameRequestDto;
import ago.ago_be.dto.ChangePasswordRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserResponseDto getUser(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.getUserInfo(principal.getUser().getId());
    }

    @PostMapping("/nickname")
    public UserResponseDto updateNickname(@RequestBody ChangeNicknameRequestDto requestDto, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return userService.changeNickname(principalDetails.getUser().getId(), requestDto.getPassword(), requestDto.getNewNickname());
    }

    @PostMapping("/password")
    public UserResponseDto updatePassword(@RequestBody ChangePasswordRequestDto requestDto, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return userService.changePassword(principal.getUser().getId(), requestDto.getExPassword(), requestDto.getNewPassword());
    }
}
