package tech.blockchainers.safe.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import tech.blockchainers.safe.domain.SaltedUser;

/**
 * Spring Data SQL repository for the SaltedUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaltedUserRepository extends JpaRepository<SaltedUser, Long> {}
