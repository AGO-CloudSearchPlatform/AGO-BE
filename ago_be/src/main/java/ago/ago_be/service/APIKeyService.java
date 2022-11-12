package ago.ago_be.service;

import ago.ago_be.domain.Index;
import ago.ago_be.domain.User;
import ago.ago_be.dto.APIKeyResponseDto;
import ago.ago_be.repository.IndexRepository;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class APIKeyService {

    private final UserRepository userRepository;

    public List<APIKeyResponseDto> findAPIKeys(Long userId) {
        User user = userRepository.findById(userId).get();
        List<APIKeyResponseDto> apiKeyResponseDtoList = new ArrayList<>();
        for (Index index : user.getIndices()) {
            if (index.getApiKey() != null) {
                apiKeyResponseDtoList.add(APIKeyResponseDto.builder()
                        .indexName(index.getName())
                        .apiKey(index.getApiKey().substring(0, 12) + "...")
                        .build()
                );
            }
        }
        return apiKeyResponseDtoList;
    }

    @Transactional
    public APIKeyResponseDto issue(Long userId, String indexName) {
        String uuid = UUID.randomUUID().toString();
        byte[] encode = Base64.getEncoder().encode(uuid.getBytes());
        String apiKey = new String(encode);
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        index.updateApiKey(apiKey);
        return APIKeyResponseDto.builder()
                .indexName(indexName)
                .apiKey(apiKey)
                .build();
    }
}
