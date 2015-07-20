package DAL;

import BL.Car;
import BL.GasStation;
import BL.Pump;
import DAL.Entities.CarsEntity;
import DAL.Entities.GasStationsEntity;
import DAL.Entities.PumpsEntity;
import DAL.Entities.TransactionsEntity;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("SpellCheckingInspection")
public class PersistenceDAL implements IDAL {

    private static IDAL instance;

    private SessionFactory factory;

    private PersistenceDAL() {

        Configuration configuration = new Configuration().configure();
        ServiceRegistryBuilder builder = new ServiceRegistryBuilder().applySettings(configuration.getProperties());
        factory = configuration.buildSessionFactory(builder.buildServiceRegistry());
    }

    public IDAL getInstance() {
        if (instance == null) {
            instance = new PersistenceDAL();
        }
        return instance;
    }

    @Override
    public boolean addCar(Car car) {
        CarsEntity entity = car.toEntity();
        Session session = factory.openSession();
        session.saveOrUpdate(entity);
        session.flush();
        session.close();
        return true;
    }

    @Override
    public boolean checkGasStation(GasStation gs) {
        Session session = factory.openSession();
        org.hibernate.Transaction transaction1 = session.beginTransaction();
        GasStationsEntity entity = (GasStationsEntity) session.get(GasStationsEntity.class, gs.getId());
        //session.saveOrUpdate(entity);
        if (entity == null) {
            entity = gs.toEntity();
            session.save(entity);
        }
        transaction1.commit();
        session.close();
        return true;
    }

    @Override
    public boolean setPumps(ArrayList<Pump> pumps, GasStation gs) {
        Session session = factory.openSession();
        org.hibernate.Transaction transaction1 = session.beginTransaction();
        for (Pump pump : pumps) {
            PumpsEntity entity = pump.getEntity();
            session.saveOrUpdate(entity);
        }
        transaction1.commit();
        session.close();
        return true;
    }

    @Override
    public boolean storeTransaction(Transaction transaction) {
        TransactionsEntity entity = transaction.toEntity();
        Session session = factory.openSession();
        org.hibernate.Transaction transaction1 = session.beginTransaction();
        session.save(entity);
        transaction1.commit();
        session.close();
        return true;
    }

    @Override
    public Vector<Transaction> getTransactions(LocalDateTime first, LocalDateTime last, int option) {
        Session session = factory.openSession();
        Query query = setQuery(session, first, last, option);
        Vector<Transaction> vector = new Vector<>();
        Iterator it = query.iterate();
        while (it.hasNext()) {
            TransactionsEntity entity = (TransactionsEntity) it.next();
            Transaction transaction = new Transaction(entity);
            vector.add(transaction);
        }
        return vector;
    }

    private Query setQuery(Session session, LocalDateTime first, LocalDateTime last, int option) {
        StringBuilder select = new StringBuilder().append("from TransactionsEntity");
        Query query;
        switch (option) {
            case 1:
                select.append(" where timeAdded between :firstTime and :secondTime ")
                        .append("and dateAdded between :firstDate and :secondDate group by pump");
                query = session.createQuery(select.toString()).setTime("firstTime", Time.valueOf(first.toLocalTime()))
                       .setTime("secondTime", Time.valueOf(last.toLocalTime()))
                       .setDate("firstDate", Date.valueOf(first.toLocalDate()))
                       .setDate("secondDate", Date.valueOf(last.toLocalDate()));
                break;
            case 2:
                select.append(" where dateAdded between :firstDate and :secondDate group by pump");
                query = session.createQuery(select.toString()).setDate("firstDate", Date.valueOf(first.toLocalDate()))
                        .setDate("secondDate", Date.valueOf(last.toLocalDate()));
                break;
            case 3:
                select.append(" where dateAdded between :firstDate and :secondDate group by dateAdded");
                query = session.createQuery(select.toString()).setDate("firstDate", Date.valueOf(first.toLocalDate()))
                        .setDate("secondDate", Date.valueOf(last.toLocalDate()));
                break;
            default:
                query = session.createQuery(select.toString());
        }
        return query;
        // return select;
    }

	@Override
	public String getCarFee(int id) {
        String sumFee = "Car No." + id;
        Session session = factory.openSession();
        //SELECT DATE_ADDED, TIME_ADDED, PUMP, SERVICE_TYPE, SUM(AMOUNT) AS 'SUM' FROM transactions "
        Vector<Transaction> vector = new Vector<Transaction>();
        Query query = session.createQuery("select sum(tr.amount), count(tr.carId) from TransactionsEntity tr where tr.carId=" + id);
        List<Object[]> listResult = query.list();
        Number sum = (Number) listResult.get(0)[0];
        Number resultsCounted = (Number) listResult.get(0)[1];
        if(resultsCounted.intValue() > 0) {
            sumFee += " has paid " + sum + " in total";
        } else {
            sumFee += " does not exist.";
        }
        session.close();
        return sumFee;
	}
}
