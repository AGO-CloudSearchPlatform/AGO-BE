package ago.ago_be.controller;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.dto.APILogRequestDto;
import ago.ago_be.dto.APILogResponseDto;
import ago.ago_be.service.APILogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class APILogController {

    private final APILogService apiLogService;

    @GetMapping
    public List<APILogResponseDto> getLogs(@RequestBody APILogRequestDto apiLogRequestDto, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return apiLogService.findLogs(principalDetails.getUser().getId(), apiLogRequestDto.getIndexName());
    }
}
