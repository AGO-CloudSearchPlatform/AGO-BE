package ago.ago_be.interceptor;

import ago.ago_be.domain.Index;
import ago.ago_be.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class APIKeyAuthorizationInterceptor implements HandlerInterceptor {

    private final IndexRepository indexRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("X-API-Key");
        if (header == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        Optional<Index> optionalIndex = indexRepository.findByApiKey(header);
        if (optionalIndex.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        } else {
            Index index = optionalIndex.get();
            String uri = request.getRequestURI();
            String[] split = uri.split("/");
            String indexName = split[4];
            if (!index.getName().equals(indexName)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
            Long userId = index.getUser().getId();
            request.setAttribute("userId", userId);
            return true;
        }
    }
}
