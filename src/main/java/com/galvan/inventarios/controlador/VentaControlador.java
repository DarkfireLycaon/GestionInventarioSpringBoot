package com.galvan.inventarios.controlador;

import com.galvan.inventarios.modelo.Venta;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepository;
import com.galvan.inventarios.servicio.VentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Necesario para seguridad
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController


public class VentaControlador {

    @Autowired
    private VentaServicio ventaServicio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/ventas")
    public List<Venta> listarVentas(Authentication authentication) {
        // 1. Obtener usuario del token
        Usuario usuario = obtenerUsuarioLogueado(authentication);

        // 2. Filtrar ventas por el ID del dueño
        return ventaServicio.listarVentasPorUsuario(usuario.getId());
    }

    @PostMapping("/ventas")
    public ResponseEntity<Venta> crearVenta(@RequestBody Venta venta, Authentication authentication) {
        // 1. Obtener usuario del token
        Usuario usuario = obtenerUsuarioLogueado(authentication);
        String email = authentication.getName();
        // 2. Asignar el dueño a la venta
        venta.setUsuario(usuario);

        // 3. Guardar
        Venta nuevaVenta = ventaServicio.guardarVenta(venta);
        return ResponseEntity.ok(nuevaVenta);
    }

    // Método auxiliar para centralizar la obtención del usuario
    private Usuario obtenerUsuarioLogueado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
    }
}