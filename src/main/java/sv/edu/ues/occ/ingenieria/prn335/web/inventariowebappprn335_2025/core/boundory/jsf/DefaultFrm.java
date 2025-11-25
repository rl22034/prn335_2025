package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.InventarioDefaultDataAccess;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class DefaultFrm<T> implements Serializable {

    protected List<T> entidadesList;
    protected LazyDataModel<T> lazyModel;
    protected T filaSeleccionada;
    protected CRUD estado = CRUD.NINGUNO;

    // Métodos abstractos que cada clase hija debe implementar
    protected abstract InventarioDefaultDataAccess<T> obtenerDAO();
    protected abstract T instanciarEntidad();

    // Métodos hook para validaciones (opcionales - sobrescribir solo si se necesita)
    protected void validarAntesDeCrear(T entidad) throws Exception {
        // Método vacío - las clases hijas sobrescriben solo si necesitan validar antes de crear
    }

    protected void validarAntesDeActualizar(T entidad) throws Exception {
        // Método vacío - las clases hijas sobrescriben solo si necesitan validar antes de actualizar
    }

    protected void validarAntesDeEliminar(T entidad, T original) throws Exception {
        // Método vacío - las clases hijas sobrescriben solo si necesitan validar antes de eliminar
    }

    // Métodos con implementación Template Method
    /**
     * Crea una nueva entidad con validaciones comunes.
     *
     * Validaciones automáticas:
     * - Verifica que la entidad no sea null
     * - Verifica que el ID sea null (entidad nueva)
     * - Llama a validaciones personalizadas (hook)
     * - Guarda en BD
     */
    protected void crearEntidad(T entidad) throws Exception {
        // Validación común: entidad no debe ser null
        if (entidad == null) {
            throw new Exception("validacion.entidad.nula");
        }

        // Validación común: el ID debe ser null para crear (evita sobrescribir registros)
        Object id = obtenerIdEntidad(entidad);
        if (id != null) {
            throw new Exception("validacion.entidad.id.debe.ser.nulo");
        }

        // Validaciones personalizadas (hook para clases hijas)
        validarAntesDeCrear(entidad);

        // Crear en BD
        obtenerDAO().crear(entidad);
    }

    /**
     * Actualiza una entidad existente con validaciones comunes.
     *
     * Validaciones automáticas:
     * - Verifica que la entidad no sea null
     * - Verifica que el ID NO sea null (debe existir)
     * - Busca el registro original en BD
     * - Verifica que el registro original exista
     * - Llama a validaciones personalizadas (hook)
     * - Actualiza en BD
     */
    protected void actualizarEntidad(T entidad) throws Exception {
        // Validación común: entidad no debe ser null
        if (entidad == null) {
            throw new Exception("validacion.entidad.nula");
        }

        // Validación común: el ID NO debe ser null para actualizar
        Object id = obtenerIdEntidad(entidad);
        if (id == null) {
            throw new Exception("validacion.entidad.id.requerido");
        }

        // Validación común: el registro debe existir en BD
        T original = obtenerDAO().finById(id);
        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        // Validaciones personalizadas (hook para clases hijas)
        // Recibe la entidad actual y la original para comparar
        validarAntesDeActualizar(entidad);

        // Actualizar en BD
        obtenerDAO().update(entidad);
    }

    /**
     * Elimina una entidad con validaciones comunes.
     *
     * Validaciones automáticas:
     * - Verifica que la entidad no sea null
     * - Verifica que el ID NO sea null
     * - Busca el registro original en BD
     * - Verifica que el registro original exista
     * - Llama a validaciones personalizadas (hook)
     * - Elimina de BD
     */
    protected void eliminarEntidad(T entidad) throws Exception{
        // Validación común: entidad no debe ser null
        if (entidad == null) {
            throw new Exception("validacion.entidad.nula");
        }

        // Validación común: el ID NO debe ser null
        Object id = obtenerIdEntidad(entidad);
        if (id == null) {
            throw new Exception("validacion.entidad.id.requerido");
        }

        // Validación común: el registro debe existir en BD
        T original = obtenerDAO().finById(id);
        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        // Validaciones personalizadas (hook para clases hijas)
        validarAntesDeEliminar(entidad, original);

        // Eliminar de BD
        obtenerDAO().delete(entidad);
    }


    // Métodos con implementación por defecto (pueden ser sobrescritos si se necesita lógica diferente)
    protected List<T> buscarEntidades(int first, int pageSize) throws Exception {
        return obtenerDAO().findRange(first, pageSize);
    }

    protected Long contarEntidades() throws Exception {
        return obtenerDAO().count();
    }

    protected Object obtenerIdEntidad(T entidad) {
        // Asume que todas las entidades tienen un método getId()
        // Si alguna entidad no lo tiene, el FRM hijo puede sobrescribir este método
        try {
            return entidad.getClass().getMethod("getId").invoke(entidad);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el ID de la entidad. Sobrescribe obtenerIdEntidad() en el FRM hijo.", e);
        }
    }

    @PostConstruct
    public void init() {
        estado = CRUD.NINGUNO;
        filaSeleccionada = instanciarEntidad();
    }

    public void findRange() {
        try {
            entidadesList = buscarEntidades(0, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRowSelect(SelectEvent<T> event) {
        T seleccionado = event.getObject();
        this.filaSeleccionada = seleccionado;
        estado = CRUD.MODIFICAR;
    }

    public void initLazyModel() {
        lazyModel = new LazyDataModel<T>() {

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                try {
                    Long total = contarEntidades();
                    return total.intValue();
                } catch (Exception e) {
                    mostrarError("mensaje.contar.error", e);
                    return 0;
                }
            }

            @Override
            public List<T> load(int first, int pageSize,
                                Map<String, SortMeta> sortBy,
                                Map<String, FilterMeta> filterBy) {
                try {
                    List<T> resultado = buscarEntidades(first, pageSize);
                    if (resultado == null) {
                        return Collections.emptyList();
                    }
                    return resultado;
                } catch (Exception e) {
                    mostrarError("mensaje.cargar.error", e);
                    return Collections.emptyList();
                }
            }

            @Override
            public T getRowData(String rowKey) {
                List<T> lista = (List<T>) getWrappedData();
                if (lista != null) {
                    for (T tipo : lista) {
                        if (obtenerIdEntidad(tipo).toString().equals(rowKey)) {
                            return tipo;
                        }
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(T object) {
                Object id = obtenerIdEntidad(object);
                return id != null ? id.toString() : null;
            }
        };
    }

    public void limpiarSeleccionado() {
        this.filaSeleccionada = null;
        this.estado = CRUD.NINGUNO;
    }

    public void btnNuevo() {
        try {
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.CREAR;

        } catch (Exception e) {
            System.out.println("Error en btnNuevo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnAgregar() {
        try {
            crearEntidad(filaSeleccionada);
            initLazyModel();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito");
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.NINGUNO;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.crear.error", e);
        }
    }

    public void btnEliminar() {
        try {
            eliminarEntidad(filaSeleccionada);
            MessageHelper.addInfoMessage(
                    "mensaje.titulo.exito",
                    "mensaje.eliminar.exito"
            );
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.NINGUNO;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.eliminar.error", e);
        }
    }

    public void btnActualizar() {
        try {
            actualizarEntidad(filaSeleccionada);
            MessageHelper.addInfoMessage(
                    "mensaje.titulo.exito",
                    "mensaje.actualizar.exito"
            );
            filaSeleccionada = instanciarEntidad();
            estado = CRUD.NINGUNO;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("mensaje.actualizar.error", e);
        }
    }

    /**
     * Muestra un mensaje de error detectando si es una clave i18n o texto directo.
     * Si el mensaje no contiene espacios y tiene puntos, se asume que es una clave.
     *
     * @param errorKey Clave del mensaje de error base
     * @param e Excepcion capturada
     */
    void mostrarError(String errorKey, Exception e) {
        String errorMsg = e.getMessage();

        // Buscar en toda la cadena de excepciones para errores de FK
        Throwable causa = e;
        while (causa != null) {
            String msg = causa.getMessage();
            if (msg != null) {
                String msgLower = msg.toLowerCase();
                if (msgLower.contains("foreign key") || msgLower.contains("fk_") ||
                    msgLower.contains("still referenced") || msgLower.contains("violates")) {
                    MessageHelper.addErrorMessage("mensaje.titulo.error", "validacion.fk.registros.relacionados");
                    return;
                }
            }
            causa = causa.getCause();
        }

        // Detectar si es una clave de i18n o un texto normal
        if (errorMsg != null && !errorMsg.contains(" ") && errorMsg.contains(".")) {
            // Es una clave como "validacion.nombre.requerido"
            MessageHelper.addErrorMessage("mensaje.titulo.error", errorMsg);
        } else {
            // Es un texto normal o error de base de datos
            MessageHelper.addErrorMessage("mensaje.titulo.error", errorKey, errorMsg);
        }
    }

    // Getters y Setters
    public T getFilaSeleccionada() {
        if (filaSeleccionada == null) {
            filaSeleccionada = instanciarEntidad();
        }
        return filaSeleccionada;
    }

    public void setFilaSeleccionada(T filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public List<T> getEntidadesList() {
        return entidadesList;
    }

    public void setEntidadesList(List<T> entidadesList) {
        this.entidadesList = entidadesList;
    }

    public LazyDataModel<T> getLazyModel() {

        if (lazyModel == null) {
            initLazyModel();
        }
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<T> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public CRUD getEstado() {
        return estado;
    }

    public void setEstado(CRUD estado) {
        this.estado = estado;
    }
}