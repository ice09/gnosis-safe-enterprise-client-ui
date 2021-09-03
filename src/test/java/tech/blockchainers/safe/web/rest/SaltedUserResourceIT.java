package tech.blockchainers.safe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;

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
import tech.blockchainers.safe.domain.SaltedUser;
import tech.blockchainers.safe.domain.User;
import tech.blockchainers.safe.repository.SaltedUserRepository;

/**
 * Integration tests for the {@link SaltedUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaltedUserResourceIT {

    private static final String DEFAULT_SALT = "AAAAAAAAAA";
    private static final String UPDATED_SALT = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/salted-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SaltedUserRepository saltedUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaltedUserMockMvc;

    private SaltedUser saltedUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaltedUser createEntity(EntityManager em) {
        SaltedUser saltedUser = new SaltedUser().salt(DEFAULT_SALT).address(DEFAULT_ADDRESS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        saltedUser.setUser(user);
        return saltedUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaltedUser createUpdatedEntity(EntityManager em) {
        SaltedUser saltedUser = new SaltedUser().salt(UPDATED_SALT).address(UPDATED_ADDRESS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        saltedUser.setUser(user);
        return saltedUser;
    }

    @BeforeEach
    public void initTest() {
        saltedUser = createEntity(em);
    }

    @Test
    @Transactional
    @Disabled
    void createSaltedUser() throws Exception {
        int databaseSizeBeforeCreate = saltedUserRepository.findAll().size();
        // Create the SaltedUser
        restSaltedUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saltedUser)))
            .andExpect(status().isCreated());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeCreate + 1);
        SaltedUser testSaltedUser = saltedUserList.get(saltedUserList.size() - 1);
        assertThat(testSaltedUser.getSalt()).isEqualTo(DEFAULT_SALT);
        assertThat(testSaltedUser.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void createSaltedUserWithExistingId() throws Exception {
        // Create the SaltedUser with an existing ID
        saltedUser.setId(1L);

        int databaseSizeBeforeCreate = saltedUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaltedUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saltedUser)))
            .andExpect(status().isBadRequest());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @Disabled
    void getAllSaltedUsers() throws Exception {
        // Initialize the database
        saltedUserRepository.saveAndFlush(saltedUser);

        // Get all the saltedUserList
        restSaltedUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saltedUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].salt").value(hasItem(DEFAULT_SALT)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    @Test
    @Transactional
    void getSaltedUser() throws Exception {
        // Initialize the database
        saltedUserRepository.saveAndFlush(saltedUser);

        // Get the saltedUser
        restSaltedUserMockMvc
            .perform(get(ENTITY_API_URL_ID, saltedUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saltedUser.getId().intValue()))
            .andExpect(jsonPath("$.salt").value(DEFAULT_SALT))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }

    @Test
    @Transactional
    void getNonExistingSaltedUser() throws Exception {
        // Get the saltedUser
        restSaltedUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Disabled
    void putNewSaltedUser() throws Exception {
        // Initialize the database
        saltedUserRepository.saveAndFlush(saltedUser);

        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();

        // Update the saltedUser
        SaltedUser updatedSaltedUser = saltedUserRepository.findById(saltedUser.getId()).get();
        // Disconnect from session so that the updates on updatedSaltedUser are not directly saved in db
        em.detach(updatedSaltedUser);
        updatedSaltedUser.salt(UPDATED_SALT).address(UPDATED_ADDRESS);

        restSaltedUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSaltedUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSaltedUser))
            )
            .andExpect(status().isOk());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
        SaltedUser testSaltedUser = saltedUserList.get(saltedUserList.size() - 1);
        assertThat(testSaltedUser.getSalt()).isEqualTo(UPDATED_SALT);
        assertThat(testSaltedUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void putNonExistingSaltedUser() throws Exception {
        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();
        saltedUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaltedUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saltedUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saltedUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaltedUser() throws Exception {
        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();
        saltedUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaltedUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saltedUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaltedUser() throws Exception {
        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();
        saltedUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaltedUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saltedUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @Disabled
    void partialUpdateSaltedUserWithPatch() throws Exception {
        // Initialize the database
        saltedUserRepository.saveAndFlush(saltedUser);

        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();

        // Update the saltedUser using partial update
        SaltedUser partialUpdatedSaltedUser = new SaltedUser();
        partialUpdatedSaltedUser.setId(saltedUser.getId());

        partialUpdatedSaltedUser.salt(UPDATED_SALT).address(UPDATED_ADDRESS);

        restSaltedUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaltedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSaltedUser))
            )
            .andExpect(status().isOk());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
        SaltedUser testSaltedUser = saltedUserList.get(saltedUserList.size() - 1);
        assertThat(testSaltedUser.getSalt()).isEqualTo(UPDATED_SALT);
        assertThat(testSaltedUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    @Disabled
    void fullUpdateSaltedUserWithPatch() throws Exception {
        // Initialize the database
        saltedUserRepository.saveAndFlush(saltedUser);

        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();

        // Update the saltedUser using partial update
        SaltedUser partialUpdatedSaltedUser = new SaltedUser();
        partialUpdatedSaltedUser.setId(saltedUser.getId());

        partialUpdatedSaltedUser.salt(UPDATED_SALT).address(UPDATED_ADDRESS);

        restSaltedUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaltedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSaltedUser))
            )
            .andExpect(status().isOk());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
        SaltedUser testSaltedUser = saltedUserList.get(saltedUserList.size() - 1);
        //assertThat(testSaltedUser.getSalt()).isEqualTo(UPDATED_SALT);
        assertThat(testSaltedUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void patchNonExistingSaltedUser() throws Exception {
        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();
        saltedUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaltedUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saltedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(saltedUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaltedUser() throws Exception {
        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();
        saltedUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaltedUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(saltedUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaltedUser() throws Exception {
        int databaseSizeBeforeUpdate = saltedUserRepository.findAll().size();
        saltedUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaltedUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(saltedUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaltedUser in the database
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaltedUser() throws Exception {
        // Initialize the database
        saltedUserRepository.saveAndFlush(saltedUser);

        int databaseSizeBeforeDelete = saltedUserRepository.findAll().size();

        // Delete the saltedUser
        restSaltedUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, saltedUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SaltedUser> saltedUserList = saltedUserRepository.findAll();
        assertThat(saltedUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
