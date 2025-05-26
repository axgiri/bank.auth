package github.axgiri.bankauthentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import github.axgiri.bankauthentication.entity.Activate;

public interface ActivateRepository extends JpaRepository<Activate, Long>{
    
    Optional<Activate> findByPhoneNumber(String phoneNumber);

    Activate setActiveByPhoneNumber(String phoneNumber, boolean isActive);

    Optional<Activate> findByPhoneNumberAndIsLogin(String phoneNumber, boolean isLogin);
}
