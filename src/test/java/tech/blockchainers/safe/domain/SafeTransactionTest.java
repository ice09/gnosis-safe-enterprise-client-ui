package tech.blockchainers.safe.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tech.blockchainers.safe.web.rest.TestUtil;

class SafeTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SafeTransaction.class);
        SafeTransaction safeTransaction1 = new SafeTransaction();
        safeTransaction1.setId(1L);
        SafeTransaction safeTransaction2 = new SafeTransaction();
        safeTransaction2.setId(safeTransaction1.getId());
        assertThat(safeTransaction1).isEqualTo(safeTransaction2);
        safeTransaction2.setId(2L);
        assertThat(safeTransaction1).isNotEqualTo(safeTransaction2);
        safeTransaction1.setId(null);
        assertThat(safeTransaction1).isNotEqualTo(safeTransaction2);
    }
}
