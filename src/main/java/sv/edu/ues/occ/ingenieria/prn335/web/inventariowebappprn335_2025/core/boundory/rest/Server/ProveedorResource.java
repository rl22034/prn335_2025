package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.rest.Server;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@RequestScoped
@Path("proveedor")
public class ProveedorResource {

    @Inject
    ProveedorDAO proveedorDAO;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(@Min(0)
                              @DefaultValue("0")
                              @QueryParam("first")
                              int first,
                              @Max(50)
                              @DefaultValue("10")
                              @QueryParam("max")
                              int max) {
        if(first >= 0 && max > 0){
            try{
                Long total = proveedorDAO.count();
                return Response.ok(proveedorDAO.findRange(first, max)).header("Total-Records", total).build();
            }catch (Exception e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-Exception", "cannot.access db").build();
            }
        }
        return Response.status(422).header("Missing-Parameters",  "first >= 0 and max > 0 required").build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Integer id) {
        if(id != null){
            try{
                Proveedor proveedor = proveedorDAO.finById(id);
                if(proveedor != null) {
                    return Response.ok(proveedor).build();
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

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") Integer id) {
        if(id != null){
            try{
                Proveedor proveedor = proveedorDAO.finById(id);
                if(proveedor != null) {
                    proveedorDAO.delete(proveedor);
                    return Response.noContent().build();
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


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Proveedor entity, @Context
    UriInfo uriInfo){
        // 1. Validar que entity no sea null
        if(entity == null){
            return Response.status(422)
                    .header("Error-Message", "entity es null").build();
        }

        try{
            // 2. Crear el proveedor en la base de datos
            proveedorDAO.crear(entity);

            // 3. Construir la URI del recurso creado
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.getId()));

            // 4. Retornar HTTP 201 Created con el proveedor creado
            return Response.created(uriBuilder.build()).entity(entity).build();

        }catch (Exception e){
            return
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Server-Exception", "cannot.create").build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Integer id, Proveedor entity){
        // 1. Validar que id y entity no sean null
        if(id == null || entity == null){
            return Response.status(422)
                    .header("Error-Message", "id o entity es null").build();
        }

        try{
            // 2. Verificar si el proveedor existe
            Proveedor existingProveedor = proveedorDAO.finById(id);
            if(existingProveedor == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Error-Message", "Resgistro con id " + id + " no encontrado")
                        .build();
            }

            // 3. Actualizar el proveedor
            entity.setId(id); // Asegurar que el ID de la entidad sea el mismo que el del path
            Proveedor updatedProveedor = proveedorDAO.update(entity);

            // 4. Retornar HTTP 200 OK con el proveedor actualizado
            return Response.ok(updatedProveedor).build();

        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-Exception", "cannot.update").build();
        }
    }
}
