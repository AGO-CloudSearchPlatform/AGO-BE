package ago.ago_be.jwt;

public interface JwtProperties {
    String SECRET = "soma13#300Ago!";
    long AT_EXP_TIME = 60000L * 60 * 3; // 3시간
    long RT_EXP_TIME = 60000L * 60 * 24 * 30; // 1개월
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String AT_HEADER = "Access-Token";
    String RT_HEADER = "Refresh-Token";
}
