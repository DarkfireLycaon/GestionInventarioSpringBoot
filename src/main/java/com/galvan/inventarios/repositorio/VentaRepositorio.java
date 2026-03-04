package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VentaRepositorio extends JpaRepository<Venta,Integer> {
    List<Venta> findByUsuarioId(Long usuarioId);


}
