package tech.blockchainers.safe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.blockchainers.safe.domain.SignedTransaction;

import java.util.List;

/**
 * Spring Data SQL repository for the SignedTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignedTransactionRepository extends JpaRepository<SignedTransaction, Long> {
    @Query(
        "select signedTransaction from SignedTransaction signedTransaction where signedTransaction.signer.login = ?#{principal.username}"
    )
    List<SignedTransaction> findBySignerIsCurrentUser();

}
