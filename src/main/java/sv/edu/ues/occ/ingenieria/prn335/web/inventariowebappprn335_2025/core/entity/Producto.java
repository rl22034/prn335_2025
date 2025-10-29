package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "producto", schema = "public")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_producto", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Size(max = 155)
    @Column(name = "nombre_producto", length = 155)
    private String nombreProducto;

    @Lob
    @Column(name = "referencia_externa")
    private String referenciaExterna;

    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "comentarios")
    private String comentarios;

    @OneToMany(mappedBy = "idProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductoTipoProducto> productoTipoProductos = new ArrayList<>();


    public List<ProductoTipoProducto> getProductoTipoProductos() {
        return productoTipoProductos;
    }

    public void setProductoTipoProductos(List<ProductoTipoProducto> productoTipoProductos) {
        this.productoTipoProductos = productoTipoProductos;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Producto producto = (Producto) o;

        // Si el ID es nulo en ambos, no son iguales
        if (id == null || producto.id == null) {
            return false;
        }

        // La comparación clave: se basa solo en el ID
        return id.equals(producto.id);
    }

    @Override
    public int hashCode() {
        // Si el ID no es nulo, usa su hashCode.
        // Si es nulo, usa el hashCode por defecto del objeto.
        return id != null ? id.hashCode() : super.hashCode();
    }

}