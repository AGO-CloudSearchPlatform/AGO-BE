package ago.ago_be.interceptor;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.domain.APILog;
import ago.ago_be.domain.Index;
import ago.ago_be.domain.User;
import ago.ago_be.repository.APILogRepository;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class APILogInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final APILogRepository apiLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("beginTime", System.currentTimeMillis());
        return true;
    }

    @Override
    @Transactional
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long beginTime = (long) request.getAttribute("beginTime");
        long endTime = System.currentTimeMillis();
        String method = request.getMethod();
        String url = request.getRequestURI();
        if (method.equals("GET")) {
            return;
        }
        String[] split = url.split("/");
        String indexName = split[4];
        if (split[3].equals("indices") && method.equals("DELETE")) {
            return;
        }
        if (split.length >= 6 && split[5].equals("search")) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principalDetails.getUser().getId();
        Index index = null;
        User user = userRepository.findById(userId).get();
        for (Index i : user.getIndices()) {
            if (i.getName().equals(indexName)) {
                index = i;
                break;
            }
        }
        int processingTime = Long.valueOf(endTime - beginTime).intValue();
        APILog apiLog = APILog.builder()
                .index(index)
                .indexName(indexName)
                .url(url)
                .method(method)
                .responseCode(response.getStatus())
                .processingTime(processingTime)
                .build();
        apiLogRepository.save(apiLog);
    }
}
