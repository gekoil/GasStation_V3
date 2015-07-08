package DAL;


import java.io.Serializable;
import java.time.LocalDateTime;

// a POJO for storing transactions in the database
public class Transaction implements Serializable {

    public int         gasStation;
    public int         pump;
    public double      amount;
    public LocalDateTime   timeStamp;
    public ServiceType type;
    
}
