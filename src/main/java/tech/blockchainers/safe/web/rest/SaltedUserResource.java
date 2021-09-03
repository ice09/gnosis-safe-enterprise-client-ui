package tech.blockchainers.safe.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.blockchainers.safe.domain.SaltedUser;
import tech.blockchainers.safe.repository.SaltedUserRepository;
import tech.blockchainers.safe.web.rest.access.UserPermissionCheck;
import tech.blockchainers.safe.web.rest.external.ExternalGnosisSafeServices;
import tech.blockchainers.safe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link tech.blockchainers.safe.domain.SaltedUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SaltedUserResource {

    private final Logger log = LoggerFactory.getLogger(SaltedUserResource.class);

    private static final String ENTITY_NAME = "saltedUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaltedUserRepository saltedUserRepository;
    private final ExternalGnosisSafeServices externalGnosisSafeServices;
    private final UserPermissionCheck userPermissionCheck;

    public SaltedUserResource(SaltedUserRepository saltedUserRepository, ExternalGnosisSafeServices externalGnosisSafeServices, UserPermissionCheck userPermissionCheck) {
        this.saltedUserRepository = saltedUserRepository;
        this.externalGnosisSafeServices = externalGnosisSafeServices;
        this.userPermissionCheck = userPermissionCheck;
    }

    /**
     * {@code POST  /salted-users} : Create a new saltedUser.
     *
     * @param saltedUser the saltedUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saltedUser, or with status {@code 400 (Bad Request)} if the saltedUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/salted-users")
    public ResponseEntity<SaltedUser> createSaltedUser(@Valid @RequestBody SaltedUser saltedUser) throws URISyntaxException {
        log.debug("REST request to save SaltedUser : {}", saltedUser);
        if (saltedUser.getId() != null) {
            throw new BadRequestAlertException("A new saltedUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        saltedUser.setUser(userPermissionCheck.getCurrentlyLoggedInUser());
        String address = externalGnosisSafeServices.getAddressForSaltedUser(saltedUser.getSalt());
        saltedUser.setAddress(address);
        saltedUser.setSalt(null);
        SaltedUser result = saltedUserRepository.save(saltedUser);
        return ResponseEntity
            .created(new URI("/api/salted-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /salted-users/:id} : Updates an existing saltedUser.
     *
     * @param id the id of the saltedUser to save.
     * @param saltedUser the saltedUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saltedUser,
     * or with status {@code 400 (Bad Request)} if the saltedUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saltedUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/salted-users/{id}")
    public ResponseEntity<SaltedUser> updateSaltedUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaltedUser saltedUser
    ) throws URISyntaxException {
        log.debug("REST request to update SaltedUser : {}, {}", id, saltedUser);
        if (saltedUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saltedUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saltedUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        String address = externalGnosisSafeServices.getAddressForSaltedUser(saltedUser.getSalt());
        saltedUser.setAddress(address);
        saltedUser.setSalt(null);
        saltedUser.setUser(userPermissionCheck.getCurrentlyLoggedInUser());

        SaltedUser result = saltedUserRepository.save(saltedUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saltedUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /salted-users/:id} : Partial updates given fields of an existing saltedUser, field will ignore if it is null
     *
     * @param id the id of the saltedUser to save.
     * @param saltedUser the saltedUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saltedUser,
     * or with status {@code 400 (Bad Request)} if the saltedUser is not valid,
     * or with status {@code 404 (Not Found)} if the saltedUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the saltedUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/salted-users/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SaltedUser> partialUpdateSaltedUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaltedUser saltedUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update SaltedUser partially : {}, {}", id, saltedUser);
        if (saltedUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (saltedUser.getSalt() == null) {
            throw new BadRequestAlertException("Invalid salt", ENTITY_NAME, "idsalt");
        }
        if (!Objects.equals(id, saltedUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saltedUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        String address = externalGnosisSafeServices.getAddressForSaltedUser(saltedUser.getSalt());
        saltedUser.setAddress(address);
        saltedUser.setSalt(null);
        saltedUser.setUser(userPermissionCheck.getCurrentlyLoggedInUser());

        Optional<SaltedUser> result = saltedUserRepository
            .findById(saltedUser.getId())
            .map(
                existingSaltedUser -> {
                    if (saltedUser.getAddress() != null) {
                        existingSaltedUser.setAddress(saltedUser.getAddress());
                    }

                    return existingSaltedUser;
                }
            )
            .map(saltedUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saltedUser.getId().toString())
        );
    }

    /**
     * {@code GET  /salted-users} : get all the saltedUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saltedUsers in body.
     */
    @GetMapping("/salted-users")
    public List<SaltedUser> getAllSaltedUsers(Principal loggedInPrincipal) {
        log.debug("REST request to get all SaltedUsers");
        List<SaltedUser> allUsers = saltedUserRepository.findAll();
        if (loggedInPrincipal.getName().equals("admin")) {
            return allUsers;
        }
        return allUsers.stream().filter(user -> user.getUser().equals(userPermissionCheck.getCurrentlyLoggedInUser())).collect(Collectors.toList());
    }

    /**
     * {@code GET  /salted-users/:id} : get the "id" saltedUser.
     *
     * @param id the id of the saltedUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saltedUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/salted-users/{id}")
    public ResponseEntity<SaltedUser> getSaltedUser(@PathVariable Long id) {
        log.debug("REST request to get SaltedUser : {}", id);
        Optional<SaltedUser> saltedUser = saltedUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(saltedUser);
    }

    /**
     * {@code DELETE  /salted-users/:id} : delete the "id" saltedUser.
     *
     * @param id the id of the saltedUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/salted-users/{id}")
    public ResponseEntity<Void> deleteSaltedUser(@PathVariable Long id) {
        log.debug("REST request to delete SaltedUser : {}", id);
        saltedUserRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
