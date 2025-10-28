package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

@FacesConverter(value = "tipoUnidadMedidaConverter", managed = true)
@ApplicationScoped
public class TipoUnidadMedidaConverter implements Converter<TipoUnidadMedida> {

    @Inject
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    @Override
    public TipoUnidadMedida getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            return tipoUnidadMedidaDAO.finById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, TipoUnidadMedida value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}