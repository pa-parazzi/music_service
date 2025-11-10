package org.musicservice.demo.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;

import java.time.Instant;
import java.time.LocalDateTime;

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

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    public RefreshToken(){}

    public RefreshToken(String tokenHash, Instant expiryDate, Boolean revoked, User user) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.user = user;
    }
}
