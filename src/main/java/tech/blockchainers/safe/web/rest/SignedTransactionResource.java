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
import tech.blockchainers.safe.domain.SignedTransaction;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.repository.SignedTransactionRepository;
import tech.blockchainers.safe.web.rest.external.ExternalGnosisSafeServices;
import tech.blockchainers.safe.web.rest.access.UserPermissionCheck;
import tech.blockchainers.safe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link tech.blockchainers.safe.domain.SignedTransaction}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SignedTransactionResource {

    private final Logger log = LoggerFactory.getLogger(SignedTransactionResource.class);

    private static final String ENTITY_NAME = "signedTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SignedTransactionRepository signedTransactionRepository;
    private final ExternalGnosisSafeServices externalGnosisSafeServices;
    private final UserPermissionCheck userPermissionCheck;

    public SignedTransactionResource(SignedTransactionRepository signedTransactionRepository, ExternalGnosisSafeServices externalGnosisSafeServices, UserPermissionCheck userPermissionCheck) {
        this.signedTransactionRepository = signedTransactionRepository;
        this.externalGnosisSafeServices = externalGnosisSafeServices;
        this.userPermissionCheck = userPermissionCheck;
    }

    /**
     * {@code POST  /signed-transactions} : Create a new signedTransaction.
     *
     * @param signedTransaction the signedTransaction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new signedTransaction, or with status {@code 400 (Bad Request)} if the signedTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/signed-transactions")
    public ResponseEntity<SignedTransaction> createSignedTransaction(@Valid @RequestBody SignedTransaction signedTransaction, Principal user)
        throws URISyntaxException {
        log.debug("REST request to save SignedTransaction : {}", signedTransaction);
        if (signedTransaction.getId() != null) {
            throw new BadRequestAlertException("A new signedTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        signedTransaction.setSignedTx("0x0");
        User userRepo = userPermissionCheck.getCurrentlyLoggedInUser();
        signedTransaction.setSigner(userRepo);
        String salt = signedTransaction.getSalt();
        signedTransaction.setSalt(null);
        SignedTransaction result = signedTransactionRepository.save(signedTransaction);

        SignedTransaction loaded = signedTransactionRepository.getOne(result.getId());
        String saltSignedTransaction = externalGnosisSafeServices.signTransactionWithSalt(
            salt,
            loaded.getSafeTransaction().getGnosisSafe().getAddress(),
            loaded.getSafeTransaction().getToken(),
            loaded.getSafeTransaction().getReceiver(),
            loaded.getSafeTransaction().getValue()
        );
        signedTransaction.setSignedTx(saltSignedTransaction);

        return ResponseEntity
            .created(new URI("/api/signed-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /signed-transactions/:id} : Updates an existing signedTransaction.
     *
     * @param id the id of the signedTransaction to save.
     * @param signedTransaction the signedTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated signedTransaction,
     * or with status {@code 400 (Bad Request)} if the signedTransaction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the signedTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/signed-transactions/{id}")
    public ResponseEntity<SignedTransaction> updateSignedTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SignedTransaction signedTransaction
    ) throws URISyntaxException {
        log.debug("REST request to update SignedTransaction : {}, {}", id, signedTransaction);
        if (signedTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, signedTransaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!signedTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        String salt = signedTransaction.getSalt();
        signedTransaction.setSalt(null);
        SignedTransaction loaded = signedTransactionRepository.getOne(signedTransaction.getId());
        String saltSignedTransaction = externalGnosisSafeServices.signTransactionWithSalt(
            salt,
            loaded.getSafeTransaction().getGnosisSafe().getAddress(),
            loaded.getSafeTransaction().getToken(),
            loaded.getSafeTransaction().getReceiver(),
            loaded.getSafeTransaction().getValue());
        signedTransaction.setSignedTx(saltSignedTransaction);

        SignedTransaction result = signedTransactionRepository.save(signedTransaction);

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, signedTransaction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /signed-transactions/:id} : Partial updates given fields of an existing signedTransaction, field will ignore if it is null
     *
     * @param id the id of the signedTransaction to save.
     * @param signedTransaction the signedTransaction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated signedTransaction,
     * or with status {@code 400 (Bad Request)} if the signedTransaction is not valid,
     * or with status {@code 404 (Not Found)} if the signedTransaction is not found,
     * or with status {@code 500 (Internal Server Error)} if the signedTransaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/signed-transactions/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SignedTransaction> partialUpdateSignedTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SignedTransaction signedTransaction
    ) throws URISyntaxException {
        log.debug("REST request to partial update SignedTransaction partially : {}, {}", id, signedTransaction);
        if (signedTransaction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, signedTransaction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!signedTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SignedTransaction> result = signedTransactionRepository
            .findById(signedTransaction.getId())
            .map(
                existingSignedTransaction -> {
                    if (signedTransaction.getSignedTx() != null) {
                        existingSignedTransaction.setSignedTx(signedTransaction.getSignedTx());
                    }
                    if (signedTransaction.getSalt() != null) {
                        existingSignedTransaction.setSalt(signedTransaction.getSalt());
                    }

                    return existingSignedTransaction;
                }
            )
            .map(signedTransactionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, signedTransaction.getId().toString())
        );
    }

    /**
     * {@code GET  /signed-transactions} : get all the signedTransactions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of signedTransactions in body.
     */
    @GetMapping("/signed-transactions")
    public List<SignedTransaction> getAllSignedTransactions(Principal loggedInPrincipal) {
        log.debug("REST request to get all SignedTransactions");
        List<SignedTransaction> allSignedTrx = signedTransactionRepository.findAll();
        if (loggedInPrincipal.getName().equals("admin")) {
            return allSignedTrx;
        }
        return allSignedTrx.stream().filter(trx -> userPermissionCheck.getCurrentlyLoggedInUser().equals(trx.getSigner())).collect(Collectors.toList());
    }

    /**
     * {@code GET  /signed-transactions/:id} : get the "id" signedTransaction.
     *
     * @param id the id of the signedTransaction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the signedTransaction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/signed-transactions/{id}")
    public ResponseEntity<SignedTransaction> getSignedTransaction(@PathVariable Long id) {
        log.debug("REST request to get SignedTransaction : {}", id);
        Optional<SignedTransaction> signedTransaction = signedTransactionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(signedTransaction);
    }

    /**
     * {@code DELETE  /signed-transactions/:id} : delete the "id" signedTransaction.
     *
     * @param id the id of the signedTransaction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/signed-transactions/{id}")
    public ResponseEntity<Void> deleteSignedTransaction(@PathVariable Long id) {
        log.debug("REST request to delete SignedTransaction : {}", id);
        signedTransactionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
