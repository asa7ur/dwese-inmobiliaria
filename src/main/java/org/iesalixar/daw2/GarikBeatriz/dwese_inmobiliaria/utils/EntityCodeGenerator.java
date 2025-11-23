package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils;

public class EntityCodeGenerator {

    /**
     * Genera un código del tipo XX12345 a partir de:
     * - entityClass: clase de la entidad (se usan las dos primeras letras en mayúsculas)
     * - identifier: String único de la entidad (ej: DNI)
     *
     * El número de 5 dígitos se obtiene a partir del hash del identificador,
     * asegurando un valor numérico de longitud fija.
     *
     * Ejemplo:
     * Agent con identificador "12345678A" -> AG94081 (los dígitos dependen del hash)
     */
    public static String generateCode(Class<?> entityClass, String identifier) {
        if (entityClass == null || identifier == null || identifier.isEmpty()) {
            return null;
        }

        // Iniciales de la entidad (2 primeras letras)
        String letters = entityClass.getSimpleName().substring(0, 2).toUpperCase();

        // 5 dígitos derivados del identificador
        int hash = Math.abs(identifier.hashCode());
        String numbers = String.format("%05d", hash % 100000);

        // Combinar letras + números
        return letters + numbers;
    }

    /**
     * Sobrecarga para generar código a partir del ID de la entidad
     * útil para Appointment donde quieres letras + 5 dígitos del ID
     */
    public static String generateCode(Class<?> entityClass, Long id) {
        if (entityClass == null || id == null) return null;

        String letters = entityClass.getSimpleName().substring(0, 2).toUpperCase();
        String numbers = String.format("%05d", id);
        return letters + numbers;
    }
}
