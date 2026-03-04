package com.galvan.inventarios.servicio;


import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.modelo.Proveedor;
import com.galvan.inventarios.repositorio.ProductoRepositorio;
import com.galvan.inventarios.repositorio.ProveedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.galvan.inventarios.exepcion.RecursoNoEncontradoExepcion;
import java.util.List;

@Service
public class ProveedorServicio implements IProveedorServicio {
    @Autowired
    private ProveedorRepositorio proveedorRepositorio;
    @Autowired // Te falta esta inyección
    private ProductoRepositorio productoRepositorio;

    @Override
    public List<Proveedor> listarProveedores() {
        return this.proveedorRepositorio.findAll();
    }

    @Override
    public Proveedor buscarProveedorPorId(Integer id) {
        Proveedor proveedor = this.proveedorRepositorio.findById(id).orElse(null);
        return proveedor;
    }

    @Override
    public Proveedor guardarProveedor(Proveedor proveedor) {
        return this.proveedorRepositorio.save(proveedor);
    }

    @Override
    public void eliminarProveedor(Integer id) {
        this.proveedorRepositorio.deleteById(id);
    }

    public Proveedor agregarProductoAProveedor(
            Integer proveedorId,
            Integer productoId,
            Long usuarioId) { // Pasamos el ID del usuario logueado

        Proveedor proveedor = proveedorRepositorio.findById(proveedorId)
                .orElseThrow(() -> new RecursoNoEncontradoExepcion("Proveedor no encontrado"));

        Producto producto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoExepcion("Producto no encontrado"));

        // VALIDACIÓN DE SEGURIDAD:
        if (!proveedor.getUsuario().getId().equals(usuarioId) ||
                !producto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para vincular estos recursos.");
        }

        proveedor.getProductos().add(producto);
        return proveedorRepositorio.save(proveedor);
    }
    @Override
    public List<Proveedor> listarProveedoresPorUsuario(Long usuarioId) {
        return proveedorRepositorio.findByUsuarioId(usuarioId);
    }
}
