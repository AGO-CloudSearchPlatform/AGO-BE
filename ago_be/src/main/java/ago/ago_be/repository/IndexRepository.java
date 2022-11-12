package ago.ago_be.repository;

import ago.ago_be.domain.Index;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexRepository extends JpaRepository<Index, Long> {

    public Index findByApiKey(String apiKey);
}
