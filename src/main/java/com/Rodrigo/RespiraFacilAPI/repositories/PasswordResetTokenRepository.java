package com.Rodrigo.RespiraFacilAPI.repositories;
import com.Rodrigo.RespiraFacilAPI.entities.PasswordResetToken;
import com.Rodrigo.RespiraFacilAPI.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
