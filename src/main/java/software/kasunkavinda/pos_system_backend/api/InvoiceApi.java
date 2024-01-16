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
import org.hibernate.query.Query;
import software.kasunkavinda.pos_system_backend.dto.OrderDto;
import software.kasunkavinda.pos_system_backend.dto.OrderItem;
import software.kasunkavinda.pos_system_backend.entity.Customer;
import software.kasunkavinda.pos_system_backend.entity.Item;
import software.kasunkavinda.pos_system_backend.entity.Orders;
import software.kasunkavinda.pos_system_backend.util.FactoryConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "invoiceApi", urlPatterns = "/invoiceApi")
public class InvoiceApi extends HttpServlet {

    static Session session;

    static Transaction transaction;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
                getConnection();

                if (req.getContentType() == null ||
                        !req.getContentType().toLowerCase().startsWith("application/json")) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    Jsonb jsonb = JsonbBuilder.create();
                    var order = jsonb.fromJson(req.getReader(), OrderDto.class);

                    Customer customer = session.get(Customer.class, order.getCustomer_id());

                    List<OrderItem> itemIds = order.getItems();
                    List<Item> items = new ArrayList<>();
                    for (OrderItem orderItem: itemIds){
                        Item item = session.get(Item.class, orderItem.getItemId());
                        items.add(item);
                    }
                    Orders order1 = new Orders(
                           order.getId(),
                            order.getDate(),
                            order.getBalance(),
                            customer,
                            items
                    );

                    session.persist(order1);
                }
                try {
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    e.printStackTrace();
                }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            session.close();
        }
    }


    private static void getConnection() {

        session = FactoryConfiguration.getInstance().getSession();
        transaction = session.beginTransaction();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try{

            getConnection();

            Query<Orders> query = session.createQuery("SELECT MAX(id) FROM Orders", Orders.class);
            String lastOrder = String.valueOf(query.uniqueResultOptional().orElse(null));

            String lastOrderIdString = (lastOrder != null) ? lastOrder : "0001"; // Default if null

            Gson gson = new Gson(); // Import Gson library for JSON manipulation
            String lastId = gson.toJson(lastOrderIdString);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(lastId);
            out.flush();


        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            session.close();
        }
    }
}