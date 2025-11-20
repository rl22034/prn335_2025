package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DualListModel;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Named("productoTipoProductoBean")
@ViewScoped
public class ProductoTipoProductoFrm extends DefaultFrm<ProductoTipoProducto> implements Serializable {

    @Inject
    private ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    @Inject
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    @Inject
    private ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;

    @Inject
    private ProductoFrm productoBean;

    // Para el PickList de características
    private DualListModel<TipoProductoCaracteristica> caracteristicasPickList;

    // Para almacenar valores de características asignadas
    private Map<Long, String> valoresCaracteristicas;

    // TipoProducto seleccionado
    private TipoProducto tipoProductoSeleccionado;

    @PostConstruct
    public void init() {
        valoresCaracteristicas = new HashMap<>();
        caracteristicasPickList = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
        super.init(); // Inicializa el LazyModel
    }

    /**
     * Método para recargar la tabla cuando se abre el tab
     */
    public void recargarTabla() {
        if (lazyModel != null) {
            initLazyModel();
        }
    }

    // ==================== MÉTODOS ABSTRACTOS DE DefaultFrm ====================

    @Override
    protected ProductoTipoProductoDAO obtenerDAO() {
        return productoTipoProductoDAO;
    }

    @Override
    protected void crearEntidad(ProductoTipoProducto entidad) throws Exception {
        try {
            if (tipoProductoSeleccionado == null) {
                throw new Exception("validacion.tipoproducto.requerido");
            }

            // Validar que todas las características obligatorias estén asignadas
            validarCaracteristicasObligatorias();

            entidad.setIdTipoProducto(tipoProductoSeleccionado);
            entidad.setFechaCreacion(OffsetDateTime.now());

            // Obtener el producto actual
            Producto productoActual = productoBean != null ? productoBean.getFilaSeleccionada() : null;
            if (productoActual == null || productoActual.getId() == null) {
                throw new Exception("No hay producto seleccionado");
            }
            entidad.setIdProducto(productoActual);

            // Crear ProductoTipoProducto
            productoTipoProductoDAO.crear(entidad);

            // Guardar características
            guardarCaracteristicas(entidad);

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected void actualizarEntidad(ProductoTipoProducto entidad) throws Exception {
        try {
            if (tipoProductoSeleccionado == null) {
                throw new Exception("validacion.tipoproducto.requerido");
            }

            // Validar que todas las características obligatorias estén asignadas
            validarCaracteristicasObligatorias();

            entidad.setIdTipoProducto(tipoProductoSeleccionado);

            // Actualizar ProductoTipoProducto
            productoTipoProductoDAO.update(entidad);

            // Guardar características
            guardarCaracteristicas(entidad);

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Hook ejecutado antes de eliminar. Elimina características relacionadas primero.
     * Las validaciones de existencia ya las hace DefaultFrm automáticamente.
     */
    @Override
    protected void validarAntesDeEliminar(ProductoTipoProducto entidad, ProductoTipoProducto original) throws Exception {
        // Eliminar características relacionadas primero (CASCADE manual)
        productoTipoProductoCaracteristicaDAO.eliminarPorProductoTipoProducto(entidad.getId());
    }

    @Override
    protected List<ProductoTipoProducto> buscarEntidades(int first, int pageSize) throws Exception {
        try {
            Producto productoActual = productoBean != null ? productoBean.getFilaSeleccionada() : null;

            if (productoActual != null && productoActual.getId() != null) {
                return productoTipoProductoDAO.findByIdProducto(productoActual.getId(), first, pageSize);
            }

            return new ArrayList<>();

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected Long contarEntidades() throws Exception {
        try {
            Producto productoActual = productoBean != null ? productoBean.getFilaSeleccionada() : null;

            if (productoActual != null && productoActual.getId() != null) {
                return productoTipoProductoDAO.countByIdProducto(productoActual.getId());
            }

            return 0L;

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected ProductoTipoProducto instanciarEntidad() {
        ProductoTipoProducto nuevo = new ProductoTipoProducto();
        nuevo.setId(UUID.randomUUID()); // ✅ Generar UUID para el ID
        nuevo.setActivo(true);
        nuevo.setFechaCreacion(OffsetDateTime.now());

        // Asignar automáticamente el producto seleccionado
        Producto productoActual = productoBean != null ? productoBean.getFilaSeleccionada() : null;
        if (productoActual != null) {
            nuevo.setIdProducto(productoActual);
        }

        return nuevo;
    }

    // ==================== MÉTODOS PERSONALIZADOS ====================

    /**
     * Override del método btnNuevo para inicializar el PickList
     */
    @Override
    public void btnNuevo() {
        super.btnNuevo();
        tipoProductoSeleccionado = null;
        valoresCaracteristicas.clear();
        caracteristicasPickList = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Override del método onRowSelect para cargar características cuando se selecciona una fila
     */
    @Override
    public void onRowSelect(org.primefaces.event.SelectEvent<ProductoTipoProducto> event) {
        super.onRowSelect(event);
        if (getFilaSeleccionada() != null && getFilaSeleccionada().getIdTipoProducto() != null) {
            tipoProductoSeleccionado = getFilaSeleccionada().getIdTipoProducto();
            cargarCaracteristicas();
        }
    }

    /**
     * Se ejecuta cuando cambia el TipoProducto seleccionado
     */
    public void onTipoProductoChange() {
        if (tipoProductoSeleccionado == null) {
            caracteristicasPickList = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
            return;
        }

        if (getFilaSeleccionada() != null) {
            getFilaSeleccionada().setIdTipoProducto(tipoProductoSeleccionado);
        }
        cargarCaracteristicas();
    }

    /**
     * Carga las características disponibles y asignadas
     * Las características OBLIGATORIAS siempre van a asignadas automáticamente
     */
    private void cargarCaracteristicas() {
        try {
            if (tipoProductoSeleccionado == null || tipoProductoSeleccionado.getId() == null) {
                caracteristicasPickList = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
                return;
            }

            // Obtener todas las características del TipoProducto
            List<TipoProductoCaracteristica> todasCaracteristicas =
                    tipoProductoCaracteristicaDAO.findByTipoProducto(tipoProductoSeleccionado.getId());

            List<TipoProductoCaracteristica> disponibles = new ArrayList<>();
            List<TipoProductoCaracteristica> asignadas = new ArrayList<>();

            if (getFilaSeleccionada() != null && getFilaSeleccionada().getId() != null) {
                // MODO EDICIÓN: Cargar las que ya están guardadas en BD
                List<ProductoTipoProductoCaracteristica> caracteristicasAsignadas =
                        productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(getFilaSeleccionada().getId());

                Set<Long> idsAsignadas = caracteristicasAsignadas.stream()
                        .map(ptpc -> ptpc.getIdTipoProductoCaracteristica().getId())
                        .collect(Collectors.toSet());

                // Cargar valores en el mapa
                valoresCaracteristicas.clear();
                for (ProductoTipoProductoCaracteristica ptpc : caracteristicasAsignadas) {
                    valoresCaracteristicas.put(
                            ptpc.getIdTipoProductoCaracteristica().getId(),
                            ptpc.getValor() != null ? ptpc.getValor() : ""
                    );
                }

                // Separar en disponibles y asignadas
                for (TipoProductoCaracteristica tpc : todasCaracteristicas) {
                    if (idsAsignadas.contains(tpc.getId())) {
                        // Ya está guardada en BD
                        asignadas.add(tpc);
                    } else if (tpc.getObligatorio() != null && tpc.getObligatorio()) {
                        // Es obligatoria pero no está guardada - agregarla a asignadas
                        asignadas.add(tpc);
                        valoresCaracteristicas.put(tpc.getId(), ""); // Valor vacío por defecto
                    } else {
                        // Es opcional y no está guardada - va a disponibles
                        disponibles.add(tpc);
                    }
                }
            } else {
                // MODO NUEVO: Separar por obligatoriedad
                valoresCaracteristicas.clear();

                for (TipoProductoCaracteristica tpc : todasCaracteristicas) {
                    if (tpc.getObligatorio() != null && tpc.getObligatorio()) {
                        // Característica OBLIGATORIA → va directo a asignadas
                        asignadas.add(tpc);
                        valoresCaracteristicas.put(tpc.getId(), ""); // Valor vacío por defecto
                    } else {
                        // Característica OPCIONAL → va a disponibles
                        disponibles.add(tpc);
                    }
                }
            }

            caracteristicasPickList = new DualListModel<>(disponibles, asignadas);

        } catch (Exception e) {
            mostrarError("mensaje.cargar.error", e);
            caracteristicasPickList = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
        }
    }

    /**
     * Guarda las características asignadas con sus valores
     */
    private void guardarCaracteristicas(ProductoTipoProducto entidad) throws Exception {
        try {
            if (entidad.getId() == null) {
                return;
            }

            // Eliminar todas las características existentes
            productoTipoProductoCaracteristicaDAO.eliminarPorProductoTipoProducto(entidad.getId());

            // Insertar las características seleccionadas (target del PickList)
            List<TipoProductoCaracteristica> asignadas = caracteristicasPickList.getTarget();

            for (TipoProductoCaracteristica tpc : asignadas) {
                ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
                ptpc.setId(UUID.randomUUID());
                ptpc.setIdProductoTipoProducto(entidad);
                ptpc.setIdTipoProductoCaracteristica(tpc);

                // Obtener el valor del mapa
                String valor = valoresCaracteristicas.get(tpc.getId());
                ptpc.setValor(valor != null ? valor : "");

                productoTipoProductoCaracteristicaDAO.crear(ptpc);
            }

        } catch (Exception e) {
            throw new Exception("Error al guardar características: " + e.getMessage());
        }
    }

    /**
     * Valida que todas las características obligatorias estén en la lista de asignadas
     * @throws Exception si falta alguna característica obligatoria
     */
    private void validarCaracteristicasObligatorias() throws Exception {
        if (tipoProductoSeleccionado == null || tipoProductoSeleccionado.getId() == null) {
            return;
        }

        // Obtener todas las características del TipoProducto
        List<TipoProductoCaracteristica> todasCaracteristicas =
                tipoProductoCaracteristicaDAO.findByTipoProducto(tipoProductoSeleccionado.getId());

        // Obtener las IDs de las características asignadas (en el target del PickList)
        List<TipoProductoCaracteristica> asignadas = caracteristicasPickList.getTarget();
        Set<Long> idsAsignadas = asignadas.stream()
                .map(TipoProductoCaracteristica::getId)
                .collect(Collectors.toSet());

        // Verificar que todas las obligatorias estén asignadas
        List<String> faltantes = new ArrayList<>();
        for (TipoProductoCaracteristica tpc : todasCaracteristicas) {
            if (tpc.getObligatorio() != null && tpc.getObligatorio()) {
                if (!idsAsignadas.contains(tpc.getId())) {
                    // Falta una característica obligatoria
                    String nombreCaracteristica = tpc.getIdCaracteristica() != null ?
                            tpc.getIdCaracteristica().getNombre() : "Desconocida";
                    faltantes.add(nombreCaracteristica);
                }
            }
        }

        // Si hay características obligatorias faltantes, lanzar excepción
        if (!faltantes.isEmpty()) {
            String mensaje = "Debe asignar las siguientes características obligatorias: " +
                    String.join(", ", faltantes);
            throw new Exception(mensaje);
        }
    }

    /**
     * Obtiene la lista de tipos de producto activos
     */
    public List<TipoProducto> getTiposProductoActivos() {
        try {
            return tipoProductoDAO.findRange(0, 1000)
                    .stream()
                    .filter(t -> t.getActivo() != null && t.getActivo())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ==================== GETTERS Y SETTERS ====================

    public DualListModel<TipoProductoCaracteristica> getCaracteristicasPickList() {
        return caracteristicasPickList;
    }

    public void setCaracteristicasPickList(DualListModel<TipoProductoCaracteristica> caracteristicasPickList) {
        this.caracteristicasPickList = caracteristicasPickList;
    }

    public Map<Long, String> getValoresCaracteristicas() {
        return valoresCaracteristicas;
    }

    public void setValoresCaracteristicas(Map<Long, String> valoresCaracteristicas) {
        this.valoresCaracteristicas = valoresCaracteristicas;
    }

    public TipoProducto getTipoProductoSeleccionado() {
        return tipoProductoSeleccionado;
    }

    public void setTipoProductoSeleccionado(TipoProducto tipoProductoSeleccionado) {
        this.tipoProductoSeleccionado = tipoProductoSeleccionado;
    }
}
