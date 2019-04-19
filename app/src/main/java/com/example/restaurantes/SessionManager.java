package com.example.restaurantes;

public abstract class SessionManager {
    private static String token;
    private static String nombre;
    private static String email;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        SessionManager.token = token;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        SessionManager.nombre = nombre;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        SessionManager.email = email;
    }
}
