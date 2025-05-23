package id.ac.ui.cs.advprog.authentication.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenRequestTest {

    @Test
    public void testRefreshTokenGetterAndSetter() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        String expectedToken = "sample-refresh-token";

        request.setRefreshToken(expectedToken);

        assertEquals(expectedToken, request.getRefreshToken(), "Refresh token should match the one set.");
    }

    @Test
    public void testEqualsAndHashCode() {
        RefreshTokenRequest request1 = new RefreshTokenRequest();
        RefreshTokenRequest request2 = new RefreshTokenRequest();
        String token = "token-123";

        request1.setRefreshToken(token);
        request2.setRefreshToken(token);

        assertEquals(request1, request2, "Objects with same refreshToken should be equal.");
        assertEquals(request1.hashCode(), request2.hashCode(), "Hash codes should match for equal objects.");
    }

    @Test
    public void testToString() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("token-xyz");

        String result = request.toString();
        assertTrue(result.contains("token-xyz"), "toString should contain the refresh token value.");
    }
}
