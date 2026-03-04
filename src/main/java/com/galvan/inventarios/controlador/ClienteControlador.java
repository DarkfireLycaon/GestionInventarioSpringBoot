package com.galvan.inventarios.controlador;

import com.galvan.inventarios.exepcion.RecursoNoEncontradoExepcion;
import com.galvan.inventarios.modelo.Cliente;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepository;
import com.galvan.inventarios.servicio.ClienteServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Necesario para el usuario actual
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("inventario-app")
@CrossOrigin(value = "http://localhost:4200")
public class ClienteControlador {
    private static final Logger log = LoggerFactory.getLogger(ClienteControlador.class);

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/clientes")
    public List<Cliente> obtenerClientes(Authentication authentication) {
        Usuario usuario = obtenerUsuarioLogueado(authentication);
        // Filtramos para que solo vea sus propios clientes
        List<Cliente> clientes = this.clienteServicio.listarClientesPorUsuario(usuario.getId());
        log.info("Listado de clientes para el usuario {}: {}", usuario.getEmail(), clientes.size());
        return clientes;
    }

    @PostMapping("/clientes")
    public Cliente crearCliente(@RequestBody Cliente cliente, Authentication authentication) {
        Usuario usuario = obtenerUsuarioLogueado(authentication);
        String email = authentication.getName();
        // Asignamos el dueño antes de guardar
        cliente.setUsuario(usuario);
        return this.clienteServicio.guardarCliente(cliente);
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable("id") Integer id, Authentication authentication) {
        Cliente cliente = this.clienteServicio.buscarClientePorId(id);
        validarPropiedad(cliente, authentication);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable("id") Integer id, @RequestBody Cliente clienteBuscado, Authentication authentication) {
        Cliente cliente = this.clienteServicio.buscarClientePorId(id);

        validarPropiedad(cliente, authentication);

        cliente.setNombre(clienteBuscado.getNombre());
        cliente.setApellido(clienteBuscado.getApellido());
        cliente.setEmail(clienteBuscado.getEmail());
        cliente.setTelefono(clienteBuscado.getTelefono());

        this.clienteServicio.guardarCliente(cliente);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Map<String, Boolean>> eliminarCliente(@PathVariable Integer id, Authentication authentication) {
        Cliente cliente = this.clienteServicio.buscarClientePorId(id);

        validarPropiedad(cliente, authentication);

        this.clienteServicio.eliminarCliente(id);
        Map<String, Boolean> mapa = new HashMap<>();
        mapa.put("eliminado", true);
        return ResponseEntity.ok(mapa);
    }

    // --- MÉTODOS AUXILIARES DE SEGURIDAD ---

    private Usuario obtenerUsuarioLogueado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validarPropiedad(Cliente cliente, Authentication authentication) {
        if (cliente == null) {
            throw new RecursoNoEncontradoExepcion("Cliente no encontrado");
        }
        String emailLogueado = authentication.getName();
        // Si el email del dueño del cliente no coincide con el del token, bloqueamos
        if (!cliente.getUsuario().getEmail().equals(emailLogueado)) {
            throw new RuntimeException("Acceso denegado: este cliente no le pertenece.");
        }
    }
}