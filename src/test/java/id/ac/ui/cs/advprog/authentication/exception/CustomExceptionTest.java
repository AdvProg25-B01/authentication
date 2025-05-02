package id.ac.ui.cs.advprog.authentication.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomExceptionTest {

    @Test
    void testCustomExceptionMessage() {
        String errorMessage = "Something went wrong";

        CustomException exception = assertThrows(CustomException.class, () -> {
            throw new CustomException(errorMessage);
        });

        assertEquals(errorMessage, exception.getMessage());
    }
}
