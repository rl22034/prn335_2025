package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;

import java.io.Serializable;
import java.util.List;

@Named("tipoAlmacenBean")
@ViewScoped
public class TipoAlmacenFrm extends DefaultFrm<TipoAlmacen> implements Serializable {

    @Inject
    private TipoAlmacenDAO tipoAlmacenDAO;

    @Override
    protected void crearEntidad(TipoAlmacen entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        tipoAlmacenDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(TipoAlmacen entidad) throws Exception {
        //  Validar con clave
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");  // ⭐ Solo la clave
        }

        try{
            tipoAlmacenDAO.update(entidad);
        }catch (Exception e){
            // Propagar el error real de la BD
            throw e;
        }
    }

    @Override
    protected void eliminarEntidad(TipoAlmacen entidad) throws Exception {
        try {
            // Obtener el registro actual de la base de datos
            TipoAlmacen original = tipoAlmacenDAO.finById(entidad.getId());

            // Validar que el registro aún existe
            if (original == null) {
                throw new Exception("validacion.registro.no.existe");  // ⭐ Solo la clave
            }

            // ⭐ Validar que el nombre NO ha cambiado
            if (!entidad.getNombre().equals(original.getNombre())) {
                throw new Exception("validacion.nombre.cambiado");  // ⭐ Solo la clave
            }

            // Si el nombre es igual, proceder con la eliminación
            tipoAlmacenDAO.delete(entidad);

        } catch (Exception e) {
            // Propagar el error
            throw e;
        }
    }

    @Override
    protected List<TipoAlmacen> buscarEntidades(int first, int pageSize) throws Exception {
        try{
            return tipoAlmacenDAO.findRange(first, pageSize);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    protected Long contarEntidades() throws Exception {
        try{
            return tipoAlmacenDAO.count();
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    protected Object obtenerIdEntidad(TipoAlmacen entidad) {
        return entidad.getId();
    }

    @Override
    protected TipoAlmacen instanciarEntidad() {
        TipoAlmacen nuevo = new TipoAlmacen();
        nuevo.setActivo(true);
        return nuevo;
    }




    // Getters y setters

    public TipoAlmacen getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(TipoAlmacen filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<TipoAlmacen> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<TipoAlmacen> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public TipoAlmacenDAO getTipoAlmacenDAO() {
        return tipoAlmacenDAO;
    }

    public void setTipoAlmacenDAO(TipoAlmacenDAO tipoAlmacenDAO) {
        this.tipoAlmacenDAO = tipoAlmacenDAO;
    }
}