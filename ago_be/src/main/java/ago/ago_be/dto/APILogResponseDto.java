package ago.ago_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class APILogResponseDto {
    private Long id;
    private String url;
    private String method;
    private int responseCode;
    private String time;
    private int processingTime;

    @Builder
    public APILogResponseDto(Long id, String url, String method, int responseCode, String time, int processingTime) {
        this.id = id;
        this.url = url;
        this.method = method;
        this.responseCode = responseCode;
        this.time = time;
        this.processingTime = processingTime;
    }

}
