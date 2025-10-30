package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Managed Bean para gestionar Productos con TipoProducto y Características
 */
@Named("productoBean")
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProductoFrm.class.getName());

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    @Inject
    private ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    private ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;

    // ========== VARIABLES PARA TABS ==========
    private int tabActivo = 0;

    // ========== VARIABLES PARA TIPO DE PRODUCTO ==========
    private List<TipoProducto> tipoProductoDisponiblesList;
    private Long tipoProductoSeleccionadoId; // Cambiado a Long
    private ProductoTipoProducto productoTipoProductoActual;
    private boolean mostrarDialogoTipo = false;

    // ========== VARIABLES PARA PICKLIST CARACTERÍSTICAS ==========
    private DualListModel<TipoProductoCaracteristica> pickListCaracteristicas;
    private Map<Long, String> valoresCaracteristicas; // Cambiado a Long

    // ========== VARIABLE PARA MODIFICAR CARACTERÍSTICA ==========
    private ProductoTipoProductoCaracteristica caracteristicaParaModificar;

    @PostConstruct
    @Override
    public void init() {
        super.init();
        tipoProductoDisponiblesList = new ArrayList<>();
        valoresCaracteristicas = new HashMap<>();
        pickListCaracteristicas = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
    }

    // ========== MÉTODOS ABSTRACTOS OBLIGATORIOS ==========

    @Override
    protected void crearEntidad(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        productoDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        productoDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Producto entidad) throws Exception {
        productoDAO.delete(entidad);
    }

    @Override
    protected List<Producto> buscarEntidades(int first, int pageSize) throws Exception {
        return productoDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return productoDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Producto entidad) {
        return entidad.getId();
    }

    @Override
    protected Producto instanciarEntidad() {
        Producto nuevo = new Producto();
        nuevo.setActivo(true);
        return nuevo;
    }

    // ========== MANEJO DE TABS ==========

    public void onTabChange(TabChangeEvent<?> event) {
        try {
            int tabIndex = ((org.primefaces.component.tabview.TabView) event.getComponent()).getActiveIndex();
            this.tabActivo = tabIndex;

            LOGGER.log(Level.INFO, "Tab cambiado a índice: {0}", tabIndex);

            if (tabIndex == 1) { // Tab de Tipos de Producto
                cargarTiposDeProducto();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cambiar de tab", e);
        }
    }

    // ========== GESTIÓN DE TIPO DE PRODUCTO ==========

    /**
     * Carga la lista de tipos de producto disponibles
     */
    private void cargarTiposDeProducto() {
        try {
            tipoProductoDisponiblesList = tipoProductoDAO.findRange(0, 1000);
            LOGGER.log(Level.INFO, "Tipos de producto cargados: {0}", tipoProductoDisponiblesList.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar tipos de producto", e);
            MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.cargar.error");
        }
    }

    /**
     * Abre el diálogo para seleccionar un tipo de producto
     */
    public void abrirDialogoSeleccionarTipo() {
        try {
            if (filaSeleccionada == null || filaSeleccionada.getId() == null) {
                MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.seleccionar.producto");
                return;
            }

            cargarTiposDeProducto();
            tipoProductoSeleccionadoId = null;
            mostrarDialogoTipo = true;

            LOGGER.log(Level.INFO, "Diálogo de selección de tipo abierto para producto: {0}",
                    filaSeleccionada.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al abrir diálogo de tipo", e);
            MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.error.general");
        }
    }

    /**
     * Confirma la selección del tipo de producto y carga las características
     */
    public void confirmarSeleccionTipo() {
        try {
            if (tipoProductoSeleccionadoId == null) {
                MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.seleccionar.tipo");
                return;
            }

            // Verificar si ya existe esta relación
            boolean existe = productoTipoProductoDAO.existeRelacion(
                    filaSeleccionada.getId(),
                    tipoProductoSeleccionadoId
            );

            if (existe) {
                MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.tipo.ya.asignado");
                return;
            }

            // Buscar el TipoProducto completo
            TipoProducto tipoProducto = tipoProductoDAO.finById(tipoProductoSeleccionadoId);
            if (tipoProducto == null) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.tipo.no.encontrado");
                return;
            }

            // Crear la relación ProductoTipoProducto
            ProductoTipoProducto ptp = new ProductoTipoProducto();
            // No establecer ID, dejar que la base de datos lo genere automáticamente
            ptp.setIdProducto(filaSeleccionada);
            ptp.setIdTipoProducto(tipoProducto);
            ptp.setFechaCreacion(OffsetDateTime.now());
            ptp.setActivo(true);

            productoTipoProductoDAO.crear(ptp);
            productoTipoProductoActual = ptp;

            // Cargar características disponibles para el PickList
            cargarCaracteristicasParaPickList();

            // Cerrar el diálogo
            mostrarDialogoTipo = false;
            tipoProductoSeleccionadoId = null;

            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.tipo.asignado.exito");

            LOGGER.log(Level.INFO, "Tipo de producto asignado exitosamente: {0}", tipoProducto.getNombre());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al confirmar selección de tipo", e);
            MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.error.crear");
        }
    }

    /**
     * Cancela la selección de tipo
     */
    public void cancelarSeleccionTipo() {
        mostrarDialogoTipo = false;
        tipoProductoSeleccionadoId = null;
    }

    // ========== GESTIÓN DE PICKLIST CARACTERÍSTICAS ==========

    /**
     * Carga las características disponibles y asignadas para el PickList
     */
    private void cargarCaracteristicasParaPickList() {
        try {
            if (productoTipoProductoActual == null || productoTipoProductoActual.getId() == null) {
                pickListCaracteristicas = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
                return;
            }

            // Obtener características disponibles (no asignadas)
            List<TipoProductoCaracteristica> disponibles =
                    productoTipoProductoCaracteristicaDAO.findCaracteristicasDisponibles(
                            productoTipoProductoActual.getId(),
                            productoTipoProductoActual.getIdTipoProducto().getId()
                    );

            // Obtener características ya asignadas
            List<ProductoTipoProductoCaracteristica> asignadas =
                    productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(
                            productoTipoProductoActual.getId()
                    );

            // Convertir las asignadas a TipoProductoCaracteristica
            // ✅ LÍNEA CORREGIDA (funciona en Java 8+)
            List<TipoProductoCaracteristica> asignadasList = asignadas.stream()
                    .map(ProductoTipoProductoCaracteristica::getIdTipoProductoCaracteristica)
                    .collect(Collectors.toList());

            // Cargar los valores existentes
            valoresCaracteristicas.clear();
            for (ProductoTipoProductoCaracteristica ptpc : asignadas) {
                valoresCaracteristicas.put(
                        ptpc.getIdTipoProductoCaracteristica().getId(),
                        ptpc.getValor()
                );
            }

            pickListCaracteristicas = new DualListModel<>(disponibles, asignadasList);

            LOGGER.log(Level.INFO, "PickList cargado - Disponibles: {0}, Asignadas: {1}",
                    new Object[]{disponibles.size(), asignadasList.size()});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar PickList de características", e);
            pickListCaracteristicas = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
        }
    }

    /**
     * Guarda las características seleccionadas en el PickList
     */
    public void guardarCaracteristicas() {
        try {
            if (productoTipoProductoActual == null || productoTipoProductoActual.getId() == null) {
                MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.seleccionar.tipo.primero");
                return;
            }

            List<TipoProductoCaracteristica> target = pickListCaracteristicas.getTarget();

            // Eliminar todas las características actuales
            productoTipoProductoCaracteristicaDAO.deleteByProductoTipoProducto(
                    productoTipoProductoActual.getId()
            );

            // Crear las nuevas asignaciones
            for (TipoProductoCaracteristica tpc : target) {
                ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
                // No establecer ID, dejar que la base de datos lo genere automáticamente
                ptpc.setIdProductoTipoProducto(productoTipoProductoActual);
                ptpc.setIdTipoProductoCaracteristica(tpc);

                // Obtener el valor del mapa
                String valor = valoresCaracteristicas.get(tpc.getId());
                ptpc.setValor(valor != null ? valor : "");

                productoTipoProductoCaracteristicaDAO.crear(ptpc);
            }

            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.caracteristicas.guardadas");

            // Recargar el PickList
            cargarCaracteristicasParaPickList();

            LOGGER.log(Level.INFO, "Características guardadas: {0}", target.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar características", e);
            MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.error.guardar.caracteristicas");
        }
    }

    /**
     * Abre el diálogo para modificar el valor de una característica
     */
    public void abrirDialogoModificarCaracteristica(TipoProductoCaracteristica tpc) {
        try {
            // Buscar la característica asignada
            ProductoTipoProductoCaracteristica ptpc =
                    productoTipoProductoCaracteristicaDAO.findByProductoTipoProductoAndCaracteristica(
                            productoTipoProductoActual.getId(),
                            tpc.getId()
                    );

            if (ptpc != null) {
                caracteristicaParaModificar = ptpc;
                LOGGER.log(Level.INFO, "Abriendo diálogo para modificar característica: {0}",
                        tpc.getIdCaracteristica().getNombre());
            } else {
                MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.caracteristica.no.encontrada");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al abrir diálogo de modificación", e);
            MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.error.general");
        }
    }

    /**
     * Guarda la modificación del valor de una característica
     */
    public void guardarModificacionCaracteristica() {
        try {
            if (caracteristicaParaModificar == null) {
                MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.seleccionar.caracteristica");
                return;
            }

            productoTipoProductoCaracteristicaDAO.update(caracteristicaParaModificar);
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.caracteristica.actualizada");

            // Actualizar el mapa de valores
            valoresCaracteristicas.put(
                    caracteristicaParaModificar.getIdTipoProductoCaracteristica().getId(),
                    caracteristicaParaModificar.getValor()
            );

            caracteristicaParaModificar = null;

            LOGGER.log(Level.INFO, "Característica modificada exitosamente");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar modificación", e);
            MessageHelper.addErrorMessage("mensaje.titulo.error", "mensaje.error.actualizar");
        }
    }

    /**
     * Cancela la modificación de característica
     */
    public void cancelarModificacionCaracteristica() {
        caracteristicaParaModificar = null;
    }

    /**
     * Cancela la edición de tipos de producto
     */
    public void cancelarTiposDeProducto() {
        productoTipoProductoActual = null;
        pickListCaracteristicas = null;
        valoresCaracteristicas.clear();
    }

    // ========== GETTERS Y SETTERS ==========

    public int getTabActivo() {
        return tabActivo;
    }

    public void setTabActivo(int tabActivo) {
        this.tabActivo = tabActivo;
    }

    public List<TipoProducto> getTipoProductoDisponiblesList() {
        return tipoProductoDisponiblesList;
    }

    public void setTipoProductoDisponiblesList(List<TipoProducto> tipoProductoDisponiblesList) {
        this.tipoProductoDisponiblesList = tipoProductoDisponiblesList;
    }

    public Long getTipoProductoSeleccionadoId() {
        return tipoProductoSeleccionadoId;
    }

    public void setTipoProductoSeleccionadoId(Long tipoProductoSeleccionadoId) {
        this.tipoProductoSeleccionadoId = tipoProductoSeleccionadoId;
    }

    public ProductoTipoProducto getProductoTipoProductoActual() {
        return productoTipoProductoActual;
    }

    public void setProductoTipoProductoActual(ProductoTipoProducto productoTipoProductoActual) {
        this.productoTipoProductoActual = productoTipoProductoActual;
    }

    public boolean isMostrarDialogoTipo() {
        return mostrarDialogoTipo;
    }

    public void setMostrarDialogoTipo(boolean mostrarDialogoTipo) {
        this.mostrarDialogoTipo = mostrarDialogoTipo;
    }

    public DualListModel<TipoProductoCaracteristica> getPickListCaracteristicas() {
        return pickListCaracteristicas;
    }

    public void setPickListCaracteristicas(DualListModel<TipoProductoCaracteristica> pickListCaracteristicas) {
        this.pickListCaracteristicas = pickListCaracteristicas;
    }

    public Map<Long, String> getValoresCaracteristicas() {
        return valoresCaracteristicas;
    }

    public void setValoresCaracteristicas(Map<Long, String> valoresCaracteristicas) {
        this.valoresCaracteristicas = valoresCaracteristicas;
    }

    public ProductoTipoProductoCaracteristica getCaracteristicaParaModificar() {
        return caracteristicaParaModificar;
    }

    public void setCaracteristicaParaModificar(ProductoTipoProductoCaracteristica caracteristicaParaModificar) {
        this.caracteristicaParaModificar = caracteristicaParaModificar;
    }

    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }

    public void setProductoDAO(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
}