package ago.ago_be.config;

import ago.ago_be.interceptor.APIKeyAuthorizationInterceptor;
import ago.ago_be.interceptor.APILogInterceptor;
import ago.ago_be.interceptor.ElasticSearchInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final APIKeyAuthorizationInterceptor apiKeyAuthorizationInterceptor;
    private final APILogInterceptor apiLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyAuthorizationInterceptor)
                .addPathPatterns("/api/v1/**");
        registry.addInterceptor(apiLogInterceptor)
                .addPathPatterns("/api/v2/indices/**")
                .addPathPatterns("/api/v2/documents/**");
    }
}
