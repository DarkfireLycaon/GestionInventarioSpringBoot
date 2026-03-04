package com.galvan.inventarios.repositorio;

import com.galvan.inventarios.modelo.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepositorio   extends JpaRepository<DetalleVenta, Integer> {
}
