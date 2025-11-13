package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import java.io.Serializable;

@Stateless
@LocalBean
public class TipoUnidadMedidaDAO extends InventarioDefaultDataAccess<TipoUnidadMedida> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public TipoUnidadMedidaDAO(){super(TipoUnidadMedida.class);}

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
