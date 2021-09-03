package tech.blockchainers.safe.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SaltedUser.
 */
@Entity
@Table(name = "salted_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SaltedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "salt")
    private String salt;

    @Column(name = "address")
    private String address;

    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SaltedUser id(Long id) {
        this.id = id;
        return this;
    }

    public String getSalt() {
        return this.salt;
    }

    public SaltedUser salt(String salt) {
        this.salt = salt;
        return this;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAddress() {
        return this.address;
    }

    public SaltedUser address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return this.user;
    }

    public SaltedUser user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaltedUser)) {
            return false;
        }
        return id != null && id.equals(((SaltedUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaltedUser{" +
            "id=" + getId() +
            ", salt='" + getSalt() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
