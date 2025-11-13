package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;

/**
 * Converter para TipoProductoCaracteristica en componentes JSF
 * Convierte entre el ID (String) y el objeto TipoProductoCaracteristica
 * Usado en el PickList de características de productos
 */
@Named
@FacesConverter(value = "tipoProductoCaracteristicaConverter", managed = true)
public class TipoProductoCaracteristicaConverter implements Converter<TipoProductoCaracteristica> {

    @Inject
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    /**
     * Convierte el String (ID) a objeto TipoProductoCaracteristica
     */
    @Override
    public TipoProductoCaracteristica getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            Long id = Long.parseLong(value);
            return tipoProductoCaracteristicaDAO.finById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Convierte el objeto TipoProductoCaracteristica a String (ID)
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, TipoProductoCaracteristica value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
