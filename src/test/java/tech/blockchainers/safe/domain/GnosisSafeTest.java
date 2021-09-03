package tech.blockchainers.safe.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tech.blockchainers.safe.web.rest.TestUtil;

class GnosisSafeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GnosisSafe.class);
        GnosisSafe gnosisSafe1 = new GnosisSafe();
        gnosisSafe1.setId(1L);
        GnosisSafe gnosisSafe2 = new GnosisSafe();
        gnosisSafe2.setId(gnosisSafe1.getId());
        assertThat(gnosisSafe1).isEqualTo(gnosisSafe2);
        gnosisSafe2.setId(2L);
        assertThat(gnosisSafe1).isNotEqualTo(gnosisSafe2);
        gnosisSafe1.setId(null);
        assertThat(gnosisSafe1).isNotEqualTo(gnosisSafe2);
    }
}
