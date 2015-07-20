package DAL;


import DAL.Entities.TransactionsEntity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

// a POJO representing transactions stored in the database
public class Transaction implements Serializable {

    public int              gasStationId;
    public int              carId;
    public int              pump;
    public double           amount;
    public LocalDateTime    timeStamp;
    public ServiceType      type;

    public Transaction() {

    }

    public Transaction(TransactionsEntity entity) {
        gasStationId = entity.getStationId();
        carId = entity.getCarId();
        pump = entity.getPump();
        amount = entity.getAmount();
        timeStamp = LocalDateTime.of(entity.getDateAdded().toLocalDate(), entity.getTimeAdded().toLocalTime());
        type = ServiceType.fromInt(entity.getServiceType());
    }

    public TransactionsEntity toEntity() {
        TransactionsEntity entity = new TransactionsEntity();
        entity.setStationId(gasStationId);
        entity.setCarId(carId);
        if (pump >= 0) {
            entity.setPump(pump);
        } else {
            entity.setPump(0);
        }

        entity.setAmount(amount);
        Date out = Date.valueOf(timeStamp.toLocalDate());
        entity.setDateAdded(out);
        Time time = Time.valueOf(timeStamp.toLocalTime());
        entity.setTimeAdded(time);
        entity.setServiceType(type.ordinal());
        return entity;
    }
}
