package com.tecsup.petclinic.services;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for OwnerService
 *
 * Implemented: create and update tests
 * Pending (empty): findById and delete
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OwnerServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert ownerInsert;

    /**
     * Initialize test data (fixtures)
     * Este método se ejecuta antes de cada test
     */
    @BeforeEach
    public void setUp() {
        this.ownerInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("owners")
                .usingGeneratedKeyColumns("id");
    }

    // region CREATE TESTS
    /**
     * Prueba para crear un dueño
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
    // endregion

    // region UPDATE TESTS
    /**
     * Prueba para actualizar un dueño
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
    // endregion

	

    // region FIND TESTS (empty)
    /**
     * Prueba para buscar un dueño por ID
     * Verifica que se pueda encontrar un dueño por su identificador
     */
    @Test
    public void testFindOwnerById() {
        // TODO: Implementar prueba para buscar dueño por ID
    }
    // endregion

    // region DELETE TESTS (empty)
    /**
     * Prueba para eliminar un dueño
     * Verifica que se pueda eliminar un dueño correctamente
     */
    @Test
    public void testDeleteOwner() {
        // TODO: Implementar prueba para eliminar dueño
    }
    // endregion




    // === HELPERS (internos) ===
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
