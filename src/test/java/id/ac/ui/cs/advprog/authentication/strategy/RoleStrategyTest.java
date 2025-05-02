package id.ac.ui.cs.advprog.authentication.strategy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleStrategyTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testAdminStrategyExecute() {
        RoleStrategy strategy = new AdminStrategy();
        strategy.execute();
        assertTrue(outContent.toString().contains("Admin-specific behavior executed."));
    }

    @Test
    void testKasirStrategyExecute() {
        RoleStrategy strategy = new KasirStrategy();
        strategy.execute();
        assertTrue(outContent.toString().contains("Kasir-specific behavior executed."));
    }
}
