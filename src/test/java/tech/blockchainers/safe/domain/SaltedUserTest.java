package tech.blockchainers.safe.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tech.blockchainers.safe.web.rest.TestUtil;

class SaltedUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaltedUser.class);
        SaltedUser saltedUser1 = new SaltedUser();
        saltedUser1.setId(1L);
        SaltedUser saltedUser2 = new SaltedUser();
        saltedUser2.setId(saltedUser1.getId());
        assertThat(saltedUser1).isEqualTo(saltedUser2);
        saltedUser2.setId(2L);
        assertThat(saltedUser1).isNotEqualTo(saltedUser2);
        saltedUser1.setId(null);
        assertThat(saltedUser1).isNotEqualTo(saltedUser2);
    }
}
