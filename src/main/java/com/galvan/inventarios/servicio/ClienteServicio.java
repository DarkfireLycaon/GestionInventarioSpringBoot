package com.galvan.inventarios.servicio;


import com.galvan.inventarios.modelo.Cliente;
import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.repositorio.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServicio implements IClienteServicio {
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    public List<Cliente> listarClientes(){
        return clienteRepositorio.findAll();
    }

    @Override
    public Cliente buscarClientePorId(Integer idCliente) {
        Cliente cliente = clienteRepositorio.findById(idCliente).orElse(null);
        return cliente;
    }

    @Override
    public Cliente buscarClientePorNombre(String nombre) {
        Cliente cliente = clienteRepositorio.findByNombre(nombre);
        return cliente;
    }

    @Override
    public Cliente buscarClientePorCodigo(Integer codigo) {
        Cliente cliente = clienteRepositorio.findById(codigo).orElse(null);
        return cliente;
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {
      return clienteRepositorio.save(cliente);
    }

    @Override
    public void eliminarCliente(Integer idCliente) {
this.clienteRepositorio.deleteById(idCliente);
    }
    public List<Cliente> listarClientesPorUsuario(Long usuarioId) {
        return clienteRepositorio.findByUsuarioId(usuarioId);
    }
}
