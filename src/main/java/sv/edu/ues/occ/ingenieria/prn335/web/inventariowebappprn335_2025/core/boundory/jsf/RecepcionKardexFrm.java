package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("recepcionKardexFrm")
@ViewScoped
public class RecepcionKardexFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    private CompraDAO compraDAO;

    private String nombrePagina = "Recepción de Kardex";

    @PostConstruct
    public void init() {
        cargarRegistros();
    }

    // ✅ MANTENER: Este método lo usa el remoteCommand del WebSocket
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

    // Filtra solo compras PAGADAS para el LazyDataModel
    @Override
    protected List<Compra> buscarEntidades(int first, int pageSize) {
        try {
            List<Compra> comprasPagadas = compraDAO.findByEstado("PAGADA");
            // Simular paginación manual
            int toIndex = Math.min(first + pageSize, comprasPagadas.size());
            if (first >= comprasPagadas.size()) {
                return new ArrayList<>();
            }
            return comprasPagadas.subList(first, toIndex);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Cuenta solo compras PAGADAS
    @Override
    protected Long contarEntidades() throws Exception {
        try {
            return (long) compraDAO.findByEstado("PAGADA").size();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    protected CompraDAO obtenerDAO() {
        return compraDAO;
    }

    @Override
    protected Compra instanciarEntidad() {
        Compra nueva = new Compra();
        nueva.setEstado("PAGADA");
        return nueva;
    }


    @Override
    protected void validarAntesDeActualizar(Compra entidad) throws Exception {
        if (entidad.getProveedor() == null) {
            throw new Exception("validacion.proveedor.requerido");
        }
    }


    public String getNombrePagina() {
        return nombrePagina;
    }

    public void setNombrePagina(String nombrePagina) {
        this.nombrePagina = nombrePagina;
    }

    /**
     * Obtiene los estados disponibles para el dropdown
     * @return Lista de SelectItem con todos los estados de compra
     */
    public List<SelectItem> getEstadosDisponibles() {
        List<SelectItem> estados = new ArrayList<>();
        for (EstadoCompra estado : EstadoCompra.values()) {
            estados.add(new SelectItem(estado.name(), estado.getDescripcion()));
        }
        return estados;
    }
}
