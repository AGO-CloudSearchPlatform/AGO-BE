package ago.ago_be.controller.v2;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/documents")
public class DocumentControllerV2 {

    private final DocumentService documentService;

    @GetMapping("/{indexName}/{documentId}")
    public ResponseEntity<Map<String, Object>> getDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") String documentId, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = documentService.findDocument(principalDetails.getUser().getId(), indexName, documentId);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping(value = {"/{indexName}", "/{indexName}/{documentId}"})
    public ResponseEntity<Map<String, Object>> createDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") Optional<String> documentId, @RequestBody Map<String, Object> source, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = documentService.addDocument(principalDetails.getUser().getId(), indexName, documentId, source);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/{indexName}/bulk")
    public ResponseEntity<Map<String, Object>> bulk(@PathVariable("indexName") String indexName, @RequestBody String bulkData, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = documentService.bulk(principalDetails.getUser().getId(), indexName, bulkData);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/{indexName}/search")
    public ResponseEntity<Map<String, Object>> searchDocuments(@PathVariable("indexName") String indexName, @RequestBody Map<String, Object> query, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = documentService.search(principalDetails.getUser().getId(), indexName, query);
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/{indexName}/{documentId}")
    public ResponseEntity<Map<String, Object>> updateDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") String documentId, @RequestBody Map<String, Object> source, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = documentService.editDocument(principalDetails.getUser().getId(), indexName, documentId, source);
        return ResponseEntity.ok(responseMap);
    }

    @DeleteMapping("/{indexName}/{documentId}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") String documentId, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Object> responseMap = documentService.removeDocument(principalDetails.getUser().getId(), indexName, documentId);
        return ResponseEntity.ok(responseMap);
    }
}
