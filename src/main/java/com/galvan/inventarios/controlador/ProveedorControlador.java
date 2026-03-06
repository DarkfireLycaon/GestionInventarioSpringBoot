package com.galvan.inventarios.controlador;

import com.galvan.inventarios.exepcion.RecursoNoEncontradoExepcion;
import com.galvan.inventarios.modelo.Proveedor;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepository;
import com.galvan.inventarios.servicio.ProveedorServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Necesario para identificar al usuario
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("inventario-app")

public class ProveedorControlador {
    private static final Logger LOG = LoggerFactory.getLogger(ProveedorControlador.class);

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/proveedores")
    public List<Proveedor> listarProveedores(Authentication authentication) {
        Usuario usuario = obtenerUsuarioLogueado(authentication);
        // Filtrado por usuario_id
        List<Proveedor> proveedores = this.proveedorServicio.listarProveedoresPorUsuario(usuario.getId());
        LOG.info("Listando proveedores para el usuario: {}", usuario.getEmail());
        return proveedores;
    }

    @PostMapping("/proveedores")
    public Proveedor crearProveedor(@RequestBody Proveedor proveedor, Authentication authentication) {
        Usuario usuario = obtenerUsuarioLogueado(authentication);
        String email = authentication.getName();
        // Asignamos el dueño antes de persistir
        proveedor.setUsuario(usuario);
        LOG.info("Creando proveedor para el usuario: {}", usuario.getEmail());
        return this.proveedorServicio.guardarProveedor(proveedor);
    }

    @GetMapping("/proveedores/{id}")
    public ResponseEntity<Proveedor> buscarProveedorPorId(@PathVariable Integer id, Authentication authentication) {
        Proveedor proveedor = proveedorServicio.buscarProveedorPorId(id);

        // Validación de propiedad
        validarPropiedad(proveedor, authentication);

        return ResponseEntity.ok(proveedor);
    }

    @PutMapping("/proveedores/{id}")
    public ResponseEntity<Proveedor> editarProveedorPorId(@PathVariable Integer id,
                                                          @RequestBody Proveedor proveedorRecibido,
                                                          Authentication authentication) {
        Proveedor proveedor = proveedorServicio.buscarProveedorPorId(id);

        validarPropiedad(proveedor, authentication);

        proveedor.setNombre(proveedorRecibido.getNombre());
        proveedor.setApellido(proveedorRecibido.getApellido());
        proveedor.setDireccion(proveedorRecibido.getDireccion());
        proveedor.setTelefono(proveedorRecibido.getTelefono());
        proveedor.setEmail(proveedorRecibido.getEmail());

        this.proveedorServicio.guardarProveedor(proveedor);
        return ResponseEntity.ok(proveedor);
    }

    @DeleteMapping("/proveedores/{id}")
    public ResponseEntity<Map<String, Boolean>> eliminarProveedorPorId(@PathVariable Integer id,
                                                                       Authentication authentication) {
        Proveedor proveedor = proveedorServicio.buscarProveedorPorId(id);

        validarPropiedad(proveedor, authentication);

        this.proveedorServicio.eliminarProveedor(id);
        Map<String, Boolean> mapa = new HashMap<>();
        mapa.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(mapa);
    }

    // --- MÉTODOS DE SEGURIDAD INTERNOS ---

    private Usuario obtenerUsuarioLogueado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la sesión"));
    }

    private void validarPropiedad(Proveedor proveedor, Authentication authentication) {
        if (proveedor == null) {
            throw new RecursoNoEncontradoExepcion("Proveedor no encontrado");
        }
        String emailLogueado = authentication.getName();
        // Verificamos que el email del dueño del proveedor sea el mismo que el del token JWT
        if (!proveedor.getUsuario().getEmail().equals(emailLogueado)) {
            LOG.warn("Intento de acceso no autorizado al proveedor ID {} por parte de {}", proveedor.getId(), emailLogueado);
            throw new RuntimeException("No tienes permiso para acceder a este proveedor.");
        }
    }
}