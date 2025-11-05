package com.tecsup.petclinic.services;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for OwnerService
 * 
 * Implemented: CREATE/UPDATE using JDBC
 * Pending (empty): FIND/DELETE (waiting for OwnerService implementation)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class OwnerServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // @Autowired
    // private OwnerService ownerService;  // TODO: Uncomment when OwnerService is implemented

    private SimpleJdbcInsert ownerInsert;

    @BeforeEach
    public void setUp() {
        this.ownerInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("owners")
                .usingGeneratedKeyColumns("id");
    }

    // ========== CREATE TESTS (JDBC-based) ==========
    
    /**
     * Prueba para crear un dueño usando JDBC
     * Verifica que se pueda crear un nuevo dueño correctamente
     */
    @Test
    public void testCreateOwner() {
        // Arrange
        String firstName = "Juan";
        String lastName = "Perez";
        String address = "Calle 1";
        String city = "Lima";
        String phone = "999999999";

        // Act
        Integer newId = insertOwner(firstName, lastName, address, city, phone);

        // Assert
        assertNotNull(newId, "ID generado no debe ser nulo");
        OwnerRow row = findOwnerByIdInternal(newId);
        assertEquals(firstName, row.firstName);
        assertEquals(lastName, row.lastName);
        assertEquals(address, row.address);
        assertEquals(city, row.city);
        assertEquals(phone, row.telephone);
    }

    // ========== UPDATE TESTS (JDBC-based) ==========
    
    /**
     * Prueba para actualizar un dueño usando JDBC
     * Verifica que se pueda actualizar la información de un dueño existente
     */
    @Test
    public void testUpdateOwner() {
        // Arrange: crear primero
        Integer id = insertOwner("Ana", "Diaz", "Av 10", "Cusco", "987654321");

        String upFirstName = "Ana";        // sin cambio
        String upLastName = "Diaz Soto";   // cambio
        String upAddress = "Av 10";        // sin cambio
        String upCity = "Arequipa";        // cambio
        String upPhone = "987654321";      // sin cambio

        // Act: actualizar
        int updated = jdbcTemplate.update(
                "UPDATE owners SET first_name=?, last_name=?, address=?, city=?, telephone=? WHERE id=?",
                upFirstName, upLastName, upAddress, upCity, upPhone, id
        );

        // Assert
        assertEquals(1, updated, "Debe actualizar exactamente 1 registro");
        OwnerRow row = findOwnerByIdInternal(id);
        assertEquals(upFirstName, row.firstName);
        assertEquals(upLastName, row.lastName);
        assertEquals(upAddress, row.address);
        assertEquals(upCity, row.city);
        assertEquals(upPhone, row.telephone);
    }

    // ========== FIND TESTS (JDBC-based) ==========
    
    /**
     * Prueba para buscar un dueño por ID
     * Verifica que se pueda encontrar un dueño por su identificador usando JDBC
     */
    @Test
    public void testFindOwnerById() {
        // Arrange: usar un owner existente de data.sql (ID=1 es George Franklin)
        Integer ID = 1;
        String FIRST_NAME_EXPECTED = "George";
        String LAST_NAME_EXPECTED = "Franklin";

        // Act
        OwnerRow owner = findOwnerByIdInternal(ID);

        // Assert
        assertNotNull(owner, "Owner no debe ser nulo");
        assertEquals(ID, owner.id);
        assertEquals(FIRST_NAME_EXPECTED, owner.firstName);
        assertEquals(LAST_NAME_EXPECTED, owner.lastName);
        log.info("Owner found: {}", owner);
    }

    /**
     * Prueba para buscar un dueño por ID - caso no encontrado
     * Verifica que retorne null o lance excepción cuando el ID no existe
     */
    @Test
    public void testFindOwnerById_notFound() {
        // Arrange
        Integer NON_EXISTENT_ID = 99999;

        // Act & Assert
        try {
            OwnerRow owner = findOwnerByIdInternal(NON_EXISTENT_ID);
            fail("Se esperaba una excepción al buscar un owner inexistente, pero se encontró: " + owner);
        } catch (Exception e) {
            // Esperado: EmptyResultDataAccessException o similar
            log.info("Owner no encontrado (esperado): {}", e.getMessage());
            assertTrue(true);
        }
    }

    // ========== DELETE TESTS (JDBC-based) ==========
    
    /**
     * Prueba para eliminar un dueño
     * Verifica que se pueda eliminar un dueño correctamente usando JDBC
     */
    @Test
    public void testDeleteOwner() {
        // Arrange: crear un owner primero
        String firstName = "TestOwner";
        String lastName = "TestDelete";
        String address = "123 Test St.";
        String city = "Test City";
        String telephone = "5555555555";

        Integer ownerId = insertOwner(firstName, lastName, address, city, telephone);
        assertNotNull(ownerId);
        assertTrue(existsOwnerById(ownerId), "Owner debe existir antes de eliminar");

        // Act: eliminar
        int deleted = jdbcTemplate.update("DELETE FROM owners WHERE id = ?", ownerId);

        // Assert
        assertEquals(1, deleted, "Debe eliminar exactamente 1 registro");
        assertFalse(existsOwnerById(ownerId), "Owner no debe existir después de eliminarse");
        log.info("Owner with ID {} deleted successfully", ownerId);
    }

    /**
     * Prueba para eliminar un dueño - caso ID no existe
     * Verifica que no se elimine nada cuando el ID no existe
     */
    @Test
    public void testDeleteOwner_notFound() {
        // Arrange
        Integer NON_EXISTENT_ID = 99999;

        // Act
        int deleted = jdbcTemplate.update("DELETE FROM owners WHERE id = ?", NON_EXISTENT_ID);

        // Assert
        assertEquals(0, deleted, "No debe eliminar ningún registro cuando el ID no existe");
        log.info("Delete attempt on non-existent owner (expected 0 rows affected)");
    }

    // ========== HELPERS (JDBC internals) ==========
    
    private Integer insertOwner(String firstName, String lastName, String address, String city, String phone) {
        Map<String, Object> params = new HashMap<>();
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("address", address);
        params.put("city", city);
        params.put("telephone", phone);
        Number key = ownerInsert.executeAndReturnKey(params);
        return (key != null) ? key.intValue() : null;
    }

    private OwnerRow findOwnerByIdInternal(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id = ?",
                ownerRowMapper,
                id
        );
    }

    private boolean existsOwnerById(Integer id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM owners WHERE id = ?",
                Integer.class,
                id
        );
        return count != null && count > 0;
    }

    private final RowMapper<OwnerRow> ownerRowMapper = new RowMapper<>() {
        @Override
        public OwnerRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OwnerRow(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("telephone")
            );
        }
    };

    private record OwnerRow(Integer id, String firstName, String lastName, String address, String city, String telephone) {}
}
