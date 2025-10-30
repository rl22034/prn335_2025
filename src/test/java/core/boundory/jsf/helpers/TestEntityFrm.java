package core.boundory.jsf.helpers;

import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.DefaultFrm;
import java.util.ArrayList;
import java.util.List;

public class TestEntityFrm extends DefaultFrm<TestEntity> {

    private List<TestEntity> dataStore = new ArrayList<>();
    private Long contadorId = 1L;
    private boolean debeArrojarExcepcion = false;
    private String mensajeExcepcion = "Error genérico";

    @Override
    protected void crearEntidad(TestEntity entidad) throws Exception {
        if (debeArrojarExcepcion) {
            throw new Exception(mensajeExcepcion);
        }
        if (entidad.getId() == null) {
            entidad.setId(contadorId++);
        }
        dataStore.add(entidad);
    }

    @Override
    protected void actualizarEntidad(TestEntity entidad) throws Exception {
        if (debeArrojarExcepcion) {
            throw new Exception(mensajeExcepcion);
        }
        for (int i = 0; i < dataStore.size(); i++) {
            if (dataStore.get(i).getId().equals(entidad.getId())) {
                dataStore.set(i, entidad);
                return;
            }
        }
    }

    @Override
    protected void eliminarEntidad(TestEntity entidad) throws Exception {
        if (debeArrojarExcepcion) {
            throw new Exception(mensajeExcepcion);
        }
        dataStore.removeIf(e -> e.getId().equals(entidad.getId()));
    }

    @Override
    protected List<TestEntity> buscarEntidades(int first, int pageSize) throws Exception {
        if (debeArrojarExcepcion) {
            throw new Exception(mensajeExcepcion);
        }

        if (first >= dataStore.size()) {
            return new ArrayList<>();
        }

        int toIndex = Math.min(first + pageSize, dataStore.size());
        return new ArrayList<>(dataStore.subList(first, toIndex));
    }

    @Override
    protected Long contarEntidades() throws Exception {
        if (debeArrojarExcepcion) {
            throw new Exception(mensajeExcepcion);
        }
        return (long) dataStore.size();
    }

    @Override
    protected Object obtenerIdEntidad(TestEntity entidad) {
        return entidad != null ? entidad.getId() : null;
    }

    @Override
    protected TestEntity instanciarEntidad() {
        return new TestEntity();
    }

    public void setDebeArrojarExcepcion(boolean value) {
        this.debeArrojarExcepcion = value;
    }

    public void setMensajeExcepcion(String mensaje) {
        this.mensajeExcepcion = mensaje;
    }

    public void agregarEntidadesAlStore(List<TestEntity> entidades) {
        this.dataStore.addAll(entidades);
    }

    public List<TestEntity> getDataStore() {
        return new ArrayList<>(dataStore);
    }

    public void limpiarDataStore() {
        this.dataStore.clear();
        this.contadorId = 1L;
    }
}

