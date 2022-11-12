package ago.ago_be.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Authority authority; //ROLE_USER, ROLE_ADMIN

    @CreationTimestamp
    private Timestamp createDate;

    @OneToMany(mappedBy = "user")
    private List<Index> indices = new ArrayList<>();

    @Builder
    public User(Long id, String email, String password, String nickname, Authority authority, Timestamp createDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.authority = authority;
        this.createDate = createDate;
    }

}
