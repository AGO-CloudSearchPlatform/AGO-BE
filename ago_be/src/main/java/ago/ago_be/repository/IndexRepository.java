package ago.ago_be.repository;

import ago.ago_be.domain.Index;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndexRepository extends JpaRepository<Index, Long> {

    public Optional<Index> findByApiKey(String apiKey);
}
