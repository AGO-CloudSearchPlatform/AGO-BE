package ago.ago_be.service;


import ago.ago_be.domain.User;
import ago.ago_be.dto.UserRequestDto;
import ago.ago_be.dto.UserResponseDto;
import ago.ago_be.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    UserRepository userRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        UserRequestDto userRequestDto = UserRequestDto.builder().nickname("kim").password("1234").email("kim@naver.com").build();

        //when
        UserResponseDto userResponseDto = authService.join(userRequestDto);

        //then
        User findUser = userRepository.findByEmail(userResponseDto.getEmail());
        System.out.println("findUser = " + findUser);
        assertEquals(userRequestDto.getEmail(), findUser.getEmail());
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        UserRequestDto userRequestDto1 = UserRequestDto.builder().nickname("kim").password("1234").email("kim@naver.com").build();
        UserRequestDto userRequestDto2 = UserRequestDto.builder().nickname("kim").password("14124").email("kim@naver.com").build();

        //when
        authService.join(userRequestDto1);
        authService.join(userRequestDto2); //예외가 발생해야 한다!!!

        //then
        fail("예외가 발생해야 한다.");
    }

}
