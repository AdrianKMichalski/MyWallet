package psm.mywallet.server.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Adrian Michalski
 */
@Entity
@Table(name = "tags")
public class Tag implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Set<Entry> entries;

    public Tag() {

    }

    public Tag(String tagName) {
        setName(tagName);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    public Set<Entry> getEntries() {
        return entries;
    }

    public void setEntries(Set<Entry> entries) {
        this.entries = entries;
    }

}
