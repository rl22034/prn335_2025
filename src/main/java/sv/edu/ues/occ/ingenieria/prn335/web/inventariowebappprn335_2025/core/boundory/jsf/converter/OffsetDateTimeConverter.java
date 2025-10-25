package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@FacesConverter(value = "offsetDateTimeConverter", managed = true)
@ApplicationScoped
public class OffsetDateTimeConverter implements Converter<OffsetDateTime> {

    // Formato CON offset: 2025-10-14T13:45-06:00
    private static final DateTimeFormatter FMT_WITH_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");

    // Formato SIN offset: 2025-10-14T13:45 (lo que envía el datePicker)
    private static final DateTimeFormatter FMT_NO_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Zona horaria por defecto (El Salvador)
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/El_Salvador");

    @Override
    public OffsetDateTime getAsObject(FacesContext ctx, UIComponent comp, String value) {
        // Si viene vacío o nulo, retornamos null
        if (value == null || value.isBlank()) {
            return null;
        }

        // Normalizar: quitar espacios extras después de la 'T'
        String v = value.trim().replaceFirst("T\\s+", "T");

        try {
            // Verificar si trae offset al final (ejemplo: -06:00)
            if (v.matches(".*[+-]\\d{2}:\\d{2}$")) {
                // Si tiene offset, usarlo directamente
                return OffsetDateTime.parse(v, FMT_WITH_OFFSET);
            }

            // Si NO tiene offset, asumimos la zona de El Salvador
            LocalDateTime ldt = LocalDateTime.parse(v, FMT_NO_OFFSET);
            return ldt.atZone(DEFAULT_ZONE).toOffsetDateTime();

        } catch (Exception e) {
            // Si hay error de formato, retornamos null
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, OffsetDateTime value) {
        // Si el valor es nulo, retornamos String vacío
        if (value == null) {
            return "";
        }

        // Convertimos a la zona de El Salvador y formateamos SIN offset
        // porque el datePicker espera formato: 2025-10-14T13:45
        return value.atZoneSameInstant(DEFAULT_ZONE)
                .toLocalDateTime()
                .format(FMT_NO_OFFSET);
    }
}