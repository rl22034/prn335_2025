// En: sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf

package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;

import java.io.Serializable;
import java.util.UUID;

/**
 * Convertidor para la entidad Producto.
 * Usamos @Named y @RequestScoped para permitir la inyección de EJB (@Inject).
 */
@Named
@ApplicationScoped
public class ProductoConverter implements Converter<Producto>, Serializable {

    @Inject
    private ProductoDAO productoDAO;

    @Override
    public Producto getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            UUID id = UUID.fromString(value);
            return productoDAO.finById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Producto value) {
        if (value == null || value.getId() == null) {
            return null;
        }
        return value.getId().toString();
    }
}