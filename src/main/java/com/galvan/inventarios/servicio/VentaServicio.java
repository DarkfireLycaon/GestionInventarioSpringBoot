package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.DetalleVenta;
import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.modelo.Venta;
import com.galvan.inventarios.repositorio.VentaRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaServicio {
    @Autowired
    private VentaRepositorio ventaRepositorio;

    @Autowired
    private ProductoService productoServicio; // Para actualizar el stock


    @Transactional
    public Venta guardarVenta(Venta venta) {
        // Es VITAL asociar cada detalle con la venta padre
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(venta);
            }
        }
        return ventaRepositorio.save(venta);
    }

    public List<Venta> listarVentas() {
        return ventaRepositorio.findAll();
    }
    public List<Venta> listarVentasPorUsuario(Long usuarioId) {
        return ventaRepositorio.findByUsuarioId(usuarioId);
    }
}