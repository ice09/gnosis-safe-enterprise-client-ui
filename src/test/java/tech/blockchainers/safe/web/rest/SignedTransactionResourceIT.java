package tech.blockchainers.safe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tech.blockchainers.safe.IntegrationTest;
import tech.blockchainers.safe.domain.SafeTransaction;
import tech.blockchainers.safe.domain.SignedTransaction;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.repository.SignedTransactionRepository;

/**
 * Integration tests for the {@link SignedTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SignedTransactionResourceIT {

    private static final String DEFAULT_SIGNED_TX = "AAAAAAAAAA";
    private static final String UPDATED_SIGNED_TX = "BBBBBBBBBB";

    private static final String DEFAULT_SALT = "AAAAAAAAAA";
    private static final String UPDATED_SALT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/signed-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SignedTransactionRepository signedTransactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSignedTransactionMockMvc;

    private SignedTransaction signedTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignedTransaction createEntity(EntityManager em) {
        SignedTransaction signedTransaction = new SignedTransaction().signedTx(DEFAULT_SIGNED_TX).salt(DEFAULT_SALT);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        signedTransaction.setSigner(user);
        // Add required entity
        SafeTransaction safeTransaction;
        if (TestUtil.findAll(em, SafeTransaction.class).isEmpty()) {
            safeTransaction = SafeTransactionResourceIT.createEntity(em);
            em.persist(safeTransaction);
            em.flush();
        } else {
            safeTransaction = TestUtil.findAll(em, SafeTransaction.class).get(0);
        }
        signedTransaction.setSafeTransaction(safeTransaction);
        return signedTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignedTransaction createUpdatedEntity(EntityManager em) {
        SignedTransaction signedTransaction = new SignedTransaction().signedTx(UPDATED_SIGNED_TX).salt(UPDATED_SALT);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        signedTransaction.setSigner(user);
        // Add required entity
        SafeTransaction safeTransaction;
        if (TestUtil.findAll(em, SafeTransaction.class).isEmpty()) {
            safeTransaction = SafeTransactionResourceIT.createUpdatedEntity(em);
            em.persist(safeTransaction);
            em.flush();
        } else {
            safeTransaction = TestUtil.findAll(em, SafeTransaction.class).get(0);
        }
        signedTransaction.setSafeTransaction(safeTransaction);
        return signedTransaction;
    }

    @BeforeEach
    public void initTest() {
        signedTransaction = createEntity(em);
    }

    @Test
    @Transactional
    @Disabled
    void createSignedTransaction() throws Exception {
        int databaseSizeBeforeCreate = signedTransactionRepository.findAll().size();
        // Create the SignedTransaction
        restSignedTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isCreated());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        SignedTransaction testSignedTransaction = signedTransactionList.get(signedTransactionList.size() - 1);
        assertThat(testSignedTransaction.getSignedTx()).isEqualTo(DEFAULT_SIGNED_TX);
        assertThat(testSignedTransaction.getSalt()).isEqualTo(DEFAULT_SALT);
    }

    @Test
    @Transactional
    void createSignedTransactionWithExistingId() throws Exception {
        // Create the SignedTransaction with an existing ID
        signedTransaction.setId(1L);

        int databaseSizeBeforeCreate = signedTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSignedTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @Disabled
    void checkSignedTxIsRequired() throws Exception {
        int databaseSizeBeforeTest = signedTransactionRepository.findAll().size();
        // set the field null
        signedTransaction.setSignedTx(null);

        // Create the SignedTransaction, which fails.

        restSignedTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isBadRequest());

        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @Disabled
    void getAllSignedTransactions() throws Exception {
        // Initialize the database
        signedTransactionRepository.saveAndFlush(signedTransaction);

        // Get all the signedTransactionList
        restSignedTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(signedTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].signedTx").value(hasItem(DEFAULT_SIGNED_TX)))
            .andExpect(jsonPath("$.[*].salt").value(hasItem(DEFAULT_SALT)));
    }

    @Test
    @Transactional
    void getSignedTransaction() throws Exception {
        // Initialize the database
        signedTransactionRepository.saveAndFlush(signedTransaction);

        // Get the signedTransaction
        restSignedTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, signedTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(signedTransaction.getId().intValue()))
            .andExpect(jsonPath("$.signedTx").value(DEFAULT_SIGNED_TX))
            .andExpect(jsonPath("$.salt").value(DEFAULT_SALT));
    }

    @Test
    @Transactional
    void getNonExistingSignedTransaction() throws Exception {
        // Get the signedTransaction
        restSignedTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Disabled
    void putNewSignedTransaction() throws Exception {
        // Initialize the database
        signedTransactionRepository.saveAndFlush(signedTransaction);

        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();

        // Update the signedTransaction
        SignedTransaction updatedSignedTransaction = signedTransactionRepository.findById(signedTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedSignedTransaction are not directly saved in db
        em.detach(updatedSignedTransaction);
        updatedSignedTransaction.signedTx(UPDATED_SIGNED_TX).salt(UPDATED_SALT);

        restSignedTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSignedTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSignedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
        SignedTransaction testSignedTransaction = signedTransactionList.get(signedTransactionList.size() - 1);
        assertThat(testSignedTransaction.getSignedTx()).isEqualTo(UPDATED_SIGNED_TX);
        assertThat(testSignedTransaction.getSalt()).isEqualTo(UPDATED_SALT);
    }

    @Test
    @Transactional
    void putNonExistingSignedTransaction() throws Exception {
        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();
        signedTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSignedTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, signedTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSignedTransaction() throws Exception {
        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();
        signedTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSignedTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSignedTransaction() throws Exception {
        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();
        signedTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSignedTransactionMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSignedTransactionWithPatch() throws Exception {
        // Initialize the database
        signedTransactionRepository.saveAndFlush(signedTransaction);

        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();

        // Update the signedTransaction using partial update
        SignedTransaction partialUpdatedSignedTransaction = new SignedTransaction();
        partialUpdatedSignedTransaction.setId(signedTransaction.getId());

        restSignedTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSignedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSignedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
        SignedTransaction testSignedTransaction = signedTransactionList.get(signedTransactionList.size() - 1);
        assertThat(testSignedTransaction.getSignedTx()).isEqualTo(DEFAULT_SIGNED_TX);
        assertThat(testSignedTransaction.getSalt()).isEqualTo(DEFAULT_SALT);
    }

    @Test
    @Transactional
    void fullUpdateSignedTransactionWithPatch() throws Exception {
        // Initialize the database
        signedTransactionRepository.saveAndFlush(signedTransaction);

        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();

        // Update the signedTransaction using partial update
        SignedTransaction partialUpdatedSignedTransaction = new SignedTransaction();
        partialUpdatedSignedTransaction.setId(signedTransaction.getId());

        partialUpdatedSignedTransaction.signedTx(UPDATED_SIGNED_TX).salt(UPDATED_SALT);

        restSignedTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSignedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSignedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
        SignedTransaction testSignedTransaction = signedTransactionList.get(signedTransactionList.size() - 1);
        assertThat(testSignedTransaction.getSignedTx()).isEqualTo(UPDATED_SIGNED_TX);
        assertThat(testSignedTransaction.getSalt()).isEqualTo(UPDATED_SALT);
    }

    @Test
    @Transactional
    void patchNonExistingSignedTransaction() throws Exception {
        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();
        signedTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSignedTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, signedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSignedTransaction() throws Exception {
        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();
        signedTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSignedTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSignedTransaction() throws Exception {
        int databaseSizeBeforeUpdate = signedTransactionRepository.findAll().size();
        signedTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSignedTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(signedTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SignedTransaction in the database
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSignedTransaction() throws Exception {
        // Initialize the database
        signedTransactionRepository.saveAndFlush(signedTransaction);

        int databaseSizeBeforeDelete = signedTransactionRepository.findAll().size();

        // Delete the signedTransaction
        restSignedTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, signedTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SignedTransaction> signedTransactionList = signedTransactionRepository.findAll();
        assertThat(signedTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
