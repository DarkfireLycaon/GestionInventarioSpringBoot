package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ProductoService implements IProductoServicio {
    @Autowired
    private ProductoRepositorio productoRepositorio;
    @Override
    public List<Producto> listarProductos() {
         return this.productoRepositorio.findAll();
    }

    @Override
    public Producto buscarProductoPorId(Integer idProducto) {
        Producto producto = this.productoRepositorio.findById(idProducto).orElse(null);
        return producto;
    }

    @Override
    public Producto guardarProducto(Producto producto) {
      return this.productoRepositorio.save(producto);
    }

    @Override
    public void eliminarProducto(Integer idProducto) {
     this.productoRepositorio.deleteById(idProducto);
    }

    public List<Producto> listarProductosPorUsuario(Long usuarioId) {
        return productoRepositorio.findByUsuarioId(usuarioId);
    }
}
