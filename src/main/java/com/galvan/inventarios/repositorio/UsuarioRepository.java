package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCodigoConfirmacion(String codigo);
    boolean existsByEmail(String email);
    // Para la recuperación de contraseña (olvidé mi clave)
    Optional<Usuario> findByResetToken(String resetToken);

}