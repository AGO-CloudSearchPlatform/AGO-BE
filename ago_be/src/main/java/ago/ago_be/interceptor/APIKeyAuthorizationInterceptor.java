package ago.ago_be.interceptor;

import ago.ago_be.domain.Index;
import ago.ago_be.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class APIKeyAuthorizationInterceptor implements HandlerInterceptor {

    private final IndexRepository indexRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("X-API-KEY");
        if (header == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        if (header.equals("test")) {
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
//        Index index = indexRepository.findByApiKey(header);
//        if (index == null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return false;
//        }
//        return true;

    }
}
