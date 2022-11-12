package ago.ago_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class APIKeyResponseDto {

    String indexName;
    String apiKey;

    @Builder
    public APIKeyResponseDto(String indexName, String apiKey) {
        this.indexName = indexName;
        this.apiKey = apiKey;
    }
}
