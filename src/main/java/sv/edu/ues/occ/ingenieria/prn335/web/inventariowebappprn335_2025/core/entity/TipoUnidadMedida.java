package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tipo_unidad_medida", schema = "public")
public class TipoUnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_unidad_medida", nullable = false)
    private Integer id;

    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @Size(max = 155)
    @Column(name = "unidad_base", length = 155)
    private String unidadBase;

    @Lob
    @Column(name = "comentarios")
    private String comentarios;

    @OneToMany(mappedBy = "idTipoUnidadMedida", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UnidadMedida> unidadMedidas = new ArrayList();

    public List<UnidadMedida> getUnidadMedidas() {
        return unidadMedidas;
    }

    public void setUnidadMedidas(List<UnidadMedida> unidadMedidas) {
        this.unidadMedidas = unidadMedidas;
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

    public String getUnidadBase() {
        return unidadBase;
    }

    public void setUnidadBase(String unidadBase) {
        this.unidadBase = unidadBase;
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
        TipoUnidadMedida that = (TipoUnidadMedida) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}