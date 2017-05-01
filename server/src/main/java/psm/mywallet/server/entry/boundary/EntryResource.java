package psm.mywallet.server.entry.boundary;

import psm.mywallet.server.entry.control.EntryRepository;
import psm.mywallet.server.jpa.entity.Entry;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Adrian Michalski
 */
@Path(EntryResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntryResource {

    public static final String PATH = "entries";

    @Inject
    EntryRepository entryRepository;

    @GET
    public List<Entry> get() {
        return entryRepository.getAll();
    }

    @POST
    @Path("/{name}")
    public Response post(@PathParam("name") String name) {
        entryRepository.save(new Entry(name));
        return Response.ok().build();
    }

}
