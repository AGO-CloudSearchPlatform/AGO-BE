package ago.ago_be.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    private String apiKey;

    @CreationTimestamp
    private Timestamp createDate;

    @OneToMany(mappedBy = "index", cascade = CascadeType.REMOVE)
    private List<APILog> apiLogs = new ArrayList<>();

    @Builder
    public Index(Long id, User user, String name, Timestamp createDate) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.createDate = createDate;
    }

    public void updateApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
