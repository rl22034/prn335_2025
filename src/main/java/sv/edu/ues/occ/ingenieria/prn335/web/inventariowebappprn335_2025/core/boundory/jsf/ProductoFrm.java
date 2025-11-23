package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ReporteKardex;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import java.io.Serializable;
import java.util.*;

@Named("productoBean")
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> implements Serializable {

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    ReporteKardex reporteKardex;

    private List<Producto> productos;
    private String idProductoReporte;
    private Date fechaInicio;
    private Date fechaFin;

    private void cargarTodosLosProductos() {
        try {
            // Llama a tu findRange con valores que traigan todo (o un rango grande)
            // Usamos un rango grande (0 a 1000) si no tienes un método findAll específico
            this.productos = productoDAO.findRange(0, 1000);
        } catch (Exception e) {
            System.err.println("Error al cargar productos para combo: " + e.getMessage());
            this.productos = new java.util.ArrayList<>();
        }
    }
    @Override
    @PostConstruct
    public void init() {
        super.init();
        cargarTodosLosProductos();
    }
    @Override
    protected ProductoDAO obtenerDAO() {
        return productoDAO;
    }

    @Override
    protected void validarAntesDeCrear(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeEliminar(Producto entidad, Producto original)
            throws Exception {
        if (!entidad.getNombreProducto().equals(original.getNombreProducto())) {
            throw new Exception("validacion.nombre.cambiado");
        }
    }

    @Override
    protected Producto instanciarEntidad() {
        Producto nuevo = new Producto();
        nuevo.setActivo(true);
        return nuevo;
    }

    public void descargarKardex() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            // Validaciones
            if (this.idProductoReporte == null || this.idProductoReporte.trim().isEmpty()) {
                MessageHelper.addErrorMessage("Error", "Debe seleccionar un producto.");
                return; // Retorna sin tocar la respuesta
            }
            if (fechaInicio == null || fechaFin == null) {
                MessageHelper.addErrorMessage("Error", "Debe seleccionar el rango de fechas.");
                return; // Retorna sin tocar la respuesta
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("P_ID_PRODUCTO", this.idProductoReporte);
            parametros.put("P_FECHA_INICIO", new java.sql.Timestamp(fechaInicio.getTime()));
            parametros.put("P_FECHA_FIN", new java.sql.Timestamp(fechaFin.getTime()));

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            // 1. OBTENER EL NOMBRE (Lógica segura)
            String nombreArchivo = "Reporte_Kardex";
            try {
                UUID uuidProd = UUID.fromString(this.idProductoReporte);
                Producto prod = productoDAO.finById(uuidProd);
                if (prod != null) {
                    // Limpiar nombre de caracteres raros
                    nombreArchivo = "Kardex_" + prod.getNombreProducto().replaceAll("[^a-zA-Z0-9.-]", "_");
                }
            } catch (Exception ignored) {}

            // 2. PREPARAR RESPUESTA
            response.reset(); // ¡Importante! Limpia el buffer
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + ".pdf\"");

            // 3. GENERAR REPORTE
            // Si esto falla, saltará al catch
            reporteKardex.generarPdf("ReporteProducto", parametros, response.getOutputStream());

            // 4. FINALIZAR (ÉXITO)
            facesContext.responseComplete();

        } catch (Exception e) {
            // 🛑 AQUÍ ESTÁ EL ERROR REAL
            System.err.println(">>> ERROR GENERANDO REPORTE: " + e.getMessage());
            e.printStackTrace(); // Mira esto en la consola de IntelliJ

            // FINALIZAR TAMBIÉN EN CASO DE ERROR para evitar SRVE0199E
            facesContext.responseComplete();
        }
    }

    public ReporteKardex getReporteKardex() {
        return reporteKardex;
    }

    public void setReporteKardex(ReporteKardex reporteKardex) {
        this.reporteKardex = reporteKardex;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public String getIdProductoReporte() {
        return idProductoReporte;
    }

    public void setIdProductoReporte(String idProductoReporte) {
        this.idProductoReporte = idProductoReporte;
    }

    public List<Producto> getProductosDeCompra(Long idCompra) {
        return productoDAO.findByCompra(idCompra);
    }

    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }

    public void setProductoDAO(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
}













