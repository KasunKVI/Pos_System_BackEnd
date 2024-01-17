package software.kasunkavinda.pos_system_backend.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import software.kasunkavinda.pos_system_backend.entity.Customer;
import software.kasunkavinda.pos_system_backend.entity.Item;
import software.kasunkavinda.pos_system_backend.entity.Orders;
import software.kasunkavinda.pos_system_backend.entity.User;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FactoryConfiguration {

    private static FactoryConfiguration factoryConfiguration;
    private SessionFactory sessionFactory;

    private FactoryConfiguration() {
        try {


            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/pos_system");


            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(Customer.class);
            configuration.addAnnotatedClass(Item.class);
            configuration.addAnnotatedClass(Orders.class);
            configuration.addAnnotatedClass(User.class);
            configuration.setProperty("hibernate.connection.datasource", "java:comp/env/jdbc/pos_system");// Use JNDI to get the DataSource

            sessionFactory = configuration.buildSessionFactory();

        } catch (NamingException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public static FactoryConfiguration getInstance() {
        return (factoryConfiguration == null) ? factoryConfiguration = new FactoryConfiguration() : factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}
