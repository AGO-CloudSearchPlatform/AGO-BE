package ago.ago_be.config;

import ago.ago_be.jwt.JwtAuthenticationFilter;
import ago.ago_be.jwt.JwtAuthorizationFilter;
import ago.ago_be.repository.UserRepository;
import ago.ago_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final UserService userService;

    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomConfigurer()) // 커스텀 필터 등록
                .and()
                .authorizeRequests()
                .antMatchers("/api/docs/**").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    public class CustomConfigurer extends AbstractHttpConfigurer<CustomConfigurer, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {

            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, userService);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
            http.addFilter(corsFilter)
                    .addFilter(jwtAuthenticationFilter)
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));

        }
    }
}
