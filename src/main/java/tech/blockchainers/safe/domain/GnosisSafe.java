package tech.blockchainers.safe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GnosisSafe.
 */
@Entity
@Table(name = "gnosis_safe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GnosisSafe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    @Column(name = "signatures", nullable = false)
    private Integer signatures;

    @OneToMany(mappedBy = "gnosisSafe")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "signedTransactions", "creator", "gnosisSafe" }, allowSetters = true)
    private Set<SafeTransaction> transactions = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @NotNull
    @JoinTable(
        name = "rel_gnosis_safe__owners",
        joinColumns = @JoinColumn(name = "gnosis_safe_id"),
        inverseJoinColumns = @JoinColumn(name = "owners_id")
    )
    private Set<User> owners = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GnosisSafe id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public GnosisSafe name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public GnosisSafe address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSignatures() {
        return this.signatures;
    }

    public GnosisSafe signatures(Integer signatures) {
        this.signatures = signatures;
        return this;
    }

    public void setSignatures(Integer signatures) {
        this.signatures = signatures;
    }

    public Set<SafeTransaction> getTransactions() {
        return this.transactions;
    }

    public GnosisSafe transactions(Set<SafeTransaction> safeTransactions) {
        this.setTransactions(safeTransactions);
        return this;
    }

    public GnosisSafe addTransactions(SafeTransaction safeTransaction) {
        this.transactions.add(safeTransaction);
        safeTransaction.setGnosisSafe(this);
        return this;
    }

    public GnosisSafe removeTransactions(SafeTransaction safeTransaction) {
        this.transactions.remove(safeTransaction);
        safeTransaction.setGnosisSafe(null);
        return this;
    }

    public void setTransactions(Set<SafeTransaction> safeTransactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setGnosisSafe(null));
        }
        if (safeTransactions != null) {
            safeTransactions.forEach(i -> i.setGnosisSafe(this));
        }
        this.transactions = safeTransactions;
    }

    public Set<User> getOwners() {
        return this.owners;
    }

    public GnosisSafe owners(Set<User> users) {
        this.setOwners(users);
        return this;
    }

    public GnosisSafe addOwners(User user) {
        this.owners.add(user);
        return this;
    }

    public GnosisSafe removeOwners(User user) {
        this.owners.remove(user);
        return this;
    }

    public void setOwners(Set<User> users) {
        this.owners = users;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GnosisSafe)) {
            return false;
        }
        return id != null && id.equals(((GnosisSafe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GnosisSafe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", signatures=" + getSignatures() +
            "}";
    }
}
