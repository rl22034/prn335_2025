package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;

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
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    private TreeNode<TipoProducto> rootNode;
    private TreeNode<TipoProducto> selectedNode;
    private TipoProducto filaSeleccionada;
    private List<TipoProducto> tipoProductoList;
    private List<TipoProductoCaracteristica> tipoProductoCaracteristicasList;
    private Long idPadreSeleccionado;
    private CRUD estado = CRUD.NINGUNO;

    @PostConstruct
    public void init() {
        estado = CRUD.NINGUNO;
        filaSeleccionada = new TipoProducto();
        filaSeleccionada.setActivo(true);
        cargarArbol();
        getTipoProductoList();
    }

    public void btnNuevo() {
        filaSeleccionada = new TipoProducto();
        filaSeleccionada.setActivo(true);
        estado = CRUD.CREAR;
        selectedNode = null;
        //tipoProductoList = null;
        tipoProductoCaracteristicasList = new ArrayList<>();
        idPadreSeleccionado = null;
    }

    public void btnAgregar() {
        try {
            if (filaSeleccionada.getNombre() == null || filaSeleccionada.getNombre().trim().isEmpty()) {
                throw new Exception("validacion.nombre.requerido");
            }

            if (idPadreSeleccionado != null) {
                TipoProducto padre = tipoProductoDAO.finById(idPadreSeleccionado);
                filaSeleccionada.setIdTipoProductoPadre(padre);
            } else {
                filaSeleccionada.setIdTipoProductoPadre(null);
            }

            tipoProductoDAO.crear(filaSeleccionada);
            cargarArbol();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito");

            filaSeleccionada = new TipoProducto();
            filaSeleccionada.setActivo(true);
            estado = CRUD.NINGUNO;
            idPadreSeleccionado = null;
            tipoProductoList = null;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.crear.error", e);
        }
    }

    public void btnActualizar() {
        try {
            if (filaSeleccionada.getNombre() == null || filaSeleccionada.getNombre().trim().isEmpty()) {
                throw new Exception("validacion.nombre.requerido");
            }

            if (idPadreSeleccionado != null) {
                TipoProducto padre = tipoProductoDAO.finById(idPadreSeleccionado);
                filaSeleccionada.setIdTipoProductoPadre(padre);
            } else {
                filaSeleccionada.setIdTipoProductoPadre(null);
            }

            tipoProductoDAO.update(filaSeleccionada);
            cargarArbol();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");

            filaSeleccionada = new TipoProducto();
            filaSeleccionada.setActivo(true);
            estado = CRUD.NINGUNO;
            idPadreSeleccionado = null;
            tipoProductoList = null;
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

            filaSeleccionada = new TipoProducto();
            filaSeleccionada.setActivo(true);
            estado = CRUD.NINGUNO;
            idPadreSeleccionado = null;
            tipoProductoList = null;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.eliminar.error", e);
        }
    }

    public void cargarArbol() {
        try {
            List<TipoProducto> todos = tipoProductoDAO.findRange(0, 1000);
            rootNode = new DefaultTreeNode<>(null, null);
            rootNode.setExpanded(true);

            List<TipoProducto> raices = todos.stream()
                    .filter(tp -> tp.getIdTipoProductoPadre() == null)
                    .collect(Collectors.toList());

            for (TipoProducto raiz : raices) {
                construirNodo(raiz, rootNode, todos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void construirNodo(TipoProducto tipo, TreeNode<TipoProducto> nodoPadre, List<TipoProducto> todos) {
        TreeNode<TipoProducto> nodoActual = new DefaultTreeNode<>(tipo, nodoPadre);
        nodoActual.setExpanded(false);

        List<TipoProducto> hijos = todos.stream()
                .filter(tp -> tp.getIdTipoProductoPadre() != null &&
                        tp.getIdTipoProductoPadre().getId() != null &&
                        tipo.getId() != null &&
                        tp.getIdTipoProductoPadre().getId().equals(tipo.getId()))
                .collect(Collectors.toList());

        for (TipoProducto hijo : hijos) {
            construirNodo(hijo, nodoActual, todos);
        }
    }

    public void onNodeSelect() {
        if (selectedNode != null) {
            filaSeleccionada = selectedNode.getData();
            estado = CRUD.MODIFICAR;

            if (filaSeleccionada.getIdTipoProductoPadre() != null) {
                idPadreSeleccionado = filaSeleccionada.getIdTipoProductoPadre().getId();
            } else {
                idPadreSeleccionado = null;
            }

            tipoProductoList = null;
            cargarCaracteristicas();
        }
    }

    public void cargarCaracteristicas() {
        try {
            if (filaSeleccionada != null && filaSeleccionada.getId() != null) {
                tipoProductoCaracteristicasList = tipoProductoCaracteristicaDAO.findByTipoProducto(filaSeleccionada.getId());
            } else {
                tipoProductoCaracteristicasList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            tipoProductoCaracteristicasList = new ArrayList<>();
        }
    }

    private void mostrarError(String errorKey, Exception e) {
        String errorMsg = e.getMessage();
        if (errorMsg != null && !errorMsg.contains(" ") && errorMsg.contains(".")) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", errorMsg);
        } else {
            MessageHelper.addErrorMessage("mensaje.titulo.error", errorKey, errorMsg);
        }
    }

    private void obtenerDescendientes(Long idPadre, List<TipoProducto> todos, List<Long> idsExcluir) {
        List<TipoProducto> hijos = todos.stream()
                .filter(tp -> tp.getIdTipoProductoPadre() != null &&
                        tp.getIdTipoProductoPadre().getId() != null &&
                        tp.getIdTipoProductoPadre().getId().equals(idPadre))
                .collect(Collectors.toList());

        for (TipoProducto hijo : hijos) {
            idsExcluir.add(hijo.getId());
            obtenerDescendientes(hijo.getId(), todos, idsExcluir);
        }
    }

    private int calcularNivel(TipoProducto tipo, List<TipoProducto> todos) {
        int nivel = 0;
        TipoProducto actual = tipo;

        while (actual.getIdTipoProductoPadre() != null) {
            nivel++;
            Long idPadre = actual.getIdTipoProductoPadre().getId();
            TipoProducto padre = todos.stream()
                    .filter(tp -> tp.getId().equals(idPadre))
                    .findFirst()
                    .orElse(null);

            if (padre == null) break;
            actual = padre;
        }

        return nivel;
    }

    public List<TipoProducto> getTipoProductoList() {
        if (tipoProductoList == null) {
            try {
                List<TipoProducto> todos = tipoProductoDAO.findRange(0, 1000);

                if (estado == CRUD.MODIFICAR && filaSeleccionada != null && filaSeleccionada.getId() != null) {
                    List<Long> idsExcluir = new ArrayList<>();
                    idsExcluir.add(filaSeleccionada.getId());
                    obtenerDescendientes(filaSeleccionada.getId(), todos, idsExcluir);

                    int nivelActual = calcularNivel(filaSeleccionada, todos);

                    tipoProductoList = todos.stream()
                            .filter(tp -> {
                                if (idsExcluir.contains(tp.getId())) {
                                    return false;
                                }
                                int nivelTp = calcularNivel(tp, todos);
                                return nivelTp <= nivelActual;
                            })
                            .collect(Collectors.toList());
                } else {
                    tipoProductoList = todos;
                }
            } catch (Exception e) {
                e.printStackTrace();
                tipoProductoList = new ArrayList<>();
            }
        }
        return tipoProductoList;
    }

    // Getters y Setters
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
            filaSeleccionada = new TipoProducto();
            filaSeleccionada.setActivo(true);
        }
        return filaSeleccionada;
    }

    public void setFilaSeleccionada(TipoProducto filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public List<TipoProductoCaracteristica> getTipoProductoCaracteristicasList() {
        if (tipoProductoCaracteristicasList == null) {
            tipoProductoCaracteristicasList = new ArrayList<>();
        }
        return tipoProductoCaracteristicasList;
    }

    public void setTipoProductoCaracteristicasList(List<TipoProductoCaracteristica> tipoProductoCaracteristicasList) {
        this.tipoProductoCaracteristicasList = tipoProductoCaracteristicasList;
    }

    public void setTipoProductoList(List<TipoProducto> tipoProductoList) {
        this.tipoProductoList = tipoProductoList;
    }

    public Long getIdPadreSeleccionado() {
        return idPadreSeleccionado;
    }

    public void setIdPadreSeleccionado(Long idPadreSeleccionado) {
        this.idPadreSeleccionado = idPadreSeleccionado;
    }

    public CRUD getEstado() {
        return estado;
    }

    public void setEstado(CRUD estado) {
        this.estado = estado;
    }
}