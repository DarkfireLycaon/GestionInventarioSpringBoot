package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Proveedor;

import java.util.List;

public interface IProveedorServicio {
    List<Proveedor> listarProveedores();

    Proveedor buscarProveedorPorId(Integer id);

    Proveedor guardarProveedor(Proveedor proveedor);

    void eliminarProveedor(Integer id);

    List<Proveedor> listarProveedoresPorUsuario(Long usuarioId);

}
