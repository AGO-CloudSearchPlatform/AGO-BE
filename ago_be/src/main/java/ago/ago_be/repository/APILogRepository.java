package ago.ago_be.repository;

import ago.ago_be.domain.APILog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface APILogRepository extends JpaRepository<APILog, Long> {
}
