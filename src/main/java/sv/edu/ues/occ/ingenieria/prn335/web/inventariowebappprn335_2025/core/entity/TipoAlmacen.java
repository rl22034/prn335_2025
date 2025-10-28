package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tipo_almacen", schema = "public")
public class TipoAlmacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_almacen", nullable = false)
    private Integer id;

    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "obsevaciones")
    private String obsevaciones;

    /*
    * mappedBy = "idTipoAlmacen"
    * Indica que la propiedad inversa de la relación está en la entidad hija (Almacen) y se llama idTipoAlmacen
    * FetchType.LAZY → perezoso, se carga solo cuando lo necesitas.
    * FetchType.EAGER → ansioso, se carga automáticamente junto con la entidad
    *
    * cascade = CascadeType.ALL
    * Indica que todas las operaciones que hagas en el padre (TipoAlmacen) se aplican automáticamente a los hijos (Almacen).
    *
    * fetch = FetchType.LAZY
    * LAZY → los Almacen relacionados no se cargan automáticamente cuando cargas un TipoAlmacen
    * Solo se cargan cuando accedes explícitamente a tipoAlmacen.getAlmacenes().
    *
    */
    @OneToMany(mappedBy = "idTipoAlmacen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Almacen> almacenes = new ArrayList<>();

    public List<Almacen> getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(List<Almacen> almacenes) {
        this.almacenes = almacenes;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObsevaciones() {
        return obsevaciones;
    }

    public void setObsevaciones(String obsevaciones) {
        this.obsevaciones = obsevaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoAlmacen that = (TipoAlmacen) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}