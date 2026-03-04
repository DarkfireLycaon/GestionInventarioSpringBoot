package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {
    List<Proveedor> findByUsuarioId(Long usuarioId);


}
