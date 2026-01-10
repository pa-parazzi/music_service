package org.musicservice.demo.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.entity.user.User;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token_hash")
    private String tokenHash;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(name = "revoked")
    private Boolean revoked;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_role")
    private Authority role;

    public RefreshToken(){}

    public RefreshToken(String tokenHash, Instant expiryDate, Long userId, Authority role) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
        this.userId = userId;
        this.role = role;
    }
}
