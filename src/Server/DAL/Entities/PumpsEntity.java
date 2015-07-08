package DAL.Entities;

import javax.persistence.*;

/**
 * Created by Jack on 05/07/2015.
 */
@Entity
@Table(name = "pumps", schema = "", catalog = "my_db")
public class PumpsEntity {
    private int id;
    private int stationId;

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "STATION_ID")
    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PumpsEntity that = (PumpsEntity) o;

        if (id != that.id) return false;
        if (stationId != that.stationId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + stationId;
        return result;
    }
}
