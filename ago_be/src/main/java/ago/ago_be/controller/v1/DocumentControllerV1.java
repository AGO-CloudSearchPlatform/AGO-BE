package ago.ago_be.controller.v1;

import ago.ago_be.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documents")
public class DocumentControllerV1 {

    private final DocumentService documentService;

    @GetMapping("/{indexName}/{documentId}")
    public ResponseEntity<Map<String, Object>> getDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") String documentId, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        Map<String, Object> responseMap = documentService.findDocument(userId, indexName, documentId);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping(value = {"/{indexName}", "/{indexName}/{documentId}"})
    public ResponseEntity<Map<String, Object>> createDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") Optional<String> documentId, @RequestBody Map<String, Object> source, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        Map<String, Object> responseMap = documentService.addDocument(userId, indexName, documentId, source);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/{indexName}/bulk")
    public ResponseEntity<Map<String, Object>> bulk(@PathVariable("indexName") String indexName, @RequestBody String bulkData, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        Map<String, Object> responseMap = documentService.bulk(userId, indexName, bulkData);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/{indexName}/search")
    public ResponseEntity<Map<String, Object>> searchDocuments(@PathVariable("indexName") String indexName, @RequestBody Map<String, Object> query, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        Map<String, Object> responseMap = documentService.search(userId, indexName, query);
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/{indexName}/{documentId}")
    public ResponseEntity<Map<String, Object>> updateDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") String documentId, @RequestBody Map<String, Object> source, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        Map<String, Object> responseMap = documentService.editDocument(userId, indexName, documentId, source);
        return ResponseEntity.ok(responseMap);
    }

    @DeleteMapping("/{indexName}/{documentId}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable("indexName") String indexName, @PathVariable("documentId") String documentId, HttpServletRequest request) {
        Long userId = Long.valueOf(String.valueOf(request.getAttribute("userId")));
        Map<String, Object> responseMap = documentService.removeDocument(userId, indexName, documentId);
        return ResponseEntity.ok(responseMap);
    }
}
