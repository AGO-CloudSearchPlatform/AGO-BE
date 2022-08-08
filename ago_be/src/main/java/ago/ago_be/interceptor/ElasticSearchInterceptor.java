package ago.ago_be.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class ElasticSearchInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String baseURL = "http://ec2-43-200-170-171.ap-northeast-2.compute.amazonaws.com:8080";
        String path = request.getRequestURI().replace("/api/es", "");
        String queryString = request.getQueryString();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL).path(path);

        if (queryString != null) {
            uriComponentsBuilder.query(queryString);
        }
        URI uri = uriComponentsBuilder
                .encode()
                .build()
                .toUri();

        ResponseEntity<String> result = null;
        String method = request.getMethod();
        if (method.equals("GET")) {
            result = restTemplate.getForEntity(uri, String.class);
        } else if (method.equals("POST")) {
            String body = getBody(request);
            RequestEntity<String> requestEntity = RequestEntity
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
            result = restTemplate.exchange(requestEntity, String.class);
        } else if (method.equals("PUT")) {
            String body = getBody(request);
            RequestEntity<String> requestEntity = RequestEntity
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
            result = restTemplate.exchange(requestEntity, String.class);
        } else if (method.equals("DELETE")) {
            result = restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result.getBody());
        return false;
    }

    private String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

}