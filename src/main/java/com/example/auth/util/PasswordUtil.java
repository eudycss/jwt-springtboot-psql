package com.example.auth.util;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Utilidad para manejar contraseñas en diferentes formatos
 */
public class PasswordUtil {

    // Patrón para detectar si una cadena es potencialmente Base64
    private static final Pattern BASE64_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
    
    /**
     * Procesa una contraseña que puede estar en texto plano o en Base64
     * 
     * @param password La contraseña proporcionada por el usuario
     * @return La contraseña en texto plano
     */
    public static String processPassword(String password) {
        // Si la contraseña parece ser Base64, intentamos decodificarla
        if (isBase64(password)) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(password);
                return new String(decodedBytes);
            } catch (IllegalArgumentException e) {
                // Si falla la decodificación, asumimos que es texto plano
                return password;
            }
        }
        
        // Si no parece Base64, la usamos como texto plano
        return password;
    }
    
    /**
     * Verifica si una cadena parece estar codificada en Base64
     * 
     * @param str La cadena a verificar
     * @return true si la cadena parece ser Base64, false en caso contrario
     */
    private static boolean isBase64(String str) {
        // Verificar si cumple con el patrón de Base64 y tiene longitud múltiplo de 4 (o casi)
        // y tiene al menos 4 caracteres (mínimo razonable para Base64)
        return str != null && 
               str.length() >= 4 && 
               (str.length() % 4 <= 2) && 
               BASE64_PATTERN.matcher(str).matches();
    }
} 