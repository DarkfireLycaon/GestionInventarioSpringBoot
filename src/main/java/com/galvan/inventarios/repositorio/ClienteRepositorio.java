package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    Cliente findByNombre(String nombre);
    List<Cliente> findByUsuarioId(Long usuarioId);

}
