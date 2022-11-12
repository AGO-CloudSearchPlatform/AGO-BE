package ago.ago_be.service;

import ago.ago_be.config.ElasticsearchConfig;
import ago.ago_be.domain.APILog;
import ago.ago_be.domain.Index;
import ago.ago_be.domain.User;
import ago.ago_be.dto.APILogResponseDto;
import ago.ago_be.repository.APILogRepository;
import ago.ago_be.repository.IndexRepository;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexService {

    private final UserRepository userRepository;
    private final IndexRepository indexRepository;
    private final RestTemplate restTemplate;
    private final ElasticsearchConfig elasticsearchConfig;

    public Map<String, List<String>> findIndices(Long userId) {
        User user = userRepository.findById(userId).get();
        Map<String, List<String>> responseMap = new HashMap<>();
        List<String> indexList = new ArrayList<>();
        for (Index index : user.getIndices()) {
            indexList.add(index.getName());
        }
        responseMap.put("indices", indexList);
        return responseMap;
    }

    public Map<String, Object> findIndex(Long userId, String indexName) {
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        // 해당 이름의 인덱스가 존재하는지 확인 및 예외처리 필요
        String requestIndexName = "index-" + index.getId() + "-" + indexName;
        Map<String, Object> responseMap = new HashMap<>();

        String baseURL = "http://" + elasticsearchConfig.getIp() + ":" + elasticsearchConfig.getPort();
        String path = "/_cat/indices/" + requestIndexName;
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL).path(path);
        URI uri = uriComponentsBuilder.encode().build().toUri();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        if (responseEntity.getStatusCode().value() == 200) {
            Map<String, String> infoMap = new HashMap<>();
            if (index.getApiKey() != null) {
                infoMap.put("api_key_prefix", index.getApiKey().substring(0, 12));
            }
            String[] split = responseEntity.getBody().split(" ");
            infoMap.put("docs_count", split[6]);
            infoMap.put("store_size", split[8]);
            responseMap.put("info", infoMap);
        } else {
            System.out.println("CAT API Error");
        }

        try {
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(elasticsearchConfig.getIp(), elasticsearchConfig.getPort(), "http")
                    )
            );
            GetIndexRequest request = new GetIndexRequest(requestIndexName);
            GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
            Map<String, Object> mappings = getIndexResponse.getMappings().get(requestIndexName).getSourceAsMap();
            responseMap.put("name", indexName);
            responseMap.put("mappings", mappings);
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    public List<APILogResponseDto> findLogs(Long userId, String indexName) {
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        // 해당 유저의 인덱스가 맞는지 확인 필요
        List<APILogResponseDto> responseDtoList = new ArrayList<>();
        for (APILog apiLog : index.getApiLogs()) {
            responseDtoList.add(APILogResponseDto.builder()
                    .id(apiLog.getId())
                    .url(apiLog.getUrl())
                    .method(apiLog.getMethod())
                    .responseCode(apiLog.getResponseCode())
                    .time(timeToString(apiLog.getTime()))
                    .processingTime(apiLog.getProcessingTime())
                    .build());
        }
        return responseDtoList;
    }

    @Transactional
    public Map<String, Object> addIndex(Long userId, String indexName, Map<String, Object> mappings) {
        User user = userRepository.findById(userId).get();
        Index index = Index.builder().user(user).name(indexName).build();
        indexRepository.save(index); // 해당 사용자의 인덱스 이름 중복 검증 필요
        Map<String, Object> responseMap = new HashMap<>();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchConfig.getIp(), elasticsearchConfig.getPort(), "http")
                )
        );
        String requestIndexName = "index-" + index.getId() + "-" + indexName;
        System.out.println("requestIndexName = " + requestIndexName);
        CreateIndexRequest request = new CreateIndexRequest(requestIndexName);
        request.mapping(mappings);
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            responseMap.put("acknowledged", createIndexResponse.isAcknowledged());
            responseMap.put("index", indexName);
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    public Map<String, Object> search(Long userId, String indexName, Map<String, Object> query) {
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        // 해당 이름의 인덱스가 존재하는지 확인 및 예외처리 필요
        Map<String, Object> responseMap = new HashMap<>();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchConfig.getIp(), elasticsearchConfig.getPort(), "http")
                )
        );
        String requestIndexName = "index-" + index.getId() + "-" + indexName;
        try {
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            xContentBuilder.map(query);
            String json = Strings.toString(xContentBuilder);

            SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
            XContentParser parser = XContentFactory.xContent(XContentType.JSON)
                    .createParser(new NamedXContentRegistry(searchModule.getNamedXContents()),
                            LoggingDeprecationHandler.INSTANCE,
                            json);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.parseXContent(parser);

            SearchRequest searchRequest = new SearchRequest(requestIndexName);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            responseMap.put("took", searchResponse.getTook().getMillis());
            responseMap.put("timed_out", searchResponse.isTimedOut());
            SearchHits hits = searchResponse.getHits();
            Map<String, Object> hitsMap = new HashMap<>();
            responseMap.put("hits", hitsMap);
            Map<String, Object> totalMap = new HashMap<>();
            hitsMap.put("total", totalMap);
            totalMap.put("value", hits.getTotalHits().value);
            totalMap.put("relation", hits.getTotalHits().relation);
            hitsMap.put("max_score", hits.getMaxScore());
            List<Map<String, Object>> hitsList = new ArrayList<>();
            hitsMap.put("hits", hitsList);
            for (SearchHit hit : hits.getHits()) {
                Map<String, Object> hitMap = new HashMap<>();
                hitMap.put("index", indexName);
                hitMap.put("id", hit.getId());
                hitMap.put("score", hit.getScore());
                hitMap.put("source", hit.getSourceAsMap());
                hitsList.add(hitMap);
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        }
        return responseMap;
    }

    public Map<String, Boolean> addMappings(Long userId, String indexName, Map<String, Object> mappings) {
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        // 해당 이름의 인덱스가 존재하는지 확인 및 예외처리 필요
        Map<String, Boolean> responseMap = new HashMap<>();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchConfig.getIp(), elasticsearchConfig.getPort(), "http")
                )
        );
        String requestIndexName = "index-" + index.getId() + "-" + indexName;
        PutMappingRequest request = new PutMappingRequest(requestIndexName);
        request.source(mappings);
        try {
            AcknowledgedResponse putMappingResponse = client.indices().putMapping(request, RequestOptions.DEFAULT);
            responseMap.put("isAcknowledged", putMappingResponse.isAcknowledged());
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    @Transactional
    public Map<String, Boolean> removeIndex(Long userId, String indexName) {
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        // 해당 이름의 인덱스가 존재하는지 확인 및 예외처리 필요
        Map<String, Boolean> responseMap = new HashMap<>();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchConfig.getIp(), elasticsearchConfig.getPort(), "http")
                )
        );
        String requestIndexName = "index-" + index.getId() + "-" + indexName;
        DeleteIndexRequest request = new DeleteIndexRequest(requestIndexName);
        try {
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            responseMap.put("isAcknowledged", deleteIndexResponse.isAcknowledged());
            client.close();
            indexRepository.delete(index);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    private String timeToString(Timestamp timestamp) {
        String[] list = timestamp.toString().split(" ");
        String time = list[0] + "T" + list[1].split("[.]")[0] + "Z";
        return time;
    }
}
