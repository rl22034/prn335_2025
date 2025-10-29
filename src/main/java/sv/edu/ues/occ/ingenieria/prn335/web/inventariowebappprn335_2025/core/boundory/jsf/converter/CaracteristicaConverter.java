package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;

/**
 * Converter para Caracteristica en componentes JSF
 * Convierte entre el ID (String) y el objeto Caracteristica
 */
@Named
@FacesConverter(value = "caracteristicaConverter", managed = true)
public class CaracteristicaConverter implements Converter<Caracteristica> {

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    /**
     * Convierte el String (ID) a objeto Caracteristica
     */
    @Override
    public Caracteristica getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            Integer id = Integer.parseInt(value);
            return caracteristicaDAO.finById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Convierte el objeto Caracteristica a String (ID)
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Caracteristica value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}