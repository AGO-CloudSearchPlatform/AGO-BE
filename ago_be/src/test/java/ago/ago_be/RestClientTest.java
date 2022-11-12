package ago.ago_be;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.data.repository.query.Param;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RestClientTest {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );
//        ObjectMapper objectMapper = new ObjectMapper();
//        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
//        GetIndexRequest request = new GetIndexRequest("my-index-1");
//        GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
//        MappingMetadata indexMappings = getIndexResponse.getMappings().get("my-index-1");
//        System.out.println("indexMappings = " + objectMapper.writeValueAsString(indexMappings.getSourceAsMap()));
//        Settings indexSettings = getIndexResponse.getSettings().get("my-index-1");
//        Settings settings = indexSettings.getAsGroups().get("index");
        String indexName = "my-index-1";
        String id = "1";
        GetRequest getRequest = new GetRequest(indexName, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println("sourceAsMap = " + sourceAsMap);
        client.close();
    }
}
