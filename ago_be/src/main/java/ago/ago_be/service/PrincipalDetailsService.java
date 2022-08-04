package ago.ago_be.service;

import ago.ago_be.config.auth.PrincipalDetails;
import ago.ago_be.domain.User;
import ago.ago_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login => 여기서 동작을 안한다.(formLogin.disable())
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService의 loadUserByUsername() ");
        User user = userRepository.findByEmail(username);
        return new PrincipalDetails(user);
    }
}
