package ago.ago_be.jwt;

public interface JwtProperties {
    String SECRET = "wldyd123";
    int EXPIRATION_TIME = 60000 * 30;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
