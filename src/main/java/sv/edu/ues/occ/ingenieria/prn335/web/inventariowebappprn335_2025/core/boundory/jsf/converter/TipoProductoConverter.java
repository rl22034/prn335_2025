package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

/**
 * Converter para TipoProducto en componentes JSF
 * Convierte entre el ID (String) y el objeto TipoProducto
 * Usado especialmente para el selector de TipoProducto Padre (jerarquía)
 */
@Named
@FacesConverter(value = "tipoProductoConverter", managed = true)
public class TipoProductoConverter implements Converter<TipoProducto> {

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    /**
     * Convierte el String (ID) a objeto TipoProducto
     */
    @Override
    public TipoProducto getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            Long id = Long.parseLong(value);
            return tipoProductoDAO.finById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Convierte el objeto TipoProducto a String (ID)
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, TipoProducto value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}