package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.components;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.PropertyImage;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("imgHelper") // El nombre "imgHelper" es como lo llamaremos en el HTML
public class ImageHelper {

    public String getImagesAsString(List<PropertyImage> images) {
        if (images == null || images.isEmpty()) {
            return "";
        }
        // Convierte la lista de objetos Image a un String separado por comas
        return images.stream()
                .map(PropertyImage::getFileName)
                .collect(Collectors.joining(","));
    }
}