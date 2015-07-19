package DAL.Entities;

import javax.persistence.*;

/**
 * Created by Jack on 05/07/2015.
 */
@Entity
@Table(name = "gas_stations", schema = "", catalog = "my_db")
public class GasStationsEntity {
    private int id;
    private double gasRevenue;
    private double cleanRevenue;
    private int carsFueled;
    private int carsCleaned;

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "GAS_REVENUE")
    public double getGasRevenue() {
        return gasRevenue;
    }

    public void setGasRevenue(double gasRevenue) {
        this.gasRevenue = gasRevenue;
    }

    @Basic
    @Column(name = "CLEAN_REVENUE")
    public double getCleanRevenue() {
        return cleanRevenue;
    }

    public void setCleanRevenue(double cleanRevenue) {
        this.cleanRevenue = cleanRevenue;
    }

    @Basic
    @Column(name = "CARS_FUELED")
    public int getCarsFueled() {
        return carsFueled;
    }

    public void setCarsFueled(int carsWashed) {
        this.carsFueled = carsWashed;
    }

    @Basic
    @Column(name = "CARS_CLEANED")
    public int getCarsCleaned() {
        return carsCleaned;
    }

    public void setCarsCleaned(int carsCleaned) {
        this.carsCleaned = carsCleaned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GasStationsEntity that = (GasStationsEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.gasRevenue, gasRevenue) != 0) return false;
        if (Double.compare(that.cleanRevenue, cleanRevenue) != 0) return false;
        if (carsFueled != that.carsFueled) return false;
        if (carsCleaned != that.carsCleaned) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(gasRevenue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(cleanRevenue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + carsFueled;
        result = 31 * result + carsCleaned;
        return result;
    }
}
