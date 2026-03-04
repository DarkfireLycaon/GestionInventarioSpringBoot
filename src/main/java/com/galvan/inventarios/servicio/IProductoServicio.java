package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Producto;

import java.util.List;

public interface IProductoServicio {
    List<Producto> listarProductos();
    Producto buscarProductoPorId(Integer idProducto);

    Producto guardarProducto(Producto idProducto);

    void eliminarProducto(Integer idProducto);

}
