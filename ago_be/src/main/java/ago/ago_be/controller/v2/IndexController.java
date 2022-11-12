package ago.ago_be.controller.v2;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.dto.APILogResponseDto;
import ago.ago_be.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/indices")
public class IndexController {

    private final IndexService indexService;

    @GetMapping
    public Map<String, List<String>> getIndices(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return indexService.findIndices(principalDetails.getUser().getId());
    }

    @GetMapping("/{indexName}")
    public Map<String, Object> getIndex(@PathVariable("indexName") String indexName, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return indexService.findIndex(principalDetails.getUser().getId(), indexName);
    }

    @GetMapping("/{indexName}/logs")
    public List<APILogResponseDto> getLogs(@PathVariable("indexName") String indexName, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return indexService.findLogs(principalDetails.getUser().getId(), indexName);
    }

    @PostMapping("/{indexName}")
    public ResponseEntity<Map<String, Object>> createIndex(@PathVariable("indexName") String indexName, @RequestBody Map<String, Object> mappings, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = indexService.addIndex(principalDetails.getUser().getId(), indexName, mappings);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/{indexName}/search")
    public ResponseEntity<Map<String, Object>> searchDocuments(@PathVariable("indexName") String indexName, @RequestBody Map<String, Object> query, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = indexService.search(principalDetails.getUser().getId(), indexName, query);
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/{indexName}/mapping")
    public ResponseEntity<Map<String, Boolean>> addMapping(@PathVariable("indexName") String indexName, @RequestBody Map<String, Object> mappings, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Boolean> isAcknowledged = indexService.addMappings(principalDetails.getUser().getId(), indexName, mappings);
        return ResponseEntity.ok(isAcknowledged);
    }

    @DeleteMapping("/{indexName}")
    public ResponseEntity<Map<String, Boolean>> deleteIndex(@PathVariable("indexName") String indexName, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Boolean> isAcknowledged = indexService.removeIndex(principalDetails.getUser().getId(), indexName);
        return ResponseEntity.ok(isAcknowledged);
    }
}
