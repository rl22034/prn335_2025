package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "unidad_medida", schema = "public")
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad_medida", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_unidad_medida")
    private TipoUnidadMedida idTipoUnidadMedida;

    @Column(name = "equivalencia", precision = 8, scale = 2)
    private BigDecimal equivalencia;

    @Lob
    @Column(name = "expresion_regular")
    private String expresionRegular;

    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "comentarios")
    private String comentarios;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoUnidadMedida getIdTipoUnidadMedida() {
        return idTipoUnidadMedida;
    }

    public void setIdTipoUnidadMedida(TipoUnidadMedida idTipoUnidadMedida) {
        this.idTipoUnidadMedida = idTipoUnidadMedida;
    }

    public BigDecimal getEquivalencia() {
        return equivalencia;
    }

    public void setEquivalencia(BigDecimal equivalencia) {
        this.equivalencia = equivalencia;
    }

    public String getExpresionRegular() {
        return expresionRegular;
    }

    public void setExpresionRegular(String expresionRegular) {
        this.expresionRegular = expresionRegular;
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
        UnidadMedida that = (UnidadMedida) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}