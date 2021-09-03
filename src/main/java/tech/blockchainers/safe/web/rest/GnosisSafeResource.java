package tech.blockchainers.safe.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.blockchainers.safe.domain.GnosisSafe;
import tech.blockchainers.safe.domain.SaltedUser;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.repository.GnosisSafeRepository;
import tech.blockchainers.safe.repository.SaltedUserRepository;
import tech.blockchainers.safe.web.rest.external.ExternalGnosisSafeServices;
import tech.blockchainers.safe.web.rest.access.UserPermissionCheck;
import tech.blockchainers.safe.web.rest.dto.GnosisSafeSetupDto;
import tech.blockchainers.safe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link tech.blockchainers.safe.domain.GnosisSafe}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GnosisSafeResource {

    private final Logger log = LoggerFactory.getLogger(GnosisSafeResource.class);

    private static final String ENTITY_NAME = "gnosisSafe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GnosisSafeRepository gnosisSafeRepository;
    private final ExternalGnosisSafeServices externalGnosisSafeServices;
    private final SaltedUserRepository saltedUserRepository;
    private final UserPermissionCheck userPermissionCheck;

    public GnosisSafeResource(GnosisSafeRepository gnosisSafeRepository, ExternalGnosisSafeServices externalGnosisSafeServices, SaltedUserRepository saltedUserRepository, UserPermissionCheck userPermissionCheck) {
        this.gnosisSafeRepository = gnosisSafeRepository;
        this.externalGnosisSafeServices = externalGnosisSafeServices;
        this.saltedUserRepository = saltedUserRepository;
        this.userPermissionCheck = userPermissionCheck;
    }

    /**
     * {@code POST  /gnosis-safes} : Create a new gnosisSafe.
     *
     * @param gnosisSafe the gnosisSafe to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gnosisSafe, or with status {@code 400 (Bad Request)} if the gnosisSafe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/gnosis-safes")
    public ResponseEntity<GnosisSafe> createGnosisSafe(@Valid @RequestBody GnosisSafe gnosisSafe) throws URISyntaxException {
        log.debug("REST request to save GnosisSafe : {}", gnosisSafe);
        if (gnosisSafe.getId() != null) {
            throw new BadRequestAlertException("A new gnosisSafe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GnosisSafe result = gnosisSafeRepository.save(gnosisSafe);
        return ResponseEntity
            .created(new URI("/api/gnosis-safes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /gnosis-safes/:id} : Updates an existing gnosisSafe.
     *
     * @param id the id of the gnosisSafe to save.
     * @param gnosisSafe the gnosisSafe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gnosisSafe,
     * or with status {@code 400 (Bad Request)} if the gnosisSafe is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gnosisSafe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/gnosis-safes/{id}")
    public ResponseEntity<GnosisSafe> updateGnosisSafe(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GnosisSafe gnosisSafe
    ) throws URISyntaxException {
        log.debug("REST request to update GnosisSafe : {}, {}", id, gnosisSafe);
        if (gnosisSafe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gnosisSafe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gnosisSafeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GnosisSafe result = gnosisSafeRepository.save(gnosisSafe);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gnosisSafe.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /gnosis-safes/:id} : Partial updates given fields of an existing gnosisSafe, field will ignore if it is null
     *
     * @param id the id of the gnosisSafe to save.
     * @param gnosisSafe the gnosisSafe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gnosisSafe,
     * or with status {@code 400 (Bad Request)} if the gnosisSafe is not valid,
     * or with status {@code 404 (Not Found)} if the gnosisSafe is not found,
     * or with status {@code 500 (Internal Server Error)} if the gnosisSafe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/gnosis-safes/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<GnosisSafe> partialUpdateGnosisSafe(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GnosisSafe gnosisSafe
    ) throws URISyntaxException {
        log.debug("REST request to partial update GnosisSafe partially : {}, {}", id, gnosisSafe);
        if (gnosisSafe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gnosisSafe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gnosisSafeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GnosisSafe> result = gnosisSafeRepository
            .findById(gnosisSafe.getId())
            .map(
                existingGnosisSafe -> {
                    if (gnosisSafe.getName() != null) {
                        existingGnosisSafe.setName(gnosisSafe.getName());
                    }
                    if (gnosisSafe.getAddress() != null) {
                        existingGnosisSafe.setAddress(gnosisSafe.getAddress());
                    }
                    if (gnosisSafe.getSignatures() != null) {
                        existingGnosisSafe.setSignatures(gnosisSafe.getSignatures());
                    }

                    return existingGnosisSafe;
                }
            )
            .map(gnosisSafeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gnosisSafe.getId().toString())
        );
    }

    /**
     * {@code GET  /gnosis-safes} : get all the gnosisSafes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gnosisSafes in body.
     */
    @GetMapping("/gnosis-safes")
    public List<GnosisSafe> getAllGnosisSafes(@RequestParam(required = false, defaultValue = "false") boolean eagerload, Principal loggedInPrincipal) {
        log.debug("REST request to get all GnosisSafes");
        List<GnosisSafe> allSafes = gnosisSafeRepository.findAllWithEagerRelationships();
        if (loggedInPrincipal.getName().equals("admin")) {
            return allSafes;
        }
        return allSafes.stream().filter(safe -> safe.getOwners().contains(userPermissionCheck.getCurrentlyLoggedInUser())).collect(Collectors.toList());
    }

    /**
     * {@code GET  /gnosis-safes/:id} : get the "id" gnosisSafe.
     *
     * @param id the id of the gnosisSafe to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gnosisSafe, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/gnosis-safes/{id}")
    public ResponseEntity<GnosisSafe> getGnosisSafe(@PathVariable Long id) {
        log.debug("REST request to get GnosisSafe : {}", id);
        Optional<GnosisSafe> gnosisSafe = gnosisSafeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(gnosisSafe);
    }

    /**
     * {@code DELETE  /gnosis-safes/:id} : delete the "id" gnosisSafe.
     *
     * @param id the id of the gnosisSafe to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/gnosis-safes/{id}")
    public ResponseEntity<Void> deleteGnosisSafe(@PathVariable Long id) {
        log.debug("REST request to delete GnosisSafe : {}", id);
        gnosisSafeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/gnosis-safes/setup/{id}")
    public ResponseEntity<Void> setup(@PathVariable Long id) {
        GnosisSafe safe = gnosisSafeRepository.findById(id).get();
        Map<User, String> saltedUserToAddress = saltedUserRepository.findAll().stream().collect(Collectors.toMap(SaltedUser::getUser, SaltedUser::getAddress));
        GnosisSafeSetupDto builder = new GnosisSafeSetupDto();
        builder.setThreshold(safe.getSignatures());
        builder.setSafeAddress(safe.getAddress());
        builder.setOwners(safe.getOwners().stream().filter(it -> saltedUserToAddress.containsKey(it)).map(it -> saltedUserToAddress.get(it)).collect(Collectors.toList()).toArray(new String[]{}));
        String address = externalGnosisSafeServices.setupGnosisSafe(builder);
        log.info("Adress received: {}", address);
        return ResponseEntity.noContent().build();
    }
}
