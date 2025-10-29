package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;

import java.io.Serializable;
import java.util.UUID;

/**
 * Convertidor para la entidad Cliente.
 * Traduce entre un objeto Cliente y su representación String (UUID).
 */
@Named("clienteConverter") // El ID que usarás en el XHTML
@ApplicationScoped
public class ClienteConverter implements Converter<Cliente>, Serializable {

    @Inject
    private ClienteDAO clienteDAO; // Asume que tienes un ClienteDAO similar a ProveedorDAO

    /**
     * Toma el String (UUID) del formulario y lo convierte en un objeto Cliente.
     */
    @Override
    public Cliente getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            // El ID del cliente es UUID según tu DDL
            UUID id = UUID.fromString(value);

            if (clienteDAO == null) {
                System.err.println("ClienteConverter: ClienteDAO no fue inyectado.");
                return null;
            }

            return clienteDAO.finById(id);
        } catch (Exception e) {
            System.err.println("Error en ClienteConverter getAsObject: " + e.getMessage());
            return null;
        }
    }

    /**
     * Toma el objeto Cliente del bean y lo convierte en un String (UUID) para el HTML.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Cliente value) {
        if (value == null || value.getId() == null) {
            return null;
        }
        // El ID del cliente es UUID
        return value.getId().toString();
    }
}