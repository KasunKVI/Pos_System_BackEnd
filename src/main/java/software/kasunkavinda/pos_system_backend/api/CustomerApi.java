package software.kasunkavinda.pos_system_backend.api;

import com.google.gson.Gson;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.kasunkavinda.pos_system_backend.dto.CustomerDto;
import software.kasunkavinda.pos_system_backend.entity.Customer;
import software.kasunkavinda.pos_system_backend.util.FactoryConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet(name = "customerApi", urlPatterns = "/customerApi")
public class CustomerApi extends HttpServlet {

    static Session session;

    static Transaction transaction;

    private static final Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
                getConnection();

                if (req.getContentType() == null ||
                        !req.getContentType().toLowerCase().startsWith("application/json")) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    logger.warn("Error on request type");
                } else {
                    Jsonb jsonb = JsonbBuilder.create();
                    var customer = jsonb.fromJson(req.getReader(), CustomerDto.class);
                    Customer customer1 = new Customer(
                            customer.getId(),
                            customer.getName(),
                            customer.getMobile_no(),
                            customer.getDob(),
                            customer.getEmail(),
                            customer.getGender(),
                            customer.getAddress(),
                            null
                    );

                    logger.info("Add new customer to database");
                    session.persist(customer1);

                    // Set success response
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Customer successfully added");
                }
                try {
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    logger.error("Error message",e);
                }
        } catch (Exception e) {
          logger.error("Error message",e);
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            session.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
                getConnection();

                if (req.getContentType() == null ||
                        !req.getContentType().toLowerCase().startsWith("application/json")) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    logger.warn("Error on request type");
                } else {
                    Jsonb jsonb = JsonbBuilder.create();
                    var customer = jsonb.fromJson(req.getReader(), CustomerDto.class);

                    Customer customer1 = new Customer(
                            customer.getId(),
                            customer.getName(),
                            customer.getMobile_no(),
                            customer.getDob(),
                            customer.getEmail(),
                            customer.getGender(),
                            customer.getAddress(),
                            null
                    );

                    logger.info("Updated customer from database");
                    session.update(customer1);

                    // Set success response
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Customer successfully updated");
                }
                try {
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    logger.error("Error message",e);
                }
        } catch (Exception e) {
            logger.error("Error message",e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            session.close();
        }
    }

    private static void getConnection(){

        session = FactoryConfiguration.getInstance().getSession();
        transaction = session.beginTransaction();
        logger.info("Get new session");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            try {

                    getConnection();

                    String id = req.getParameter("customerId");


                if(id.equals("AllCustomers")){


                    List<Customer> customers = session.createNativeQuery("SELECT * FROM Customer", Customer.class).list();
                    transaction.commit();

                    logger.info("Get all customers from database");

                        for(Customer customer: customers){
                            customer.setOrders(null);
                        }


                        Gson gson = new Gson(); // Import Gson library for JSON manipulation
                        String jsonCustomer = gson.toJson(customers);

                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        PrintWriter out = resp.getWriter();
                        out.print(jsonCustomer);
                        out.flush();

                    }else {

                        Customer customer = session.get(Customer.class, id);
                        customer.setOrders(null);

                        logger.info("Get selected customer from database");

                        Gson gson = new Gson(); // Import Gson library for JSON manipulation
                        String jsonCustomer = gson.toJson(customer);

                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        PrintWriter out = resp.getWriter();
                        out.print(jsonCustomer);
                        out.flush();
                    }

            } catch (Exception e) {
                logger.error("Error message",e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            } finally {
                session.close();
            }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    getConnection();

                    String id = req.getParameter("customerId");
                    Customer customer = session.get(Customer.class, id);


                    if (customer != null) {
                        session.remove(customer);
                        transaction.commit();
                        logger.info("Deleted selected customer from database");

                        // Set success response
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write("Customer successfully deleted");

                    }else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                        logger.warn("Customer not in database");
                    }
                } catch (Exception e) {
                    logger.error("Error message",e);
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
                } finally {
                    session.close();
                }

    }
}
