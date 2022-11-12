package ago.ago_be.controller.v2;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.dto.APIKeyResponseDto;
import ago.ago_be.service.APIKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/keys")
public class APIKeyController {

    private final APIKeyService apiKeyService;

    @GetMapping
    public List<APIKeyResponseDto> getAPIKeys(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return apiKeyService.findAPIKeys(principalDetails.getUser().getId());
    }

    @PostMapping("/{indexName}")
    public APIKeyResponseDto createAPIKey(@PathVariable("indexName") String indexName, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return apiKeyService.issue(principalDetails.getUser().getId(), indexName);
    }
}
