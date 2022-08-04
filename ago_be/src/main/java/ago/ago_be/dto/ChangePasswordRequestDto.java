package ago.ago_be.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    private String exPassword;
    private String newPassword;
}
