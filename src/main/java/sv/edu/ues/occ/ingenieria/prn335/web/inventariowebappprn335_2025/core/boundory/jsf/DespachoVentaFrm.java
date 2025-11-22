package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("despachoVentaFrm")
@ViewScoped
public class DespachoVentaFrm extends DefaultFrm<Venta> implements Serializable {

    @Inject
    private VentaDAO ventaDAO;


    @PostConstruct
    public void init() {
        cargarRegistros();
    }

    public void cargarRegistros() {
        try {
            // Recarga el LazyDataModel para que la tabla se actualice
            if (lazyModel != null) {
                lazyModel.load(0, 10, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Filtra solo ventas DESPACHADAS para el LazyDataModel
    @Override
    protected List<Venta> buscarEntidades(int first, int pageSize) {
        try {
            List<Venta> ventasDespachadas = ventaDAO.findByEstado("DESPACHADA");
            // Simular paginación manual
            int toIndex = Math.min(first + pageSize, ventasDespachadas.size());
            if (first >= ventasDespachadas.size()) {
                return new ArrayList<>();
            }
            return ventasDespachadas.subList(first, toIndex);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Cuenta solo ventas DESPACHADAS
    @Override
    protected Long contarEntidades() throws Exception {
        try {
            return (long) ventaDAO.findByEstado("DESPACHADA").size();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    protected VentaDAO obtenerDAO() {
        return ventaDAO;
    }

    @Override
    protected Venta instanciarEntidad() {
        Venta nueva = new Venta();
        nueva.setEstado("DESPACHADA");
        return nueva;
    }

    @Override
    protected void validarAntesDeCrear(Venta entidad) throws Exception {
        // No aplica, no hay botón "Nuevo" en esta pantalla
    }

    @Override
    protected void validarAntesDeActualizar(Venta entidad) throws Exception {
        if (entidad.getIdCliente() == null) {
            throw new Exception("validacion.venta.campos.requeridos");
        }
    }

    /**
     * Obtiene los estados disponibles para el dropdown
     * @return Lista de SelectItem con todos los estados de venta
     */
    public List<SelectItem> getEstadosDisponibles() {
        List<SelectItem> estados = new ArrayList<>();
        for (EstadoVenta estado : EstadoVenta.values()) {
            estados.add(new SelectItem(estado.name(), estado.getDescripcion()));
        }
        return estados;
    }
}
