package tech.blockchainers.safe.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.blockchainers.safe.domain.GnosisSafe;

/**
 * Spring Data SQL repository for the GnosisSafe entity.
 */
@Repository
public interface GnosisSafeRepository extends JpaRepository<GnosisSafe, Long> {
    @Query(
        value = "select distinct gnosisSafe from GnosisSafe gnosisSafe left join fetch gnosisSafe.owners",
        countQuery = "select count(distinct gnosisSafe) from GnosisSafe gnosisSafe"
    )
    Page<GnosisSafe> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct gnosisSafe from GnosisSafe gnosisSafe left join fetch gnosisSafe.owners")
    List<GnosisSafe> findAllWithEagerRelationships();

    @Query("select gnosisSafe from GnosisSafe gnosisSafe left join fetch gnosisSafe.owners where gnosisSafe.id =:id")
    Optional<GnosisSafe> findOneWithEagerRelationships(@Param("id") Long id);
}
