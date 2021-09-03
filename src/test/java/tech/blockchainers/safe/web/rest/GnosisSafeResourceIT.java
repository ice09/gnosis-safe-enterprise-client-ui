package tech.blockchainers.safe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tech.blockchainers.safe.IntegrationTest;
import tech.blockchainers.safe.domain.GnosisSafe;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.repository.GnosisSafeRepository;

/**
 * Integration tests for the {@link GnosisSafeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GnosisSafeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_SIGNATURES = 1;
    private static final Integer UPDATED_SIGNATURES = 2;

    private static final String ENTITY_API_URL = "/api/gnosis-safes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GnosisSafeRepository gnosisSafeRepository;

    @Mock
    private GnosisSafeRepository gnosisSafeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGnosisSafeMockMvc;

    private GnosisSafe gnosisSafe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GnosisSafe createEntity(EntityManager em) {
        GnosisSafe gnosisSafe = new GnosisSafe().name(DEFAULT_NAME).address(DEFAULT_ADDRESS).signatures(DEFAULT_SIGNATURES);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        gnosisSafe.getOwners().add(user);
        return gnosisSafe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GnosisSafe createUpdatedEntity(EntityManager em) {
        GnosisSafe gnosisSafe = new GnosisSafe().name(UPDATED_NAME).address(UPDATED_ADDRESS).signatures(UPDATED_SIGNATURES);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        gnosisSafe.getOwners().add(user);
        return gnosisSafe;
    }

    @BeforeEach
    public void initTest() {
        gnosisSafe = createEntity(em);
    }

    @Test
    @Transactional
    void createGnosisSafe() throws Exception {
        int databaseSizeBeforeCreate = gnosisSafeRepository.findAll().size();
        // Create the GnosisSafe
        restGnosisSafeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gnosisSafe)))
            .andExpect(status().isCreated());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeCreate + 1);
        GnosisSafe testGnosisSafe = gnosisSafeList.get(gnosisSafeList.size() - 1);
        assertThat(testGnosisSafe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGnosisSafe.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testGnosisSafe.getSignatures()).isEqualTo(DEFAULT_SIGNATURES);
    }

    @Test
    @Transactional
    void createGnosisSafeWithExistingId() throws Exception {
        // Create the GnosisSafe with an existing ID
        gnosisSafe.setId(1L);

        int databaseSizeBeforeCreate = gnosisSafeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGnosisSafeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gnosisSafe)))
            .andExpect(status().isBadRequest());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = gnosisSafeRepository.findAll().size();
        // set the field null
        gnosisSafe.setName(null);

        // Create the GnosisSafe, which fails.

        restGnosisSafeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gnosisSafe)))
            .andExpect(status().isBadRequest());

        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = gnosisSafeRepository.findAll().size();
        // set the field null
        gnosisSafe.setAddress(null);

        // Create the GnosisSafe, which fails.

        restGnosisSafeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gnosisSafe)))
            .andExpect(status().isBadRequest());

        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSignaturesIsRequired() throws Exception {
        int databaseSizeBeforeTest = gnosisSafeRepository.findAll().size();
        // set the field null
        gnosisSafe.setSignatures(null);

        // Create the GnosisSafe, which fails.

        restGnosisSafeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gnosisSafe)))
            .andExpect(status().isBadRequest());

        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @Disabled
    void getAllGnosisSafes() throws Exception {
        // Initialize the database
        gnosisSafeRepository.saveAndFlush(gnosisSafe);

        // Get all the gnosisSafeList
        restGnosisSafeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gnosisSafe.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].signatures").value(hasItem(DEFAULT_SIGNATURES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGnosisSafesWithEagerRelationshipsIsEnabled() throws Exception {
        when(gnosisSafeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGnosisSafeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(gnosisSafeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGnosisSafesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(gnosisSafeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGnosisSafeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(gnosisSafeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getGnosisSafe() throws Exception {
        // Initialize the database
        gnosisSafeRepository.saveAndFlush(gnosisSafe);

        // Get the gnosisSafe
        restGnosisSafeMockMvc
            .perform(get(ENTITY_API_URL_ID, gnosisSafe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gnosisSafe.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.signatures").value(DEFAULT_SIGNATURES));
    }

    @Test
    @Transactional
    void getNonExistingGnosisSafe() throws Exception {
        // Get the gnosisSafe
        restGnosisSafeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGnosisSafe() throws Exception {
        // Initialize the database
        gnosisSafeRepository.saveAndFlush(gnosisSafe);

        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();

        // Update the gnosisSafe
        GnosisSafe updatedGnosisSafe = gnosisSafeRepository.findById(gnosisSafe.getId()).get();
        // Disconnect from session so that the updates on updatedGnosisSafe are not directly saved in db
        em.detach(updatedGnosisSafe);
        updatedGnosisSafe.name(UPDATED_NAME).address(UPDATED_ADDRESS).signatures(UPDATED_SIGNATURES);

        restGnosisSafeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGnosisSafe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGnosisSafe))
            )
            .andExpect(status().isOk());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
        GnosisSafe testGnosisSafe = gnosisSafeList.get(gnosisSafeList.size() - 1);
        assertThat(testGnosisSafe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGnosisSafe.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testGnosisSafe.getSignatures()).isEqualTo(UPDATED_SIGNATURES);
    }

    @Test
    @Transactional
    void putNonExistingGnosisSafe() throws Exception {
        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();
        gnosisSafe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGnosisSafeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gnosisSafe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gnosisSafe))
            )
            .andExpect(status().isBadRequest());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGnosisSafe() throws Exception {
        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();
        gnosisSafe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGnosisSafeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gnosisSafe))
            )
            .andExpect(status().isBadRequest());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGnosisSafe() throws Exception {
        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();
        gnosisSafe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGnosisSafeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gnosisSafe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGnosisSafeWithPatch() throws Exception {
        // Initialize the database
        gnosisSafeRepository.saveAndFlush(gnosisSafe);

        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();

        // Update the gnosisSafe using partial update
        GnosisSafe partialUpdatedGnosisSafe = new GnosisSafe();
        partialUpdatedGnosisSafe.setId(gnosisSafe.getId());

        restGnosisSafeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGnosisSafe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGnosisSafe))
            )
            .andExpect(status().isOk());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
        GnosisSafe testGnosisSafe = gnosisSafeList.get(gnosisSafeList.size() - 1);
        assertThat(testGnosisSafe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGnosisSafe.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testGnosisSafe.getSignatures()).isEqualTo(DEFAULT_SIGNATURES);
    }

    @Test
    @Transactional
    void fullUpdateGnosisSafeWithPatch() throws Exception {
        // Initialize the database
        gnosisSafeRepository.saveAndFlush(gnosisSafe);

        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();

        // Update the gnosisSafe using partial update
        GnosisSafe partialUpdatedGnosisSafe = new GnosisSafe();
        partialUpdatedGnosisSafe.setId(gnosisSafe.getId());

        partialUpdatedGnosisSafe.name(UPDATED_NAME).address(UPDATED_ADDRESS).signatures(UPDATED_SIGNATURES);

        restGnosisSafeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGnosisSafe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGnosisSafe))
            )
            .andExpect(status().isOk());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
        GnosisSafe testGnosisSafe = gnosisSafeList.get(gnosisSafeList.size() - 1);
        assertThat(testGnosisSafe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGnosisSafe.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testGnosisSafe.getSignatures()).isEqualTo(UPDATED_SIGNATURES);
    }

    @Test
    @Transactional
    void patchNonExistingGnosisSafe() throws Exception {
        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();
        gnosisSafe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGnosisSafeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gnosisSafe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gnosisSafe))
            )
            .andExpect(status().isBadRequest());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGnosisSafe() throws Exception {
        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();
        gnosisSafe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGnosisSafeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gnosisSafe))
            )
            .andExpect(status().isBadRequest());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGnosisSafe() throws Exception {
        int databaseSizeBeforeUpdate = gnosisSafeRepository.findAll().size();
        gnosisSafe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGnosisSafeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(gnosisSafe))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GnosisSafe in the database
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGnosisSafe() throws Exception {
        // Initialize the database
        gnosisSafeRepository.saveAndFlush(gnosisSafe);

        int databaseSizeBeforeDelete = gnosisSafeRepository.findAll().size();

        // Delete the gnosisSafe
        restGnosisSafeMockMvc
            .perform(delete(ENTITY_API_URL_ID, gnosisSafe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GnosisSafe> gnosisSafeList = gnosisSafeRepository.findAll();
        assertThat(gnosisSafeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
