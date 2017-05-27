package psm.mywallet.server.tag.boundary;

import psm.mywallet.server.tag.control.TagRepository;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Adrian Michalski
 */
@Path(TagResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

    public static final String PATH = "tags";

    @Inject
    TagRepository tagRepository;


    @GET
    public List<String> getAllTags() {
        return tagRepository.getAll();
    }

}
