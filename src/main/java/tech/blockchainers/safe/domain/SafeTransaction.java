package tech.blockchainers.safe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SafeTransaction.
 */
@Entity
@Table(name = "safe_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SafeTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "token")
    private String token;

    @NotNull
    @Column(name = "value", nullable = false)
    private Integer value;

    @NotNull
    @Column(name = "receiver", nullable = false)
    private String receiver;

    @NotNull
    @Column(name = "created", nullable = false)
    private ZonedDateTime created;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "safeTransaction")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "signer", "safeTransaction" }, allowSetters = true)
    private Set<SignedTransaction> signedTransactions = new HashSet<>();

    @ManyToOne(optional = false)
    private User creator;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "transactions", "owners" }, allowSetters = true)
    private GnosisSafe gnosisSafe;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SafeTransaction id(Long id) {
        this.id = id;
        return this;
    }

    public String getComment() {
        return this.comment;
    }

    public SafeTransaction comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getToken() {
        return this.token;
    }

    public SafeTransaction token(String token) {
        this.token = token;
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getValue() {
        return this.value;
    }

    public SafeTransaction value(Integer value) {
        this.value = value;
        return this;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public SafeTransaction receiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public SafeTransaction created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public Set<SignedTransaction> getSignedTransactions() {
        return this.signedTransactions;
    }

    public SafeTransaction signedTransactions(Set<SignedTransaction> signedTransactions) {
        this.setSignedTransactions(signedTransactions);
        return this;
    }

    public SafeTransaction addSignedTransactions(SignedTransaction signedTransaction) {
        this.signedTransactions.add(signedTransaction);
        signedTransaction.setSafeTransaction(this);
        return this;
    }

    public SafeTransaction removeSignedTransactions(SignedTransaction signedTransaction) {
        this.signedTransactions.remove(signedTransaction);
        signedTransaction.setSafeTransaction(null);
        return this;
    }

    public void setSignedTransactions(Set<SignedTransaction> signedTransactions) {
        if (this.signedTransactions != null) {
            this.signedTransactions.forEach(i -> i.setSafeTransaction(null));
        }
        if (signedTransactions != null) {
            signedTransactions.forEach(i -> i.setSafeTransaction(this));
        }
        this.signedTransactions = signedTransactions;
    }

    public User getCreator() {
        return this.creator;
    }

    public SafeTransaction creator(User user) {
        this.setCreator(user);
        return this;
    }

    public void setCreator(User user) {
        this.creator = user;
    }

    public GnosisSafe getGnosisSafe() {
        return this.gnosisSafe;
    }

    public SafeTransaction gnosisSafe(GnosisSafe gnosisSafe) {
        this.setGnosisSafe(gnosisSafe);
        return this;
    }

    public void setGnosisSafe(GnosisSafe gnosisSafe) {
        this.gnosisSafe = gnosisSafe;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SafeTransaction)) {
            return false;
        }
        return id != null && id.equals(((SafeTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SafeTransaction{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", token='" + getToken() + "'" +
            ", value=" + getValue() +
            ", receiver='" + getReceiver() + "'" +
            ", created='" + getCreated() + "'" +
            "}";
    }
}
