package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

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


    @OneToMany(mappedBy = "idTipoProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductoTipoProducto> ProductoTipoProductos = new ArrayList<>();

    @OneToMany(mappedBy = "idTipoProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TipoProductoCaracteristica> tipoProductoCaracteristicas = new ArrayList<>();

    public List<TipoProductoCaracteristica> getTipoProductoCaracteristicas() {
        return tipoProductoCaracteristicas;
    }

    public void setTipoProductoCaracteristicas(List<TipoProductoCaracteristica> tipoProductoCaracteristicas) {
        this.tipoProductoCaracteristicas = tipoProductoCaracteristicas;
    }

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

    public List<TipoProducto> getTipoProductoList() {
        return tipoProductoList;
    }

    public void setTipoProductoList(List<TipoProducto> tipoProductoList) {
        this.tipoProductoList = tipoProductoList;
    }

}