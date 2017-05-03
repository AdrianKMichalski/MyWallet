package psm.mywallet.server.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Adrian Michalski
 */
@Entity
@Table(name = "entries")
public class Entry implements Serializable {

    private Long id;
    private Date createDate;
    private Date modifyDate;
    private BigDecimal value;
    private String description;
    private Set<Tag> tags;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "create_date", nullable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @PrePersist
    protected void onCreate() {
        createDate = new Date();
    }

    @Column(name = "modify_date")
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @PreUpdate
    protected void onUpdate() {
        modifyDate = new Date();
    }

    @Column(name = "value", nullable = false, precision = 8, scale = 2)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "entries_tags",
            joinColumns = {@JoinColumn(name = "entry_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false, updatable = false)})
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        Set<String> tagSet = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return "Entry{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                ", value=" + value +
                ", description='" + description + '\'' +
                ", tags=" + tagSet +
                '}';
    }

}
