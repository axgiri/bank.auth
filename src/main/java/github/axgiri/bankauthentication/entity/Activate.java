package github.axgiri.bankauthentication.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "active")
public class Activate {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "otp", nullable = false)
    private int otp;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_login_attempted", nullable = true)
    private boolean isLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
