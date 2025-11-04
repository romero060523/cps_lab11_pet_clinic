package com.tecsup.petclinic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.tecsup.petclinic.dtos.OwnerDTO;
import org.junit.jupiter.api.Test;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class OwnerServiceTest {

	@Autowired
	private OwnerService ownerService;

	/**
	 * Test find owner by ID - found case
	 */
	@Test
	public void testFindOwnerById_found() {

		String FIRST_NAME_EXPECTED = "George";
		String LAST_NAME_EXPECTED = "Franklin";

		Integer ID = 1;

		OwnerDTO owner = null;

		try {
			owner = this.ownerService.findById(ID);
		} catch (OwnerNotFoundException e) {
			fail(e.getMessage());
		}
		log.info("Owner found: " + owner);

		assertNotNull(owner);
		assertEquals(FIRST_NAME_EXPECTED, owner.getFirstName());
		assertEquals(LAST_NAME_EXPECTED, owner.getLastName());
	}

	/**
	 * Test find owner by ID - not found case
	 */
	@Test
	public void testFindOwnerById_notFound() {

		Integer NON_EXISTENT_ID = 99999;

		try {
			this.ownerService.findById(NON_EXISTENT_ID);
			// Si llegamos aquí, el test debe fallar porque esperábamos una excepción
			fail("Expected OwnerNotFoundException but owner was found");
		} catch (OwnerNotFoundException e) {
			// Esto es lo esperado - el owner no existe
			log.info("Owner not found (expected): " + e.getMessage());
			assertTrue(true);
		}
	}

	/**
	 * Test find owner by last name
	 */
	@Test
	public void testFindOwnerByLastName() {

		String FIND_LAST_NAME = "Davis";
		int SIZE_EXPECTED = 2; // Betty Davis and Harold Davis

		List<OwnerDTO> owners = this.ownerService.findByLastName(FIND_LAST_NAME);

		log.info("Owners found with last name '{}': {}", FIND_LAST_NAME, owners.size());
		owners.forEach(owner -> log.info("  - {}", owner));

		assertEquals(SIZE_EXPECTED, owners.size());
	}

	/**
	 * Test find owner by first name
	 */
	@Test
	public void testFindOwnerByFirstName() {

		String FIND_FIRST_NAME = "Maria";
		int SIZE_EXPECTED = 1;

		List<OwnerDTO> owners = this.ownerService.findByFirstName(FIND_FIRST_NAME);

		log.info("Owners found with first name '{}': {}", FIND_FIRST_NAME, owners.size());
		owners.forEach(owner -> log.info("  - {}", owner));

		assertEquals(SIZE_EXPECTED, owners.size());
		if (!owners.isEmpty()) {
			assertEquals(FIND_FIRST_NAME, owners.get(0).getFirstName());
		}
	}

	/**
	 * Test list all owners
	 */
	@Test
	public void testListOwners_success() {

		int MINIMUM_EXPECTED_SIZE = 10; // Based on data.sql, there are at least 10 owners

		List<Owner> owners = this.ownerService.findAll();

		log.info("Total owners found: {}", owners.size());
		owners.forEach(owner -> log.info("  - {}", owner));

		assertNotNull(owners);
		assertTrue(owners.size() >= MINIMUM_EXPECTED_SIZE, 
				"Expected at least " + MINIMUM_EXPECTED_SIZE + " owners from data.sql");
	}

	/**
	 * Test delete owner - success case
	 */
	@Test
	public void testDeleteOwner_success() {

		String FIRST_NAME = "TestOwner";
		String LAST_NAME = "TestDelete";
		String ADDRESS = "123 Test St.";
		String CITY = "Test City";
		String TELEPHONE = "5555555555";

		// ------------ Create ---------------

		OwnerDTO ownerDTO = OwnerDTO.builder()
				.firstName(FIRST_NAME)
				.lastName(LAST_NAME)
				.address(ADDRESS)
				.city(CITY)
				.telephone(TELEPHONE)
				.build();

		OwnerDTO newOwnerDTO = this.ownerService.create(ownerDTO);
		log.info("Owner created: " + newOwnerDTO);

		assertNotNull(newOwnerDTO.getId());
		assertEquals(FIRST_NAME, newOwnerDTO.getFirstName());
		assertEquals(LAST_NAME, newOwnerDTO.getLastName());

		Integer ownerId = newOwnerDTO.getId();

		// ------------ Delete ---------------

		try {
			this.ownerService.delete(ownerId);
			log.info("Owner deleted successfully");
		} catch (OwnerNotFoundException e) {
			fail(e.getMessage());
		}

		// ------------ Validation ---------------

		try {
			this.ownerService.findById(ownerId);
			// Si llegamos aquí, el owner aún existe, lo cual es un error
			fail("Owner should have been deleted but still exists");
		} catch (OwnerNotFoundException e) {
			// Esto es lo esperado - el owner fue eliminado correctamente
			log.info("Owner not found after deletion (expected): " + e.getMessage());
			assertTrue(true);
		}
	}

	/**
	 * Test delete owner - not found behavior
	 */
	@Test
	public void testDeleteOwner_notFound_behavior() {

		Integer NON_EXISTENT_ID = 99999;

		try {
			this.ownerService.delete(NON_EXISTENT_ID);
			// Si llegamos aquí, el test debe fallar porque esperábamos una excepción
			fail("Expected OwnerNotFoundException when deleting non-existent owner");
		} catch (OwnerNotFoundException e) {
			// Esto es lo esperado - no se puede eliminar un owner que no existe
			log.info("Owner not found for deletion (expected): " + e.getMessage());
			assertTrue(true);
		}
	}
}
