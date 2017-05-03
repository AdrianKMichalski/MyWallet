package psm.mywallet.api;

import java.util.Set;

/**
 * @author Adrian Michalski
 */
public class TagDTO {

    private Long id;
    private String name;
    private String description;
    private Set<Long> entries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getEntries() {
        return entries;
    }

    public void setEntries(Set<Long> entries) {
        this.entries = entries;
    }

}
