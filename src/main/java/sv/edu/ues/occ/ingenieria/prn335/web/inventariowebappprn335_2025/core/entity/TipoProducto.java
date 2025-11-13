package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipo_producto", schema = "public")
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_producto", nullable = false)
    private Long id;

    /*
    * @JsonbTransient
    *Ignora este campo al convertir a JSON
    * */
   // @JsonbTransient
    @OneToMany(mappedBy = "idTipoProductoPadre", fetch = FetchType.LAZY)
    private List<TipoProducto> tipoProductoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_producto_padre")
    private TipoProducto idTipoProductoPadre;

    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "comentarios")
    private String comentarios;


  //  @JsonbTransient
    @OneToMany(mappedBy = "idTipoProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductoTipoProducto> ProductoTipoProductos = new ArrayList<>();

   // @JsonbTransient
    @OneToMany(mappedBy = "idTipoProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TipoProductoCaracteristica> tipoProductoCaracteristicas = new ArrayList<>();

    @JsonbTransient
    public List<TipoProductoCaracteristica> getTipoProductoCaracteristicas() {
        return tipoProductoCaracteristicas;
    }

    public void setTipoProductoCaracteristicas(List<TipoProductoCaracteristica> tipoProductoCaracteristicas) {
        this.tipoProductoCaracteristicas = tipoProductoCaracteristicas;
    }

    @JsonbTransient
    public List<ProductoTipoProducto> getProductoTipoProductos() {
        return ProductoTipoProductos;
    }

    public void setProductoTipoProductos(List<ProductoTipoProducto> productoTipoProductos) {
        ProductoTipoProductos = productoTipoProductos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoProducto getIdTipoProductoPadre() {
        return idTipoProductoPadre;
    }

    public void setIdTipoProductoPadre(TipoProducto idTipoProductoPadre) {
        this.idTipoProductoPadre = idTipoProductoPadre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    @JsonbTransient
    public List<TipoProducto> getTipoProductoList() {
        return tipoProductoList;
    }

    public void setTipoProductoList(List<TipoProducto> tipoProductoList) {
        this.tipoProductoList = tipoProductoList;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TipoProducto tipoProducto = (TipoProducto) o;

        // Si el ID es nulo en ambos, no son iguales
        if (id == null || tipoProducto.id == null) {
            return false;
        }

        // La comparación clave: se basa solo en el ID
        return id.equals(tipoProducto.id);
    }

    @Override
    public int hashCode() {
        // Si el ID no es nulo, usa su hashCode.
        // Si es nulo, usa el hashCode por defecto del objeto.
        return id != null ? id.hashCode() : super.hashCode();
    }
}