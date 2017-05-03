package psm.mywallet.api;

import java.util.Set;

/**
 * @author Adrian Michalski
 */
public class TagDTO {

    private Long id;
    private String name;
    private String description;
    private Set<String> entries;

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

    public Set<String> getEntries() {
        return entries;
    }

    public void setEntries(Set<String> entries) {
        this.entries = entries;
    }

}
