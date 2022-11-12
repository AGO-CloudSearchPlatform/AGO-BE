package ago.ago_be.service;

import ago.ago_be.config.ElasticsearchConfig;
import ago.ago_be.domain.Index;
import ago.ago_be.domain.User;
import ago.ago_be.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final UserRepository userRepository;
    private final ElasticsearchConfig elasticsearchConfig;

    public Map<String, Object> findDocument(Long userId, String indexName, String documentId) {
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
        GetRequest getRequest = new GetRequest(requestIndexName, documentId);
        try {
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            responseMap.put("index", indexName);
            responseMap.put("id", getResponse.getId());
            if (getResponse.isExists()) {
                responseMap.put("version", getResponse.getVersion());
                responseMap.put("found", true);
                responseMap.put("source", getResponse.getSourceAsMap());
            } else {
                responseMap.put("found", false);
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    public Map<String, Object> addDocument(Long userId, String indexName, Optional<String> documentId, Map<String, Object> source) {
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
        IndexRequest indexRequest;
        if (documentId.isPresent()) {
            indexRequest = new IndexRequest(requestIndexName)
                    .id(documentId.get())
                    .source(source);
        } else {
            indexRequest = new IndexRequest(requestIndexName)
                    .source(source);
        }
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            responseMap.put("index", indexName);
            responseMap.put("id", indexResponse.getId());
            responseMap.put("version", indexResponse.getVersion());
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                responseMap.put("result", "created");
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                responseMap.put("result", "updated");
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    public Map<String, Object> bulk(Long userId, String indexName, String bulkData) {
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
        String[] jsonStringList = bulkData.split("\n");
        int i = 0;
        ObjectMapper mapper = new ObjectMapper();
        BulkRequest bulkRequest = new BulkRequest();
        while (i < jsonStringList.length) {
            String jsonStr = jsonStringList[i];
            if (jsonStr.equals("")) {
                break;
            }
            try {
                Map<String, Object> jsonMap = mapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {
                });

                if (jsonMap.containsKey("index")) {
                    Map<String, Object> indexMap = mapper.convertValue(jsonMap.get("index"), new TypeReference<Map<String, Object>>() {
                    });
                    i++;
                    Map<String, Object> source = mapper.readValue(jsonStringList[i], new TypeReference<Map<String, Object>>() {
                    });

                    if (indexMap.containsKey("id")) {
                        String id = indexMap.get("id").toString();
                        bulkRequest.add(new IndexRequest(requestIndexName).id(id)
                                .source(source));
                    } else {
                        bulkRequest.add(new IndexRequest(requestIndexName).source(source));
                    }

                } else if (jsonMap.containsKey("create")) {
                    Map<String, Object> createMap = mapper.convertValue(jsonMap.get("create"), new TypeReference<Map<String, Object>>() {
                    });
                    i++;
                    Map<String, Object> source = mapper.readValue(jsonStringList[i], new TypeReference<Map<String, Object>>() {
                    });

                    if (createMap.containsKey("id")) {
                        String id = createMap.get("id").toString();
                        bulkRequest.add(new IndexRequest(requestIndexName).id(id)
                                .source(source).opType(DocWriteRequest.OpType.CREATE));
                    } else {
                        bulkRequest.add(new IndexRequest(requestIndexName).source(source)
                                .opType(DocWriteRequest.OpType.CREATE));
                    }

                } else if (jsonMap.containsKey("update")) {
                    Map<String, Object> updateMap = mapper.convertValue(jsonMap.get("update"), new TypeReference<Map<String, Object>>() {
                    });
                    String id = updateMap.get("id").toString();
                    i++;
                    Map<String, Object> dataMap = mapper.readValue(jsonStringList[i], new TypeReference<Map<String, Object>>() {
                    });
                    Map<String, Object> source = mapper.convertValue(dataMap.get("doc"), new TypeReference<Map<String, Object>>() {
                    });

                    bulkRequest.add(new UpdateRequest(requestIndexName, id)
                            .doc(source));
                } else if (jsonMap.containsKey("delete")) {
                    Map<String, Object> deleteMap = mapper.convertValue(jsonMap.get("delete"), new TypeReference<Map<String, Object>>() {
                    });
                    String id = deleteMap.get("id").toString();
                    bulkRequest.add(new DeleteRequest(requestIndexName, id));
                }
                i++;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            responseMap.put("took", bulkResponse.getTook().getMillis());
            responseMap.put("errors", bulkResponse.hasFailures());
            List<Map<String, Object>> items = new ArrayList<>();
            responseMap.put("items", items);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                Map<String, Object> item = new HashMap<>();
                items.add(item);

                switch (bulkItemResponse.getOpType()) {
                    case INDEX:
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        Map<String, Object> indexResponseMap = new HashMap<>();
                        item.put("index", indexResponseMap);
                        indexResponseMap.put("index", indexName);

                        if (bulkItemResponse.isFailed()) {
                            BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                            indexResponseMap.put("id", failure.getId());
                            indexResponseMap.put("status", failure.getStatus().getStatus());
                            Map<String, Object> error = new HashMap<>();
                            error.put("type", failure.getType());
                            error.put("message", failure.getMessage());
                            indexResponseMap.put("error", error);
                        } else {
                            indexResponseMap.put("id", indexResponse.getId());
                            indexResponseMap.put("status", indexResponse.status().getStatus());
                            indexResponseMap.put("version", indexResponse.getVersion());
                            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                                indexResponseMap.put("result", "created");
                            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                                indexResponseMap.put("result", "updated");
                            }
                        }
                        break;
                    case CREATE:
                        IndexResponse createResponse = (IndexResponse) itemResponse;
                        Map<String, Object> createResponseMap = new HashMap<>();
                        item.put("create", createResponseMap);
                        createResponseMap.put("index", indexName);

                        if (bulkItemResponse.isFailed()) {
                            BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                            createResponseMap.put("id", failure.getId());
                            createResponseMap.put("status", failure.getStatus().getStatus());
                            Map<String, Object> error = new HashMap<>();
                            error.put("type", failure.getType());
                            error.put("message", failure.getMessage());
                            createResponseMap.put("error", error);
                        } else {
                            createResponseMap.put("id", createResponse.getId());
                            createResponseMap.put("status", createResponse.status().getStatus());
                            createResponseMap.put("version", createResponse.getVersion());
                            if (createResponse.getResult() == DocWriteResponse.Result.CREATED) {
                                createResponseMap.put("result", "created");
                            } else if (createResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                                createResponseMap.put("result", "updated");
                            }
                        }
                        break;
                    case UPDATE:
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        Map<String, Object> updateResponseMap = new HashMap<>();
                        item.put("update", updateResponseMap);
                        updateResponseMap.put("index", indexName);

                        if (bulkItemResponse.isFailed()) {
                            BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                            updateResponseMap.put("id", failure.getId());
                            updateResponseMap.put("status", failure.getStatus().getStatus());
                            Map<String, Object> error = new HashMap<>();
                            error.put("type", failure.getType());
                            error.put("message", failure.getMessage());
                            updateResponseMap.put("error", error);
                        } else {
                            updateResponseMap.put("id", updateResponse.getId());
                            updateResponseMap.put("status", updateResponse.status().getStatus());
                            updateResponseMap.put("version", updateResponse.getVersion());
                            if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
                                updateResponseMap.put("result", "created");
                            } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                                updateResponseMap.put("result", "updated");
                            } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
                                updateResponseMap.put("result", "deleted");
                            } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
                                updateResponseMap.put("result", "noop");
                            }
                        }
                        break;
                    case DELETE:
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        Map<String, Object> deleteResponseMap = new HashMap<>();
                        item.put("delete", deleteResponseMap);
                        deleteResponseMap.put("index", indexName);

                        if (bulkItemResponse.isFailed()) {
                            BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                            deleteResponseMap.put("id", failure.getId());
                            deleteResponseMap.put("status", failure.getStatus().getStatus());
                            Map<String, Object> error = new HashMap<>();
                            error.put("type", failure.getType());
                            error.put("message", failure.getMessage());
                            deleteResponseMap.put("error", error);
                        } else {
                            deleteResponseMap.put("id", deleteResponse.getId());
                            deleteResponseMap.put("status", deleteResponse.status().getStatus());
                            deleteResponseMap.put("version", deleteResponse.getVersion());

                            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                                deleteResponseMap.put("result", "not_found");
                            } else {
                                deleteResponseMap.put("result", "deleted");
                            }
                        }
                }
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        }
        return responseMap;
    }

    public Map<String, Object> editDocument(Long userId, String indexName, String documentId, Map<String, Object> source) {
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
        UpdateRequest request = new UpdateRequest(requestIndexName, documentId)
                .doc(source);
        try {
            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
            responseMap.put("index", indexName);
            responseMap.put("id", updateResponse.getId());
            responseMap.put("version", updateResponse.getVersion());
            if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
                responseMap.put("result", "created");
            } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                responseMap.put("result", "updated");
            } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
                responseMap.put("result", "deleted");
            } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
                responseMap.put("result", "noop");
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }

    public Map<String, Object> removeDocument(Long userId, String indexName, String documentId) {
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
        DeleteRequest request = new DeleteRequest(requestIndexName, documentId);
        try {
            DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
            responseMap.put("index", indexName);
            responseMap.put("id", deleteResponse.getId());
            responseMap.put("version", deleteResponse.getVersion());
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                responseMap.put("result", "not_found");
            } else {
                responseMap.put("result", "deleted");
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseMap;
    }
}
