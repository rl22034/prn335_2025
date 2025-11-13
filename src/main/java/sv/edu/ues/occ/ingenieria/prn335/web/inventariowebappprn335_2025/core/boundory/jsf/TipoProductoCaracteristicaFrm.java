package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Named("tipoProductoCaracteristicaBean")
@ViewScoped
public class TipoProductoCaracteristicaFrm extends DefaultFrm<TipoProductoCaracteristica> implements Serializable {

    @Inject
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    @Inject
    private TipoProductoFrm tipoProductoBean;

    private List<Caracteristica> caracteristicasList;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    protected TipoProductoCaracteristicaDAO obtenerDAO() {
        return tipoProductoCaracteristicaDAO;
    }

    // Implementar métodos abstractos (aunque se sobrescriben crearEntidad/actualizarEntidad completos)
    @Override
    protected void validarAntesDeCrear(TipoProductoCaracteristica entidad) throws Exception {
        // La validación se hace en crearEntidad() sobrescrito
    }

    @Override
    protected void validarAntesDeActualizar(TipoProductoCaracteristica entidad) throws Exception {
        // La validación se hace en actualizarEntidad() sobrescrito
    }

    @Override
    protected void crearEntidad(TipoProductoCaracteristica entidad) throws Exception {
        if (entidad.getIdCaracteristica() == null) {
            throw new Exception("Debe seleccionar una característica");
        }
        // Obtener el TipoProducto seleccionado del bean padre
        TipoProducto tipoProductoActual = tipoProductoBean != null ? tipoProductoBean.getFilaSeleccionada() : null;
        if (tipoProductoActual == null || tipoProductoActual.getId() == null) {
            throw new Exception("No hay tipo de producto seleccionado");
        }
        entidad.setIdTipoProducto(tipoProductoActual);
        if (entidad.getFechaCreacion() == null) {
            entidad.setFechaCreacion(OffsetDateTime.now());
        }
        tipoProductoCaracteristicaDAO.crear(entidad);
        // Recargar la lista en el bean padre
        if (tipoProductoBean != null) {
            tipoProductoBean.cargarCaracteristicas();
        }
    }

    @Override
    protected void actualizarEntidad(TipoProductoCaracteristica entidad) throws Exception {
        if (entidad.getIdCaracteristica() == null) {
            throw new Exception("Debe seleccionar una característica");
        }
        tipoProductoCaracteristicaDAO.update(entidad);
        // Recargar la lista en el bean padre
        if (tipoProductoBean != null) {
            tipoProductoBean.cargarCaracteristicas();
        }
    }

    @Override
    protected void eliminarEntidad(TipoProductoCaracteristica entidad) throws Exception {
        TipoProductoCaracteristica original = tipoProductoCaracteristicaDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }
        tipoProductoCaracteristicaDAO.delete(entidad);
        // Recargar la lista en el bean padre
        if (tipoProductoBean != null) {
            tipoProductoBean.cargarCaracteristicas();
        }
    }

    @Override
    protected TipoProductoCaracteristica instanciarEntidad() {
        TipoProductoCaracteristica nuevo = new TipoProductoCaracteristica();
        nuevo.setObligatorio(false);
        nuevo.setFechaCreacion(OffsetDateTime.now());
        return nuevo;
    }

    @Override
    public void onRowSelect(SelectEvent<TipoProductoCaracteristica> event) {
        super.onRowSelect(event);
    }


    public List<Caracteristica> getCaracteristicasList() {
        if (caracteristicasList == null) {
            try {
                caracteristicasList = caracteristicaDAO.findRange(0, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return caracteristicasList;
    }

    public void setCaracteristicasList(List<Caracteristica> caracteristicasList) {
        this.caracteristicasList = caracteristicasList;
    }


}













