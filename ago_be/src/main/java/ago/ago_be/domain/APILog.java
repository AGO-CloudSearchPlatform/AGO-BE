package ago.ago_be.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "api_log")
@Getter
@NoArgsConstructor
public class APILog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id")
    private Index index;

    @Column(nullable = false)
    private String indexName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private int responseCode;

    @CreationTimestamp
    private Timestamp time;

    @Column(nullable = false)
    private int processingTime;

    @Builder
    public APILog(Long id, Index index, String indexName, String url, String method, int responseCode, Timestamp time, int processingTime) {
        this.id = id;
        this.index = index;
        this.indexName = indexName;
        this.url = url;
        this.method = method;
        this.responseCode = responseCode;
        this.time = time;
        this.processingTime = processingTime;
    }

}
