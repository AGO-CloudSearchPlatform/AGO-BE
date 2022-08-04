package ago.ago_be.service;

import ago.ago_be.domain.User;
import ago.ago_be.dto.UserRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserResponseDto join(UserRequestDto userRequestDto) {
        String encPassword = bCryptPasswordEncoder.encode(userRequestDto.getPassword());
        userRequestDto.setPassword(encPassword);
        User user = userRequestDto.toEntity();
        validateDuplicateUser(user); // 이메일로 중복 회원 검증
        userRepository.save(user);
        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());
        if (findUser != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
