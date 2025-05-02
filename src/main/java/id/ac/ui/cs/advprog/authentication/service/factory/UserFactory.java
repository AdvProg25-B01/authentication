package id.ac.ui.cs.advprog.authentication.service.factory;

import id.ac.ui.cs.advprog.authentication.model.User;
import id.ac.ui.cs.advprog.authentication.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createUser(String name, String email, String password, Role role) {
    }
}