package ago.ago_be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("elasticsearch.prod")
@Getter @Setter
public class ElasticsearchConfig {

    private String ip;
    private int port;
}
