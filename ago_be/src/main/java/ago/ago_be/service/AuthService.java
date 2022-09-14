package ago.ago_be.service;

import ago.ago_be.domain.User;
import ago.ago_be.dto.UserRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.jwt.JwtProperties;
import ago.ago_be.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public User join(UserRequestDto userRequestDto) {
        String encPassword = bCryptPasswordEncoder.encode(userRequestDto.getPassword());
        userRequestDto.setPassword(encPassword);
        User user = userRequestDto.toEntity();
        validateDuplicateUser(user); // 이메일로 중복 회원 검증
        return userRepository.save(user);
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());
        if (findUser != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    public Map<String, String> refresh(String refreshToken) {
        String email = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                .verify(refreshToken).getSubject();
        User user = userRepository.findByEmail(email);
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new JWTVerificationException("유효하지 않은 Refresh Token 입니다.");
        }
        Map<String, String> tokenResponseMap = new HashMap<>();
        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.AT_EXP_TIME))
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        tokenResponseMap.put(JwtProperties.AT_HEADER, JwtProperties.TOKEN_PREFIX + accessToken);

        String newRefreshToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.RT_EXP_TIME))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        tokenResponseMap.put(JwtProperties.RT_HEADER, JwtProperties.TOKEN_PREFIX + newRefreshToken);
        user.setRefreshToken(newRefreshToken);

        return tokenResponseMap;
    }
}
