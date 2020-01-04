package micro.examples.data.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Demo entity keeping a note to do
 */
@Entity
@Access(AccessType.FIELD)
public class ToDo implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "seq")
    long id;

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    String addressee;
    String description;
    Date deadLine;

    public long getId() {
        return id;
    }

    public String getAddressee() {
        return addressee;
    }

    public String getDescription() {
        return description;
    }

    public Date getDeadLine() {
        return deadLine;
    }


}
