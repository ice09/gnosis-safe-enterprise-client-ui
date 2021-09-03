package tech.blockchainers.safe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static tech.blockchainers.safe.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import tech.blockchainers.safe.domain.GnosisSafe;
import tech.blockchainers.safe.domain.SafeTransaction;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.repository.SafeTransactionRepository;

/**
 * Integration tests for the {@link SafeTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SafeTransactionResourceIT {

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final Integer DEFAULT_VALUE = 1;
    private static final Integer UPDATED_VALUE = 2;

    private static final String DEFAULT_RECEIVER = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/safe-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SafeTransactionRepository safeTransactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSafeTransactionMockMvc;

    private SafeTransaction safeTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SafeTransaction createEntity(EntityManager em) {
        SafeTransaction safeTransaction = new SafeTransaction()
            .comment(DEFAULT_COMMENT)
            .token(DEFAULT_TOKEN)
            .value(DEFAULT_VALUE)
            .receiver(DEFAULT_RECEIVER)
            .created(DEFAULT_CREATED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        safeTransaction.setCreator(user);
        // Add required entity
        GnosisSafe gnosisSafe;
        if (TestUtil.findAll(em, GnosisSafe.class).isEmpty()) {
            gnosisSafe = GnosisSafeResourceIT.createEntity(em);
            em.persist(gnosisSafe);
            em.flush();
        } else {
            gnosisSafe = TestUtil.findAll(em, GnosisSafe.class).get(0);
        }
        safeTransaction.setGnosisSafe(gnosisSafe);
        return safeTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SafeTransaction createUpdatedEntity(EntityManager em) {
        SafeTransaction safeTransaction = new SafeTransaction()
            .comment(UPDATED_COMMENT)
            .token(UPDATED_TOKEN)
            .value(UPDATED_VALUE)
            .receiver(UPDATED_RECEIVER)
            .created(UPDATED_CREATED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        safeTransaction.setCreator(user);
        // Add required entity
        GnosisSafe gnosisSafe;
        if (TestUtil.findAll(em, GnosisSafe.class).isEmpty()) {
            gnosisSafe = GnosisSafeResourceIT.createUpdatedEntity(em);
            em.persist(gnosisSafe);
            em.flush();
        } else {
            gnosisSafe = TestUtil.findAll(em, GnosisSafe.class).get(0);
        }
        safeTransaction.setGnosisSafe(gnosisSafe);
        return safeTransaction;
    }

    @BeforeEach
    public void initTest() {
        safeTransaction = createEntity(em);
    }

    @Test
    @Transactional
    void createSafeTransaction() throws Exception {
        int databaseSizeBeforeCreate = safeTransactionRepository.findAll().size();
        // Create the SafeTransaction
        restSafeTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isCreated());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        SafeTransaction testSafeTransaction = safeTransactionList.get(safeTransactionList.size() - 1);
        assertThat(testSafeTransaction.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testSafeTransaction.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testSafeTransaction.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testSafeTransaction.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testSafeTransaction.getCreated()).isEqualTo(DEFAULT_CREATED);
    }

    @Test
    @Transactional
    void createSafeTransactionWithExistingId() throws Exception {
        // Create the SafeTransaction with an existing ID
        safeTransaction.setId(1L);

        int databaseSizeBeforeCreate = safeTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSafeTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = safeTransactionRepository.findAll().size();
        // set the field null
        safeTransaction.setValue(null);

        // Create the SafeTransaction, which fails.

        restSafeTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceiverIsRequired() throws Exception {
        int databaseSizeBeforeTest = safeTransactionRepository.findAll().size();
        // set the field null
        safeTransaction.setReceiver(null);

        // Create the SafeTransaction, which fails.

        restSafeTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = safeTransactionRepository.findAll().size();
        // set the field null
        safeTransaction.setCreated(null);

        // Create the SafeTransaction, which fails.

        restSafeTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @Disabled
    void getAllSafeTransactions() throws Exception {
        // Initialize the database
        safeTransactionRepository.saveAndFlush(safeTransaction);

        // Get all the safeTransactionList
        restSafeTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(safeTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))));
    }

    @Test
    @Transactional
    void getSafeTransaction() throws Exception {
        // Initialize the database
        safeTransactionRepository.saveAndFlush(safeTransaction);

        // Get the safeTransaction
        restSafeTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, safeTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(safeTransaction.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)));
    }

    @Test
    @Transactional
    void getNonExistingSafeTransaction() throws Exception {
        // Get the safeTransaction
        restSafeTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSafeTransaction() throws Exception {
        // Initialize the database
        safeTransactionRepository.saveAndFlush(safeTransaction);

        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();

        // Update the safeTransaction
        SafeTransaction updatedSafeTransaction = safeTransactionRepository.findById(safeTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedSafeTransaction are not directly saved in db
        em.detach(updatedSafeTransaction);
        updatedSafeTransaction
            .comment(UPDATED_COMMENT)
            .token(UPDATED_TOKEN)
            .value(UPDATED_VALUE)
            .receiver(UPDATED_RECEIVER)
            .created(UPDATED_CREATED);

        restSafeTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSafeTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSafeTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
        SafeTransaction testSafeTransaction = safeTransactionList.get(safeTransactionList.size() - 1);
        assertThat(testSafeTransaction.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testSafeTransaction.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testSafeTransaction.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testSafeTransaction.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testSafeTransaction.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingSafeTransaction() throws Exception {
        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();
        safeTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSafeTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, safeTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSafeTransaction() throws Exception {
        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();
        safeTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSafeTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSafeTransaction() throws Exception {
        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();
        safeTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSafeTransactionMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSafeTransactionWithPatch() throws Exception {
        // Initialize the database
        safeTransactionRepository.saveAndFlush(safeTransaction);

        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();

        // Update the safeTransaction using partial update
        SafeTransaction partialUpdatedSafeTransaction = new SafeTransaction();
        partialUpdatedSafeTransaction.setId(safeTransaction.getId());

        partialUpdatedSafeTransaction.token(UPDATED_TOKEN).value(UPDATED_VALUE).created(UPDATED_CREATED);

        restSafeTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSafeTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSafeTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
        SafeTransaction testSafeTransaction = safeTransactionList.get(safeTransactionList.size() - 1);
        assertThat(testSafeTransaction.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testSafeTransaction.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testSafeTransaction.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testSafeTransaction.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testSafeTransaction.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateSafeTransactionWithPatch() throws Exception {
        // Initialize the database
        safeTransactionRepository.saveAndFlush(safeTransaction);

        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();

        // Update the safeTransaction using partial update
        SafeTransaction partialUpdatedSafeTransaction = new SafeTransaction();
        partialUpdatedSafeTransaction.setId(safeTransaction.getId());

        partialUpdatedSafeTransaction
            .comment(UPDATED_COMMENT)
            .token(UPDATED_TOKEN)
            .value(UPDATED_VALUE)
            .receiver(UPDATED_RECEIVER)
            .created(UPDATED_CREATED);

        restSafeTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSafeTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSafeTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
        SafeTransaction testSafeTransaction = safeTransactionList.get(safeTransactionList.size() - 1);
        assertThat(testSafeTransaction.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testSafeTransaction.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testSafeTransaction.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testSafeTransaction.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testSafeTransaction.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingSafeTransaction() throws Exception {
        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();
        safeTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSafeTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, safeTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSafeTransaction() throws Exception {
        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();
        safeTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSafeTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSafeTransaction() throws Exception {
        int databaseSizeBeforeUpdate = safeTransactionRepository.findAll().size();
        safeTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSafeTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(safeTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SafeTransaction in the database
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSafeTransaction() throws Exception {
        // Initialize the database
        safeTransactionRepository.saveAndFlush(safeTransaction);

        int databaseSizeBeforeDelete = safeTransactionRepository.findAll().size();

        // Delete the safeTransaction
        restSafeTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, safeTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SafeTransaction> safeTransactionList = safeTransactionRepository.findAll();
        assertThat(safeTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
