package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "caracteristica", schema = "public")
public class Caracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caracteristica")
    private Integer id;

    @Lob
    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_unidad_medida")
    private TipoUnidadMedida idTipoUnidadMedida;

    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;


    //@OneToMany(mappedBy = "idCaracteristica", cascade = CascadeType.ALL, fetch = FetchType.LAZY) en cascada
    @OneToMany(mappedBy = "idCaracteristica", fetch = FetchType.LAZY)
    private List<TipoProductoCaracteristica> TipoProductoCaracteristicas = new ArrayList<>();



    public List<sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica> getTipoProductoCaracteristica() {
        return TipoProductoCaracteristicas;
    }

    public void setTipoProductoCaracteristica(List<sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica> tipoProductoCaracteristica) {
        TipoProductoCaracteristicas = tipoProductoCaracteristica;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoUnidadMedida getIdTipoUnidadMedida() {
        return idTipoUnidadMedida;
    }

    public void setIdTipoUnidadMedida(TipoUnidadMedida idTipoUnidadMedida) {
        this.idTipoUnidadMedida = idTipoUnidadMedida;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Caracteristica that = (Caracteristica) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}