package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    List<Producto> findByUsuarioId(Long usuarioId);


}
