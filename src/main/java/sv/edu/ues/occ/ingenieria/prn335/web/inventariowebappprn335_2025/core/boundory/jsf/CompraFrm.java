package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Named("compraBean")
@ViewScoped
public class CompraFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    private CompraDAO compraDAO;

    @Override
    protected void crearEntidad(Compra entidad) throws Exception {
        // Validaciones básicas
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, proveedor y estado son obligatorios");
        }

        // CRÍTICO: Por el DDL, el id_compra DEBE ser igual al id_proveedor
        if (entidad.getProveedor() != null && entidad.getProveedor().getId() != null) {
            Long idProveedor = entidad.getProveedor().getId().longValue();
            entidad.setId(idProveedor);
        } else {
            throw new Exception("Debe seleccionar un proveedor válido");
        }

        // Verificar que no exista ya una compra para ese proveedor
        Compra existente = compraDAO.finById(entidad.getId());
        if (existente != null) {
            throw new Exception("Ya existe una compra para el proveedor seleccionado. Cada proveedor solo puede tener una compra.");
        }

        compraDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Compra entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, proveedor y estado son obligatorios");
        }

        // No se puede cambiar el proveedor porque cambiaría el ID
        Compra original = compraDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("La compra no existe");
        }

        if (!original.getProveedor().getId().equals(entidad.getProveedor().getId())) {
            throw new Exception("No se puede cambiar el proveedor de una compra existente");
        }

        compraDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Compra entidad) throws Exception {
        Compra original = compraDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("La compra no existe en la base de datos");
        }

        compraDAO.delete(entidad);
    }

    @Override
    protected List<Compra> buscarEntidades(int first, int pageSize) throws Exception {
        return compraDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return compraDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Compra entidad) {
        return entidad.getId();
    }

    @Override
    protected Compra instanciarEntidad() {
        Compra nuevo = new Compra();

        // NO asignamos ID aquí - se asignará cuando se seleccione el proveedor
        // Valores por defecto
        nuevo.setFecha(OffsetDateTime.now(ZoneId.of("America/El_Salvador")));
        nuevo.setEstado("PENDIENTE");

        return nuevo;
    }

    public Compra getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Compra filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Compra> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Compra> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public CompraDAO getCompraDAO() {
        return compraDAO;
    }

    public void setCompraDAO(CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }
}