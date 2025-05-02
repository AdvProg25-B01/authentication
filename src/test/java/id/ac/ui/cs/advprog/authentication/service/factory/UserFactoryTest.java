package id.ac.ui.cs.advprog.authentication.service.factory;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import id.ac.ui.cs.advprog.authentication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserFactoryTest {

    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        userFactory = new UserFactory();
    }

    @Test
    void testCreateUser_shouldReturnUserWithCorrectFields() {
        // Arrange
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "securePassword123";
        Role role = Role.ADMIN;

        // Act
        User user = userFactory.createUser(name, email, password, role);

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.isActive()).isTrue();
    }
}
