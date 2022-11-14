package ago.ago_be.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeNicknameRequestDto {
    private String password;
    private String newNickname;
}
