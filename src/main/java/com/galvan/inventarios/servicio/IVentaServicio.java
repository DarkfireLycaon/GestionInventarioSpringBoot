package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Venta;

import java.util.List;

public interface IVentaServicio {
    public List<Venta> listarVentas();
    public Venta guardarVenta(Venta venta);

}
