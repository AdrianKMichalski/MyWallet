package psm.mywallet.server.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Adrian Michalski
 */
@Entity
@Table
public class Entry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private String name;

    public Entry() {
    }

    public Entry(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
