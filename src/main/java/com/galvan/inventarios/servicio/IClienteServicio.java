package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Cliente;

import java.util.List;

public interface IClienteServicio {
    List<Cliente> listarClientes();
    Cliente buscarClientePorId(Integer idCliente);
    Cliente buscarClientePorNombre(String nombre);
    Cliente buscarClientePorCodigo(Integer codigo);
    Cliente guardarCliente(Cliente cliente);
    void eliminarCliente(Integer idCliente);
}
