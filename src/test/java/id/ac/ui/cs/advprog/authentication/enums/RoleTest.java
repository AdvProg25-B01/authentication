package id.ac.ui.cs.advprog.authentication.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleTest {

    @Test
    void testRoleValues() {
        assertEquals("KASIR", Role.KASIR.name());
        assertEquals("ADMIN", Role.ADMIN.name());
    }
}