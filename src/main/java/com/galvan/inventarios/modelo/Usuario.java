package com.galvan.inventarios.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean enabled = false; // Solo pasa a true al confirmar email
    private String codigoConfirmacion; // Código que enviaremos por mail
    private String resetToken;
    private LocalDateTime tokenExpiration;

    public Usuario() {}

    public Usuario(String nombre, String email, String password, String codigoConfirmacion) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.codigoConfirmacion = codigoConfirmacion;

    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getCodigoConfirmacion() {
        return codigoConfirmacion;
    }
    public void setCodigoConfirmacion(String codigoConfirmacion) {
        this.codigoConfirmacion = codigoConfirmacion;
    }
    public String getResetToken() {
        return resetToken;
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    public LocalDateTime getTokenExpiration() {
        return tokenExpiration;
    }
    public void setTokenExpiration(LocalDateTime tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}