package ago.ago_be.service;

import ago.ago_be.domain.User;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponseDto getUserInfo(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("로그인 유저 정보가 없습니다.");
        }
        User user = optionalUser.get();
        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    public UserResponseDto changeNickname(UserResponseDto userResponseDto) {
        User user = userRepository.findByEmail(userResponseDto.getEmail());
        user.setNickname(userResponseDto.getNickname());
        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    public UserResponseDto changePassword(Long id, String exPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("로그인 유저 정보가 없습니다.");
        }
        User user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(exPassword, user.getPassword())) {
            throw new RuntimeException("비밀번호가 맞지 않습니다.");
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional
    public void updateRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email);
        user.setRefreshToken(refreshToken);
    }
}
