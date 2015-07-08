package DAL.Entities;

import javax.persistence.*;

@Entity
@Table(name = "cars", schema = "", catalog = "my_db")
public class CarsEntity {
    private int id;
    private Boolean wantCleaning;
    private int numOfLiters;
    private int pumpNum;
    private int gs;
    private Boolean fueledUp;
    private Boolean cleanedUp;

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "WANT_CLEANING")
    public Boolean getWantCleaning() {
        return wantCleaning;
    }

    public void setWantCleaning(Boolean wantCleaning) {
        this.wantCleaning = wantCleaning;
    }

    @Basic
    @Column(name = "NUM_OF_LITERS")
    public int getNumOfLiters() {
        return numOfLiters;
    }

    public void setNumOfLiters(int numOfLiters) {
        this.numOfLiters = numOfLiters;
    }

    @Basic
    @Column(name = "PUMP_NUM")
    public int getPumpNum() {
        return pumpNum;
    }

    public void setPumpNum(int pumpNum) {
        this.pumpNum = pumpNum;
    }

    @Basic
    @Column(name = "GS")
    public int getGs() {
        return gs;
    }

    public void setGs(int gs) {
        this.gs = gs;
    }

    @Basic
    @Column(name = "FUELED_UP")
    public Boolean getFueledUp() {
        return fueledUp;
    }

    public void setFueledUp(Boolean fueledUp) {
        this.fueledUp = fueledUp;
    }

    @Basic
    @Column(name = "CLEANED_UP")
    public Boolean getCleanedUp() {
        return cleanedUp;
    }

    public void setCleanedUp(Boolean cleanedUp) {
        this.cleanedUp = cleanedUp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarsEntity that = (CarsEntity) o;

        if (id != that.id) return false;
        if (numOfLiters != that.numOfLiters) return false;
        if (pumpNum != that.pumpNum) return false;
        if (gs != that.gs) return false;
        if (wantCleaning != null ? !wantCleaning.equals(that.wantCleaning) : that.wantCleaning != null) return false;
        if (fueledUp != null ? !fueledUp.equals(that.fueledUp) : that.fueledUp != null) return false;
        if (cleanedUp != null ? !cleanedUp.equals(that.cleanedUp) : that.cleanedUp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (wantCleaning != null ? wantCleaning.hashCode() : 0);
        result = 31 * result + numOfLiters;
        result = 31 * result + pumpNum;
        result = 31 * result + gs;
        result = 31 * result + (fueledUp != null ? fueledUp.hashCode() : 0);
        result = 31 * result + (cleanedUp != null ? cleanedUp.hashCode() : 0);
        return result;
    }
}
