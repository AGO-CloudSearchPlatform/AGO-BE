package ago.ago_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private String nickname;

    @Builder
    public UserResponseDto(String email, String nickname) {
        this.email = email;
        this.nickname =nickname;
    }
}
