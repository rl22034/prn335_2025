package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("tipoProductoBean")
@ViewScoped
public class TipoProductoFrm implements Serializable {

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    // Para el TreeTable
    private TreeNode<TipoProducto> rootNode;
    private TreeNode<TipoProducto> selectedNode;

    // Para el formulario
    private TipoProducto filaSeleccionada;
    private CRUD estado = CRUD.NINGUNO;

    // Para el selector de padre
    private List<TipoProducto> tipoProductoList;

    // Para el tab de características
    private List<Caracteristica> caracteristicasList;

    @PostConstruct
    public void init() {
        estado = CRUD.NINGUNO;
        filaSeleccionada = instanciarEntidad();
        cargarArbol();
    }

    /**
     * Construye el árbol jerárquico de TipoProducto
     */
    public void cargarArbol() {
        try {
            // Obtener todos los tipos de producto
            List<TipoProducto> todos = tipoProductoDAO.findRange(0, 1000);

            // Crear nodo raíz invisible
            rootNode = new DefaultTreeNode<>(null, null);    // ✅ Infiere <TipoProducto>

            // Separar nodos raíz (sin padre) y nodos hijos
            List<TipoProducto> raices = todos.stream()
                    .filter(tp -> tp.getIdTipoProductoPadre() == null)
                    .collect(Collectors.toList());

            // Construir el árbol recursivamente
            for (TipoProducto raiz : raices) {
                construirNodo(raiz, rootNode, todos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Construye un nodo y sus hijos recursivamente
     */
    private void construirNodo(TipoProducto tipo, TreeNode<TipoProducto> nodoPadre, List<TipoProducto> todos) {
        // Crear el nodo actual
        TreeNode<TipoProducto> nodoActual = new DefaultTreeNode<>(tipo, nodoPadre);

        // Buscar los hijos de este nodo
        List<TipoProducto> hijos = todos.stream()
                .filter(tp -> tp.getIdTipoProductoPadre() != null &&
                        tp.getIdTipoProductoPadre().getId().equals(tipo.getId()))
                .collect(Collectors.toList());

        // Construir recursivamente cada hijo
        for (TipoProducto hijo : hijos) {
            construirNodo(hijo, nodoActual, todos);
        }
    }

    /**
     * Evento cuando se selecciona un nodo del árbol
     */
    public void onNodeSelect() {
        if (selectedNode != null) {
            filaSeleccionada = selectedNode.getData();
            estado = CRUD.MODIFICAR;
            cargarCaracteristicas();
        }
    }

    /**
     * Carga las características relacionadas con el TipoProducto seleccionado
     */
    public void cargarCaracteristicas() {
        try {
            if (filaSeleccionada != null && filaSeleccionada.getId() != null) {
                // Aquí cargaríamos las características a través de TipoProductoCaracteristica
                // Por ahora, cargamos todas las características disponibles
                caracteristicasList = caracteristicaDAO.findRange(0, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la lista de TipoProducto para el selector de padre
     */
    public List<TipoProducto> getTipoProductoList() {
        if (tipoProductoList == null) {
            try {
                tipoProductoList = tipoProductoDAO.findRange(0, 1000);

                // Filtrar para evitar que un tipo se seleccione a sí mismo como padre
                if (filaSeleccionada != null && filaSeleccionada.getId() != null) {
                    tipoProductoList = tipoProductoList.stream()
                            .filter(tp -> !tp.getId().equals(filaSeleccionada.getId()))
                            .collect(Collectors.toList());
                }
            } catch (Exception e) {
                e.printStackTrace();
                tipoProductoList = new ArrayList<>();
            }
        }
        return tipoProductoList;
    }

    // =================== MÉTODOS CRUD ===================

    public void btnNuevo() {
        try {
            filaSeleccionada = instanciarEntidad();
            selectedNode = null;
            estado = CRUD.CREAR;
            tipoProductoList = null; // Resetear para recargar
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnAgregar() {
        try {
            // Validaciones
            if (filaSeleccionada.getNombre() == null || filaSeleccionada.getNombre().trim().isEmpty()) {
                throw new Exception("validacion.nombre.requerido");
            }

            tipoProductoDAO.crear(filaSeleccionada);
            cargarArbol();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito");
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.NINGUNO;
            tipoProductoList = null; // Resetear para recargar
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.crear.error", e);
        }
    }

    public void btnActualizar() {
        try {
            // Validaciones
            if (filaSeleccionada.getNombre() == null || filaSeleccionada.getNombre().trim().isEmpty()) {
                throw new Exception("validacion.nombre.requerido");
            }

            tipoProductoDAO.update(filaSeleccionada);
            cargarArbol();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.NINGUNO;
            tipoProductoList = null; // Resetear para recargar
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.actualizar.error", e);
        }
    }

    public void btnEliminar() {
        try {
            TipoProducto original = tipoProductoDAO.finById(filaSeleccionada.getId());

            if (original == null) {
                throw new Exception("validacion.registro.no.existe");
            }

            if (!filaSeleccionada.getNombre().equals(original.getNombre())) {
                throw new Exception("validacion.nombre.cambiado");
            }

            tipoProductoDAO.delete(filaSeleccionada);
            cargarArbol();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito");
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.NINGUNO;
            tipoProductoList = null; // Resetear para recargar
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.eliminar.error", e);
        }
    }

    public void limpiarSeleccionado() {
        this.filaSeleccionada = null;
        this.selectedNode = null;
        this.estado = CRUD.NINGUNO;
    }

    private TipoProducto instanciarEntidad() {
        TipoProducto nuevo = new TipoProducto();
        nuevo.setActivo(true);
        return nuevo;
    }

    /**
     * Muestra un mensaje de error detectando si es una clave i18n o texto directo
     */
    private void mostrarError(String errorKey, Exception e) {
        String errorMsg = e.getMessage();

        // Detectar si es una clave de i18n o un texto normal
        if (errorMsg != null && !errorMsg.contains(" ") && errorMsg.contains(".")) {
            // Es una clave como "validacion.nombre.requerido"
            MessageHelper.addErrorMessage("mensaje.titulo.error", errorMsg);
        } else {
            // Es un texto normal o error de base de datos
            MessageHelper.addErrorMessage("mensaje.titulo.error", errorKey, errorMsg);
        }
    }

    // =================== GETTERS Y SETTERS ===================

    public TreeNode<TipoProducto> getRootNode() {
        return rootNode;
    }

    public void setRootNode(TreeNode<TipoProducto> rootNode) {
        this.rootNode = rootNode;
    }

    public TreeNode<TipoProducto> getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode<TipoProducto> selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TipoProducto getFilaSeleccionada() {
        if (filaSeleccionada == null) {
            filaSeleccionada = instanciarEntidad();
        }
        return filaSeleccionada;
    }

    public void setFilaSeleccionada(TipoProducto filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public CRUD getEstado() {
        return estado;
    }

    public void setEstado(CRUD estado) {
        this.estado = estado;
    }

    public TipoProductoDAO getTipoProductoDAO() {
        return tipoProductoDAO;
    }

    public void setTipoProductoDAO(TipoProductoDAO tipoProductoDAO) {
        this.tipoProductoDAO = tipoProductoDAO;
    }

    public CaracteristicaDAO getCaracteristicaDAO() {
        return caracteristicaDAO;
    }

    public void setCaracteristicaDAO(CaracteristicaDAO caracteristicaDAO) {
        this.caracteristicaDAO = caracteristicaDAO;
    }

    public List<Caracteristica> getCaracteristicasList() {
        return caracteristicasList;
    }

    public void setCaracteristicasList(List<Caracteristica> caracteristicasList) {
        this.caracteristicasList = caracteristicasList;
    }

    public void setTipoProductoList(List<TipoProducto> tipoProductoList) {
        this.tipoProductoList = tipoProductoList;
    }
}