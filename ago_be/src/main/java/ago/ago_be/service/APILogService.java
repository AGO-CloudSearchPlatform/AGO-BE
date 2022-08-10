package ago.ago_be.service;

import ago.ago_be.domain.APILog;
import ago.ago_be.domain.User;
import ago.ago_be.dto.APILogResponseDto;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class APILogService {

    private final UserRepository userRepository;

    public List<APILogResponseDto> findLogs(Long userId, String indexName) {
        User user = userRepository.findById(userId).get();
        List<APILogResponseDto> responseDtoList = new ArrayList<>();
        for (APILog apiLog : user.getApiLogs()) {
            if (indexName.equals(apiLog.getIndexName())) {
                responseDtoList.add(APILogResponseDto.builder()
                        .id(apiLog.getId())
                        .url(apiLog.getUrl())
                        .method(apiLog.getMethod())
                        .responseCode(apiLog.getResponseCode())
                        .time(timeToString(apiLog.getTime()))
                        .processingTime(apiLog.getProcessingTime())
                        .build());
            }
        }
        return responseDtoList;
    }

    private String timeToString(Timestamp timestamp) {
        String[] list = timestamp.toString().split(" ");
        String time = list[0] + "T" + list[1].split("[.]")[0] + "Z";
        return time;
    }
}
