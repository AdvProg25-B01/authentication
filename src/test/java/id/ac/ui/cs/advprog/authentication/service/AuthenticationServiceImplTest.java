package id.ac.ui.cs.advprog.authentication.service; 

import id.ac.ui.cs.advprog.authentication.dto.AuthRequest;
import id.ac.ui.cs.advprog.authentication.dto.AuthResponse;
import id.ac.ui.cs.advprog.authentication.dto.KasirRegistrationDTO;
import id.ac.ui.cs.advprog.authentication.model.Administrator;
import id.ac.ui.cs.advprog.authentication.model.Kasir;
import id.ac.ui.cs.advprog.authentication.repository.AdministratorRepository;
import id.ac.ui.cs.advprog.authentication.repository.KasirRepository;
import id.ac.ui.cs.advprog.authentication.security.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceImplTest {

    private AdministratorRepository adminRepo;
    private KasirRepository kasirRepo;
    private JWTTokenProvider jwtProvider;
    private AuthenticationServiceImpl authService;

    @BeforeEach
    void setUp() {
        adminRepo    = mock(AdministratorRepository.class);
        kasirRepo    = mock(KasirRepository.class);
        jwtProvider  = mock(JWTTokenProvider.class);
        authService  = new AuthenticationServiceImpl(adminRepo, kasirRepo, jwtProvider);
    }

    @Test
    void testLogin_AdminSuccess() throws Exception {
        String email       = "admin@buildstore.com";
        String rawPassword = "secret123";
        String hashed      = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        Administrator admin = new Administrator();
        admin.setId(UUID.randomUUID().toString());
        admin.setFullName("Super Admin");
        admin.setEmail(email);
        admin.setPassword(hashed);

        when(adminRepo.findByEmail(email)).thenReturn(Optional.of(admin));
        when(jwtProvider.generateToken(email, "ADMIN"))
                .thenReturn("token-admin-xyz");

        AuthRequest req = new AuthRequest();
        req.setEmail(email);
        req.setPassword(rawPassword);

        AuthResponse resp = authService.login(req);
        assertNotNull(resp);
        assertEquals("token-admin-xyz", resp.getToken());
    }

    @Test
    void testLogin_CashierSuccess() throws Exception {
        String email       = "kasir@buildstore.com";
        String rawPassword = "kasirpass";
        String hashed      = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        Kasir kasir = new Kasir();
        kasir.setId(UUID.randomUUID().toString());
        kasir.setFullName("Kasir Toko");
        kasir.setEmail(email);
        kasir.setPassword(hashed);

        when(adminRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(kasirRepo.findByEmail(email)).thenReturn(Optional.of(kasir));
        when(jwtProvider.generateToken(email, "CASHIER"))
                .thenReturn("token-kasir-abc");

        AuthRequest req = new AuthRequest();
        req.setEmail(email);
        req.setPassword(rawPassword);

        AuthResponse resp = authService.login(req);
        assertNotNull(resp);
        assertEquals("token-kasir-abc", resp.getToken());
    }

    @Test
    void testLogin_InvalidPassword() {
        String email       = "admin@buildstore.com";
        String rawPassword = "wrongpass";
        String hashed      = BCrypt.hashpw("rightpass", BCrypt.gensalt());
        Administrator admin = new Administrator();
        admin.setId(UUID.randomUUID().toString());
        admin.setFullName("Super Admin");
        admin.setEmail(email);
        admin.setPassword(hashed);

        when(adminRepo.findByEmail(email)).thenReturn(Optional.of(admin));

        AuthRequest req = new AuthRequest();
        req.setEmail(email);
        req.setPassword(rawPassword);

        Exception ex = assertThrows(Exception.class, () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Invalid password"));
    }

    @Test
    void testLogin_NoAccountFound() {
        String email = "nobody@buildstore.com";

        when(adminRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(kasirRepo.findByEmail(email)).thenReturn(Optional.empty());

        AuthRequest req = new AuthRequest();
        req.setEmail(email);
        req.setPassword("any");

        Exception ex = assertThrows(Exception.class, () -> authService.login(req));
        assertTrue(ex.getMessage().contains("No account found"));
    }

    @Test
    void testRegisterKasir() {
        KasirRegistrationDTO dto = new KasirRegistrationDTO();
        dto.setFullName("Kasir Baru");
        dto.setEmail("newkasir@buildstore.com");
        dto.setPhoneNumber("08123456789");
        dto.setPassword("newpass");

        // Simulasi saved entity dengan ID baru
        Kasir saved = new Kasir();
        saved.setId(UUID.randomUUID().toString());
        saved.setFullName(dto.getFullName());
        saved.setEmail(dto.getEmail());
        saved.setPhoneNumber(dto.getPhoneNumber());
        saved.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));

        when(kasirRepo.save(any(Kasir.class))).thenReturn(saved);

        Kasir result = authService.registerKasir(dto);
        assertNotNull(result);
        assertEquals("Kasir Baru", result.getFullName());
        assertEquals(dto.getEmail(), result.getEmail());
    }

}
