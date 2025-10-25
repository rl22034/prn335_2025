package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;


import java.util.List;

public interface InventarioDAOInterface<T> {

    public void crear (T registro);

    public T finById(Object id) ;

    public T update(T registro) ;

    public List<T> findRange(int first, int max);

    public void delete(T entidad);

    public Long count();
}
