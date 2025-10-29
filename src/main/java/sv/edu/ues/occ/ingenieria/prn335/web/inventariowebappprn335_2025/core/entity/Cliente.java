package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "cliente", schema = "public")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente", nullable = false)
    private UUID id;

    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;

    @Size(max = 9)
    @Column(name = "dui", length = 9)
    private String dui;

    @Size(max = 14)
    @Column(name = "nit", length = 14)
    private String nit;

    @Column(name = "activo")
    private Boolean activo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        // Si el ID es nulo en ambos, no son iguales
        if (id == null || cliente.id == null) {
            return false;
        }

        // La comparación clave: se basa solo en el ID
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        // Si el ID no es nulo, usa su hashCode.
        // Si es nulo, usa el hashCode por defecto del objeto.
        return id != null ? id.hashCode() : super.hashCode();
    }
}