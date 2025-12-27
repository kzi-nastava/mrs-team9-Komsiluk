package rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.TokenType;

public class UserTokenResponseDTO {

	private Long id;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
    private TokenType type;
    private Long userId;

    public UserTokenResponseDTO() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
