package psm.mywallet.server.tag.control;

import psm.mywallet.server.jpa.entity.Tag;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Adrian Michalski
 */
@Stateless
@LocalBean
public class TagRepository {

    @PersistenceContext(unitName = "mywallet-pu")
    private EntityManager em;

    public List<String> getAll() {
        List<Tag> tags = em.createQuery("SELECT t FROM Tag t ORDER BY t.name", Tag.class)
                .getResultList();

        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
