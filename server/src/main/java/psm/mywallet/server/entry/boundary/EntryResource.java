package psm.mywallet.server.entry.boundary;

import psm.mywallet.api.EntryDTO;
import psm.mywallet.server.entry.control.EntryRepository;
import psm.mywallet.server.jpa.entity.Entry;
import psm.mywallet.server.jpa.entity.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<EntryDTO> getAllEntries() {
        return entryRepository.getAll().stream()
                .map(this::mapToEntryDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("tag/{tagName}")
    public List<EntryDTO> getEntriesByTag(@PathParam("tagName") String tagName) {
        return entryRepository.getByTag(tagName).stream()
                .map(this::mapToEntryDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("tag/{tagName}/sum")
    public Number getSumOfAllEntriesForTag(@PathParam("tagName") String tagName) {
        return entryRepository.getSumOfAllEntriesForTag(tagName);
    }

    private EntryDTO mapToEntryDTO(Entry entry) {
        Set<String> tagNames = entry.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        EntryDTO entryDTO = new EntryDTO();
        entryDTO.setId(entry.getId());
        entryDTO.setCreateDate(entry.getCreateDate());
        entryDTO.setDescription(entry.getDescription());
        entryDTO.setValue(entry.getValue());
        entryDTO.setTags(tagNames);

        return entryDTO;
    }

    @GET
    @Path("balance")
    public Number getAccountBalance() {
        return entryRepository.getAccountBalance();
    }

    @POST
    public Response createEntry(EntryDTO entryDTO) {
        Set<Tag> tags = entryDTO.getTags().stream()
                .map(Tag::new)
                .collect(Collectors.toSet());

        Entry entry = new Entry();
        entry.setDescription(entryDTO.getDescription());
        entry.setValue(entryDTO.getValue());
        entry.setTags(tags);

        entryRepository.save(entry);
        return Response.ok().build();
    }

    @GET
    @Path("delete/{entryId}")
    public Response deleteEntry(@PathParam("entryId") String entryId) {
        long id = Long.parseLong(entryId);

        entryRepository.remove(id);
        return Response.ok().build();
    }

}
