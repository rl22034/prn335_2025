package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.UnidadMedida;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("tipoUnidadMedidaBean")
@ViewScoped
public class TipoUnidadMedidaFrm extends DefaultFrm<TipoUnidadMedida> implements Serializable {

    // --- Inyecciones DAO Maestro ---
    @Inject
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    // --- Inyecciones DAO Detalle ---
    @Inject
    private UnidadMedidaDAO unidadMedidaDAO; // DAO para el detalle

    // --- Propiedades para el Detalle ---
    private List<UnidadMedida> unidadesDeMedida = new ArrayList<>(); // Lista de detalles
    private UnidadMedida detalleSeleccionado; // El detalle que se está creando/editando

    // ========================================================== //
    //    MÉTODOS ABSTRACTOS (LÓGICA MAESTRO: TIPOUNIDADMEDIDA)
    // ========================================================== //

    @Override
    protected TipoUnidadMedidaDAO obtenerDAO() {
        return tipoUnidadMedidaDAO;
    }

    @Override
    protected void validarAntesDeCrear(TipoUnidadMedida entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
    }

    @Override
    protected void validarAntesDeActualizar(TipoUnidadMedida entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
    }

    @Override
    protected void eliminarEntidad(TipoUnidadMedida entidad) throws Exception {
        // Verificamos que no tenga detalles antes de borrar
        List<UnidadMedida> detalles = unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(entidad.getId());
        if (detalles != null && !detalles.isEmpty()) {
            throw new Exception("No se puede eliminar, tiene unidades de medida asociadas.");
        }
        tipoUnidadMedidaDAO.delete(entidad);
    }

    @Override
    protected TipoUnidadMedida instanciarEntidad() {
        TipoUnidadMedida nueva = new TipoUnidadMedida();
        nueva.setActivo(true);
        // El ID (Integer) es autoincremental, no se genera aquí
        return nueva;
    }

    // ========================================================== //
    //    MÉTODOS DEL BEAN (LÓGICA MAESTRO)
    // ========================================================== //

    @Override
    public void btnNuevo() {
        super.btnNuevo(); // Llama a instanciarEntidad()
        // Limpiamos la lista de detalles para el nuevo Tipo
        this.unidadesDeMedida = new ArrayList<>();
        this.detalleSeleccionado = null;
    }

    /**
     * Método helper para el XHTML, para deshabilitar pestañas/botones
     * si el Tipo de Unidad de Medida aún no se ha guardado.
     */
    public boolean isTipoNuevo() {
        // Usa el estado del formulario (CREAR), no el ID null
        return this.getEstado() == CRUD.CREAR;
    }

    // ========================================================== //
    //    MÉTODOS DEL BEAN (LÓGICA DETALLE: UNIDADMEDIDA)
    // ========================================================== //

    /**
     * Carga los detalles (UnidadMedida) del Tipo que se está viendo.
     */
    public void cargarDetalles() {
        if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
            try {
                if (unidadMedidaDAO == null) {
                    throw new IllegalStateException("UnidadMedidaDAO no fue inyectado.");
                }
                // Usamos el método que creamos en el DAO
                this.unidadesDeMedida = unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(this.filaSeleccionada.getId());
            } catch (Exception e) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al cargar unidades de medida");
                this.unidadesDeMedida = new ArrayList<>();
            }
        } else {
            this.unidadesDeMedida = new ArrayList<>();
        }
    }

    /**
     * Prepara un nuevo objeto UnidadMedida para el diálogo "Añadir Unidad".
     */
    public void prepararNuevoDetalle() {
        this.detalleSeleccionado = new UnidadMedida();

        // ¡Vínculo Maestro-Detalle!
        // Asume que UnidadMedida.java tiene un setIdTipoUnidadMedida(TipoUnidadMedida obj)
        this.detalleSeleccionado.setIdTipoUnidadMedida(this.filaSeleccionada);

        // Valores por defecto
        this.detalleSeleccionado.setActivo(true);
        // El ID (Integer) es autoincremental
    }

    /**
     * Guarda la nueva UnidadMedida en la BBDD.
     */
    /**
     * Guarda la nueva UnidadMedida en la BBDD.
     * MODIFICADO: Ahora maneja tanto CREAR como ACTUALIZAR.
     */
    public void guardarDetalle() {
        try {
            // ... (tus validaciones de 'equivalencia', etc.) ...

            if (unidadMedidaDAO == null) {
                throw new IllegalStateException("UnidadMedidaDAO no fue inyectado.");
            }

            // --- ¡AQUÍ ESTÁ LA LÓGICA CLAVE! ---
            if (this.detalleSeleccionado.getId() == null) {
                // 1. CREAR: Si el ID es nulo, es un registro nuevo
                unidadMedidaDAO.crear(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "Unidad añadida");
            } else {
                // 2. ACTUALIZAR: Si el ID ya existe, es una edición
                unidadMedidaDAO.update(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "Unidad actualizada");
            }
            // --- FIN DE LA LÓGICA ---

            // Refrescar la tabla y cerrar el diálogo (esto es igual)
            cargarDetalles();
            PrimeFaces.current().executeScript("PF('dlgUnidadMedida').hide()");

        } catch (Exception e) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al guardar detalle: " + e.getMessage());
        }
    }

    /**
     * Elimina una UnidadMedida (detalle) del Tipo.
     */
    public void eliminarDetalle(UnidadMedida detalle) {
        try {
            if (unidadMedidaDAO == null) {
                throw new IllegalStateException("UnidadMedidaDAO no fue inyectado.");
            }
            unidadMedidaDAO.delete(detalle);
            cargarDetalles(); // Refrescar la tabla
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "Unidad eliminada");
        } catch (Exception e) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al eliminar detalle: " + e.getMessage());
        }
    }


    // ========================================================== //
    //    GETTERS Y SETTERS (GENERADOS)
    // ========================================================== //

    public TipoUnidadMedidaDAO getTipoUnidadMedidaDAO() {
        return tipoUnidadMedidaDAO;
    }

    public void setTipoUnidadMedidaDAO(TipoUnidadMedidaDAO tipoUnidadMedidaDAO) {
        this.tipoUnidadMedidaDAO = tipoUnidadMedidaDAO;
    }

    public UnidadMedidaDAO getUnidadMedidaDAO() {
        return unidadMedidaDAO;
    }

    public void setUnidadMedidaDAO(UnidadMedidaDAO unidadMedidaDAO) {
        this.unidadMedidaDAO = unidadMedidaDAO;
    }

    public List<UnidadMedida> getUnidadesDeMedida() {
        return unidadesDeMedida;
    }

    public void setUnidadesDeMedida(List<UnidadMedida> unidadesDeMedida) {
        this.unidadesDeMedida = unidadesDeMedida;
    }

    public UnidadMedida getDetalleSeleccionado() {
        if (detalleSeleccionado == null) {
            detalleSeleccionado = new UnidadMedida();
        }
        return detalleSeleccionado;
    }

    public void setDetalleSeleccionado(UnidadMedida detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }


    public void prepararEditarDetalle(UnidadMedida detalle) {
        // Simplemente asigna el objeto de la fila al 'detalleSeleccionado'
        // que ya usa tu diálogo.
        this.detalleSeleccionado = detalle;
    }

}