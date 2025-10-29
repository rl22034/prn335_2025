package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;


import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.util.UUID;

/**
 * Converter personalizado para UUID
 */
@FacesConverter("uuidConverter")
public class UUIDConverter implements Converter<UUID> {

    @Override
    public UUID getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, UUID value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
