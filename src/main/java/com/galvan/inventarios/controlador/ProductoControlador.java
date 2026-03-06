package com.galvan.inventarios.controlador;

import com.galvan.inventarios.exepcion.RecursoNoEncontradoExepcion;
import com.galvan.inventarios.modelo.Producto;
import com.galvan.inventarios.modelo.Usuario; // Asegúrate de importar tu modelo
import com.galvan.inventarios.repositorio.UsuarioRepository; // Necesario para buscar al dueño
import com.galvan.inventarios.servicio.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // IMPORTANTE
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("inventario-app")

public class ProductoControlador {
    private static final Logger LOG = LoggerFactory.getLogger(ProductoControlador.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository; // Lo usamos para obtener el ID del usuario actual

    @GetMapping("/productos")
    public List<Producto> obtenerProdutos(Authentication authentication) {
        // 1. Sacamos el email del token
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        // 2. Filtramos: Necesitas crear este método en tu Service/Repository
        List<Producto> productos = this.productoService.listarProductosPorUsuario(usuario.getId());

        LOG.info("Productos obtenidos para el usuario: " + email);
        return productos;
    }

    @PostMapping("/productos")
    public Producto agregarProducto(@RequestBody Producto producto, Authentication authentication) {
        // 1. Obtenemos el usuario logueado
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        // 2. Le asignamos el dueño al producto antes de guardar
        producto.setUsuario(usuario);

        LOG.info("Agregando producto al usuario: " + email);
        return this.productoService.guardarProducto(producto);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Integer id, Authentication authentication) {
        Producto producto = this.productoService.buscarProductoPorId(id);

        // SEGURIDAD: Validar que el producto existe Y pertenece al usuario logueado
        validarPropiedad(producto, authentication);

        return ResponseEntity.ok(producto);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable int id,
            @RequestBody Producto productoRecibido,
            Authentication authentication) {

        Producto producto = this.productoService.buscarProductoPorId(id);

        // SEGURIDAD: Solo el dueño puede editar
        validarPropiedad(producto, authentication);

        producto.setDescripcion(productoRecibido.getDescripcion());
        producto.setPrecioCompra(productoRecibido.getPrecioCompra());
        producto.setPrecioVenta(productoRecibido.getPrecioVenta());
        producto.setStock(productoRecibido.getStock());

        this.productoService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Map<String, Boolean>> eliminarProducto(@PathVariable Integer id, Authentication authentication) {
        Producto producto = this.productoService.buscarProductoPorId(id);

        // SEGURIDAD: Solo el dueño puede borrar
        validarPropiedad(producto, authentication);

        this.productoService.eliminarProducto(id);
        Map<String, Boolean> mapa = new HashMap<>();
        mapa.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(mapa);
    }

    // Método auxiliar para evitar que un usuario manipule IDs de otros
    private void validarPropiedad(Producto producto, Authentication authentication) {
        if (producto == null) {
            throw new RecursoNoEncontradoExepcion("Producto no encontrado");
        }
        String emailLogueado = authentication.getName();
        if (!producto.getUsuario().getEmail().equals(emailLogueado)) {
            throw new RuntimeException("No tienes permiso para acceder a este recurso");
        }
    }
}