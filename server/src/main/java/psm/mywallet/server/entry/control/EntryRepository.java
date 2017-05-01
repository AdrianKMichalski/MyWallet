package psm.mywallet.server.entry.control;

import psm.mywallet.server.jpa.entity.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author Adrian Michalski
 */
@Stateless
@LocalBean
public class EntryRepository {

    @PersistenceContext(unitName = "mywallet-pu")
    private EntityManager em;

    public List<Entry> getAll() {
        Query query = em.createQuery("SELECT e from Entry as e");
        return query.getResultList();
    }

    public void save(Entry entry) {
        em.persist(entry);
    }

}
