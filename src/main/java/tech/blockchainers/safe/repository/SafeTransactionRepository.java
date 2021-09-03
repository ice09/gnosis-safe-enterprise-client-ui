package tech.blockchainers.safe.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import tech.blockchainers.safe.domain.SafeTransaction;

/**
 * Spring Data SQL repository for the SafeTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SafeTransactionRepository extends JpaRepository<SafeTransaction, Long> {
    @Query("select safeTransaction from SafeTransaction safeTransaction where safeTransaction.creator.login = ?#{principal.username}")
    List<SafeTransaction> findByCreatorIsCurrentUser();
}
