package rs.ac.uns.ftn.iss.Komsiluk.beans.enums;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

public class UserToken {

	private Long id;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
    private TokenType type;
	private User user;

    public UserToken() {
        super();
    }

    public UserToken(Long id, String token, LocalDateTime expiresAt, boolean used, TokenType type, User user) {
        this.id = id;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = used;
        this.type = type;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
