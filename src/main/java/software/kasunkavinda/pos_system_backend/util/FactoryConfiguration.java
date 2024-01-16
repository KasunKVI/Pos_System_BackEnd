package software.kasunkavinda.pos_system_backend.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import software.kasunkavinda.pos_system_backend.entity.Customer;
import software.kasunkavinda.pos_system_backend.entity.Item;
import software.kasunkavinda.pos_system_backend.entity.Orders;
import software.kasunkavinda.pos_system_backend.entity.User;

public class FactoryConfiguration {

    private static FactoryConfiguration factoryConfiguration;
    private SessionFactory sessionFactory;

    private FactoryConfiguration(){



        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
                configuration.addAnnotatedClass(Customer.class);
                configuration.addAnnotatedClass(Item.class);
                configuration.addAnnotatedClass(Orders.class);
                configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();


    }

    public static FactoryConfiguration getInstance(){
        return (factoryConfiguration==null)? factoryConfiguration=new FactoryConfiguration(): factoryConfiguration;

    }

    public Session getSession(){

        return  sessionFactory.openSession();
    }
}
