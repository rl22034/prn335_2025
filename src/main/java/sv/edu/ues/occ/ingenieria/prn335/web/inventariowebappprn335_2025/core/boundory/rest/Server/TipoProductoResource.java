package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.rest.Server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import jakarta.ws.rs.QueryParam;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.lang.Long;
import java.util.List;

@Path("tipo_producto")
public class TipoProductoResource {

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(@Min(0)
                              @DefaultValue("0")
                              @QueryParam("first")
                              int first,
                              @Max(100)
                              @DefaultValue("50")
                              @QueryParam("max")
                              int max) {

        if (first == 0 && max == 100) {

            try {
                Long total = tipoProductoDAO.count();
                return Response.ok(tipoProductoDAO.findRange(first, max)).header("Total-Recors", total).build();

            } catch (Exception e) {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-Exception", "cannot.access db").build();
            }
        }
        return Response.status(422).header("Missing-Parameters", "first, max are").build();
    }

    /**
     * Busca un TipoProducto por su ID
     *
     * @Path("{id}") → Define una VARIABLE en la URL entre llaves {}
     *                  Ejemplo: GET /tipo_producto/123
     *                           donde "123" es capturado como {id}
     *
     * @PathParam("id") → CAPTURA el valor de {id} desde la URL
     *                    y lo pasa al parámetro del método
     *                    URL: /tipo_producto/123 → id = 123
     *
     * @param id - El ID del tipo producto a buscar (viene de la URL)
     * @return Response con el tipo producto encontrado o error
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id){
        if(id != null){

            try{
                TipoProducto resp = tipoProductoDAO.finById(id);
                if(resp != null){

                    return Response.ok(resp).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Error-Message", "Resgistro con id " + id + " no encontrado")
                        .build();
            }catch (Exception e){

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-Exception", "cannot.access db")
                        .build();
            }
        }
        return Response.status(422).header("Missing-Parameters", "id").build();

    }

    /**
     * Elimina un TipoProducto por su ID
     *
     * @Path("{id}") → Define una VARIABLE en la URL entre llaves {}
     *                  Ejemplo: DELETE /tipo_producto/123
     *
     * @PathParam("id") → CAPTURA el valor de {id} desde la URL
     *
     * @param id - El ID del tipo producto a eliminar (viene de la URL)
     * @return Response.noContent() si se eliminó exitosamente (HTTP 204)
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id){
        if(id != null){
            try{
                TipoProducto resp = tipoProductoDAO.finById(id);
                if(resp != null){
                    tipoProductoDAO.delete(resp);
                    return Response.noContent().build();  // HTTP 204 No Content
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Error-Message", "Registro con id " + id + " no encontrado")
                        .build();
            }catch (Exception e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-Exception", "cannot.access db")
                        .build();
            }
        }
        return Response.status(422).header("Missing-Parameters", "id").build();
    }

    /**
     * Crea un nuevo TipoProducto en la base de datos
     *
     * @POST → Indica que este método maneja peticiones HTTP POST
     *         Se usa para CREAR nuevos recursos
     *
     * @Consumes(MediaType.APPLICATION_JSON) → Este método ACEPTA JSON en el body de la petición
     *                                          El JSON se convierte automáticamente a objeto TipoProducto
     *
     * @Produces(MediaType.APPLICATION_JSON) → Este método DEVUELVE JSON en la respuesta
     *                                          El objeto TipoProducto creado se convierte a JSON
     *
     * @Context UriInfo → Inyecta información sobre la URI actual
     *                     Se usa para construir el header "Location" con la URL del recurso creado
     *
     * @param entity - El TipoProducto a crear (viene en el body JSON de la petición)
     * @param uriInfo - Información de la URI actual (inyectado automáticamente)
     * @return Response.created() con HTTP 201 si se crea exitosamente
     *         Response con HTTP 422 si hay errores de validación
     *         Response con HTTP 500 si hay error en el servidor
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(TipoProducto entity, @Context UriInfo uriInfo){
        if(entity == null){
            return Response.status(422)
                          .header("Error-Message", "entity es null")
                          .build();
        }

        try{
            // Validar padre si viene especificado
            if(entity.getIdTipoProductoPadre() != null &&
               entity.getIdTipoProductoPadre().getId() != null){

                TipoProducto padre = tipoProductoDAO.finById(
                    entity.getIdTipoProductoPadre().getId()
                );

                if(padre == null){
                    return Response.status(422)
                                  .header("Error-Message", "Padre no encontrado")
                                  .build();
                }

                entity.setIdTipoProductoPadre(padre);
            }

            // Crear el registro
            tipoProductoDAO.crear(entity);
            // Después de crear, entity ya tiene el ID generado
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder()
                                           .path(String.valueOf(entity.getId()));
            return Response.created(uriBuilder.build(
                    ))
                          .entity(entity)
                          .build();

        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .header("Server-Exception", "cannot.create")
                          .build();
        }
    }
}


