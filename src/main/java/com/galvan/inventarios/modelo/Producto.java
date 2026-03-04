package com.galvan.inventarios.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

// @Entity: Indica que esta clase es una entidad JPA y se mapeará a una tabla
// en la base de datos. Spring Boot creará automáticamente una tabla llamada "producto"
@Entity
public class Producto {
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    // @Id: Marca este campo como la clave primaria de la tabla
    // @GeneratedValue: Configura cómo se genera automáticamente el valor del ID
    // strategy = GenerationType.IDENTITY: Usa el auto-increment de la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    // Estos campos sin anotaciones se mapearán automáticamente a columnas
    // con el mismo nombre en la tabla de la base de datos
    private String descripcion;
    private Double precioCompra;
    private Double precioVenta;
    private Integer stock;

    // @ManyToMany: Define una relación muchos-a-muchos con la entidad Proveedor
    // mappedBy = "productos": Indica que esta entidad NO es la dueña de la relación,
    // la relación está mapeada por el campo "productos" en la entidad Proveedor
    @ManyToMany(mappedBy = "productos")

    // @JsonIgnore: Evita que este campo se incluya en la serialización JSON
    // cuando se devuelve una respuesta HTTP. Previene problemas de recursión infinita
    // y posibles errores de rendimiento al cargar datos relacionados
    @JsonIgnore
    private List<Proveedor> proveedores;

    // Constructor vacío requerido por JPA/Hibernate para la creación de instancias
    public Producto() {

    }

    // Constructor personalizado para crear productos con parámetros específicos
    public Producto(String descripcion, Double precio, Integer stock) {
        this.descripcion = descripcion;
        this.precioVenta = precio;
        this.stock = stock;
    }

    // Getters y Setters: Métodos que Spring utiliza para:
    // - Inyectar dependencias cuando es necesario
    // - Serializar/deserializar objetos JSON en los controladores REST
    // - Acceder/modificar datos en las operaciones de base de datos

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precio) {
        this.precioVenta = precio;

    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public List<Proveedor> getProveedores() {
        return proveedores;
    }
    public void setProveedores(List<Proveedor> proveedores) {
        this.proveedores = proveedores;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // @Override: Sobrescribe el método toString() de Object
    // Útil para depuración y logging, Spring lo usa cuando imprime objetos
    @Override
    public String toString() {
        return "Producto "+ descripcion +"precio compra "+ precioCompra+ "precio venta" +precioVenta+" stock " + stock;
    }
}