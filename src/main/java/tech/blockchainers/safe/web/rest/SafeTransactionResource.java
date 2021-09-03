package tech.blockchainers.safe.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.blockchainers.safe.domain.*;
import tech.blockchainers.safe.repository.GnosisSafeRepository;
import tech.blockchainers.safe.repository.SafeTransactionRepository;
import tech.blockchainers.safe.repository.SaltedUserRepository;
import tech.blockchainers.safe.web.rest.external.ExternalGnosisSafeServices;
import tech.blockchainers.safe.web.rest.access.UserPermissionCheck;
import tech.blockchainers.safe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link tech.blockchainers.safe.domain.SafeTransaction}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SafeTransactionResource {

    private final Logger log = LoggerFactory.getLogger(SafeTransactionResource.class);

    private static final String ENTITY_NAME = "safeTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SafeTransactionRepository safeTransactionRepository;
    private final SaltedUserRepository saltedUserRepository;
    private final ExternalGnosisSafeServices externalGnosisSafeServices;
    private final GnosisSafeRepository gnosisSafeRepository;
    private final UserPermissionCheck userPermissionCheck;

    public SafeTransactionResource(SafeTransactionRepository safeTransactionRepository, SaltedUserRepository saltedUserRepository, ExternalGnosisSafeServices externalGnosisSafeServices, GnosisSafeRepository gnosisSafeRepository, UserPermissionCheck userPermissionCheck) {
        this.safeTransactionRepository = safeTransactionRepository;
        this.saltedUserRepository = saltedUserRepository;
        this.externalGnosisSafeServices = externalGnosisSafeServices;
        this.gnosisSafeRepository = gnosisSafeRepository;
        this.userPermissionCheck = userPermissionCheck;
    }

    /**
     * {@code POST  /safe-transactions} : Create a new safeTransaction.
     *
     * @param safeTransaction the safeTransaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new safeTransaction, or with status {@code 400 (Bad Request)} if the safeTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/safe-transactions")
    public ResponseEntity<SafeTransaction> createSafeTransaction(@Valid @RequestBody SafeTransaction safeTransaction)
        throws URISyntaxException {
        log.debug("REST request to save SafeTransaction : {}", safeTransaction);
        if (safeTransaction.getId() != null) {
            throw new BadRequestAlertException("A new safeTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        safeTransaction.setCreator(userPermissionCheck.getCurrentlyLoggedInUser());

        SafeTransaction result = safeTransactionRepository.save(safeTransaction);
        return ResponseEntity
            .created(new URI("/api/safe-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /safe-transactions/:id} : Updates an existing safeTransaction.
     *
     * @param id the id of the safeTransaction to save.
     * @param safeTransaction the safeTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated safeTransaction,
     * or with status {@code 400 (Bad Request)} if the safeTransaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the safeTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/safe-transactions/{id}")
    public ResponseEntity<SafeTransaction> updateSafeTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SafeTransaction safeTransaction
    ) throws URISyntaxException {
        log.debug("REST request to update SafeTransaction : {}, {}", id, safeTransaction);
        if (safeTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, safeTransaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!safeTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        safeTransaction.setCreator(userPermissionCheck.getCurrentlyLoggedInUser());

        SafeTransaction result = safeTransactionRepository.save(safeTransaction);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, safeTransaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /safe-transactions/:id} : Partial updates given fields of an existing safeTransaction, field will ignore if it is null
     *
     * @param id the id of the safeTransaction to save.
     * @param safeTransaction the safeTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated safeTransaction,
     * or with status {@code 400 (Bad Request)} if the safeTransaction is not valid,
     * or with status {@code 404 (Not Found)} if the safeTransaction is not found,
     * or with status {@code 500 (Internal Server Error)} if the safeTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/safe-transactions/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SafeTransaction> partialUpdateSafeTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SafeTransaction safeTransaction
    ) throws URISyntaxException {
        log.debug("REST request to partial update SafeTransaction partially : {}, {}", id, safeTransaction);
        if (safeTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, safeTransaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!safeTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        safeTransaction.setCreator(userPermissionCheck.getCurrentlyLoggedInUser());

        Optional<SafeTransaction> result = safeTransactionRepository
            .findById(safeTransaction.getId())
            .map(
                existingSafeTransaction -> {
                    if (safeTransaction.getComment() != null) {
                        existingSafeTransaction.setComment(safeTransaction.getComment());
                    }
                    if (safeTransaction.getToken() != null) {
                        existingSafeTransaction.setToken(safeTransaction.getToken());
                    }
                    if (safeTransaction.getValue() != null) {
                        existingSafeTransaction.setValue(safeTransaction.getValue());
                    }
                    if (safeTransaction.getReceiver() != null) {
                        existingSafeTransaction.setReceiver(safeTransaction.getReceiver());
                    }
                    if (safeTransaction.getCreated() != null) {
                        existingSafeTransaction.setCreated(safeTransaction.getCreated());
                    }

                    return existingSafeTransaction;
                }
            )
            .map(safeTransactionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, safeTransaction.getId().toString())
        );
    }

    /**
     * {@code GET  /safe-transactions} : get all the safeTransactions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of safeTransactions in body.
     */
    @GetMapping("/safe-transactions")
    public List<SafeTransaction> getAllSafeTransactions(Principal loggedInPrincipal) {
        log.debug("REST request to get all SafeTransactions");
        List<SafeTransaction> allTrx = safeTransactionRepository.findAll();
        if (loggedInPrincipal.getName().equals("admin")) {
            return allTrx;
        }
        List<GnosisSafe> allSafes = gnosisSafeRepository.findAllWithEagerRelationships();
        List<GnosisSafe> filteredSafes = allSafes.stream().filter(safe -> safe.getOwners().contains(userPermissionCheck.getCurrentlyLoggedInUser())).collect(Collectors.toList());

        return allTrx.stream().filter(trx -> filteredSafes.contains(trx.getGnosisSafe())).collect(Collectors.toList());
    }

    /**
     * {@code GET  /safe-transactions/:id} : get the "id" safeTransaction.
     *
     * @param id the id of the safeTransaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the safeTransaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/safe-transactions/{id}")
    public ResponseEntity<SafeTransaction> getSafeTransaction(@PathVariable Long id) {
        log.debug("REST request to get SafeTransaction : {}", id);
        Optional<SafeTransaction> safeTransaction = safeTransactionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(safeTransaction);
    }

    /**
     * {@code DELETE  /safe-transactions/:id} : delete the "id" safeTransaction.
     *
     * @param id the id of the safeTransaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/safe-transactions/{id}")
    public ResponseEntity<Void> deleteSafeTransaction(@PathVariable Long id) {
        log.debug("REST request to delete SafeTransaction : {}", id);
        safeTransactionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/safe-transactions/submit/{id}")
    public ResponseEntity<Void> submit(@PathVariable Long id) {
        SafeTransaction safeTransaction = safeTransactionRepository.findById(id).get();
        Set<SignedTransaction> signedTrxs = safeTransaction.getSignedTransactions();
        StringBuilder sbAddressSignature = new StringBuilder();
        for (SignedTransaction signedTrx : signedTrxs) {
            String signedTrxHex = signedTrx.getSignedTx();
            SaltedUser saltedUser = saltedUserRepository.findAll().stream().filter(user -> user.getUser().equals(signedTrx.getSigner())).findFirst().get();
            String address = saltedUser.getAddress();
            sbAddressSignature.append(address);
            sbAddressSignature.append(";");
            sbAddressSignature.append(signedTrxHex);
            sbAddressSignature.append(",");
        }

        String addressSignatureSep = sbAddressSignature.toString();
        addressSignatureSep = addressSignatureSep.substring(0, addressSignatureSep.length()-1);

        String hash = externalGnosisSafeServices.submitTransaction(
            safeTransaction.getGnosisSafe().getAddress(),
            safeTransaction.getToken(),
            safeTransaction.getReceiver(),
            safeTransaction.getValue(),
            addressSignatureSep
        );
        log.info("transactionHash: {}", hash);
        return ResponseEntity.noContent().build();
    }

}
