package org.musicservice.demo.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime expiryDate;

    @Column(name = "revoked")
    private Boolean revoked;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    public RefreshToken(){}

    public RefreshToken(String tokenHash, LocalDateTime expiryDate, Boolean revoked, User user) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.user = user;
    }
}
