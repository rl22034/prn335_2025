package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

@Named
@FacesConverter(value = "tipoAlmacenConverter", managed = true)
public class TipoAlmacenConverter implements Converter<TipoAlmacen> {

    @Inject
    private TipoAlmacenDAO tipoAlmacenDAO;

    @Override
    public TipoAlmacen getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || value.equals("null")) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            return tipoAlmacenDAO.finById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, TipoAlmacen tipoAlmacen) {
        if (tipoAlmacen == null || tipoAlmacen.getId() == null) {
            return "";
        }
        return tipoAlmacen.getId().toString();
    }
}
