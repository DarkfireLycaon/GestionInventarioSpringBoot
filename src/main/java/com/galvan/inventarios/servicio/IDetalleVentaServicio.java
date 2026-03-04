package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Venta;

import java.util.List;

public interface IDetalleVentaServicio {
    public List<Venta> listarVentas();
    public List<Venta> listarVentas(String codigo);

}
