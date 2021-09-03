package tech.blockchainers.safe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SignedTransaction.
 */
@Entity
@Table(name = "signed_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SignedTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "signed_tx", nullable = false)
    private String signedTx;

    @Column(name = "salt")
    private String salt;

    @ManyToOne(optional = false)
    private User signer;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "signedTransactions", "creator", "gnosisSafe" }, allowSetters = true)
    private SafeTransaction safeTransaction;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SignedTransaction id(Long id) {
        this.id = id;
        return this;
    }

    public String getSignedTx() {
        return this.signedTx;
    }

    public SignedTransaction signedTx(String signedTx) {
        this.signedTx = signedTx;
        return this;
    }

    public void setSignedTx(String signedTx) {
        this.signedTx = signedTx;
    }

    public String getSalt() {
        return this.salt;
    }

    public SignedTransaction salt(String salt) {
        this.salt = salt;
        return this;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public User getSigner() {
        return this.signer;
    }

    public SignedTransaction signer(User user) {
        this.setSigner(user);
        return this;
    }

    public void setSigner(User user) {
        this.signer = user;
    }

    public SafeTransaction getSafeTransaction() {
        return this.safeTransaction;
    }

    public SignedTransaction safeTransaction(SafeTransaction safeTransaction) {
        this.setSafeTransaction(safeTransaction);
        return this;
    }

    public void setSafeTransaction(SafeTransaction safeTransaction) {
        this.safeTransaction = safeTransaction;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignedTransaction)) {
            return false;
        }
        return id != null && id.equals(((SignedTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignedTransaction{" +
            "id=" + getId() +
            ", signedTx='" + getSignedTx() + "'" +
            ", salt='" + getSalt() + "'" +
            "}";
    }
}
