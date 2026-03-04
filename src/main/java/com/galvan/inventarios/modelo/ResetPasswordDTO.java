package com.galvan.inventarios.modelo;


public class ResetPasswordDTO {
    private String token;
    private String nuevaPassword;

    // Constructores
    public ResetPasswordDTO() {}

    public ResetPasswordDTO(String token, String nuevaPassword) {
        this.token = token;
        this.nuevaPassword = nuevaPassword;
    }

    // Getters y Setters (Vitales para que Spring pueda leer el JSON)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}