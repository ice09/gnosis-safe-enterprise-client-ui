package tech.blockchainers.safe.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tech.blockchainers.safe.web.rest.TestUtil;

class SignedTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SignedTransaction.class);
        SignedTransaction signedTransaction1 = new SignedTransaction();
        signedTransaction1.setId(1L);
        SignedTransaction signedTransaction2 = new SignedTransaction();
        signedTransaction2.setId(signedTransaction1.getId());
        assertThat(signedTransaction1).isEqualTo(signedTransaction2);
        signedTransaction2.setId(2L);
        assertThat(signedTransaction1).isNotEqualTo(signedTransaction2);
        signedTransaction1.setId(null);
        assertThat(signedTransaction1).isNotEqualTo(signedTransaction2);
    }
}
