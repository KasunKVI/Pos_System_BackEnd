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
import software.kasunkavinda.pos_system_backend.dto.ItemDto;
import software.kasunkavinda.pos_system_backend.entity.Item;
import software.kasunkavinda.pos_system_backend.entity.Orders;
import software.kasunkavinda.pos_system_backend.util.FactoryConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "itemApi", urlPatterns = "/itemApi")
public class ItemApi extends HttpServlet {

    static Session session ;

    static Transaction transaction;

    private static final Logger logger = LoggerFactory.getLogger(ItemApi.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            getConnection();

            if (req.getContentType() == null ||
                    !req.getContentType().toLowerCase().startsWith("application/json")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Jsonb jsonb = JsonbBuilder.create();
                var item = jsonb.fromJson(req.getReader(), ItemDto.class);

                Item item1 = new Item(
                        item.getId(),
                        item.getQty(),
                        item.getName(),
                        item.getExp(),
                        item.getPrice(),
                        null
                );

                try {
                    logger.info("Add new item to database");
                    session.persist(item1);
                    transaction.commit(); // Commit the transaction after successful persistence

                    // Set success response
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Item successfully added");

                } catch (Exception e) {
                    transaction.rollback(); // Rollback the transaction in case of exception
                    logger.error("Error message",e);
                }
        }} catch (Exception e) {
            logger.error("Error message",e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {

            session.close();
        }


    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
                getConnection();

                String id = req.getParameter("itemId");

                if(id.equals("AllItems")){


                    List<Item> items = session.createNativeQuery("SELECT * FROM Item", Item.class).list();
                    transaction.commit();

                    logger.info("Get all items from database");

                    for(Item item: items){
                       item.setOrders(null);
                    }


                    Gson gson = new Gson(); // Import Gson library for JSON manipulation
                    String jsonCustomer = gson.toJson(items);

                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter out = resp.getWriter();
                    out.print(jsonCustomer);
                    out.flush();


                }else {
                    Item item = session.get(Item.class, id);
                    item.setOrders(null);

                    logger.info("Get selected item from database");

                    Gson gson = new Gson(); // Import Gson library for JSON manipulation
                    String jsonItem = gson.toJson(item);

                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter out = resp.getWriter();
                    out.print(jsonItem);
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
                getConnection();

                if (req.getContentType() == null ||
                        !req.getContentType().toLowerCase().startsWith("application/json")) {

                    String id = req.getParameter("itemId");
                    int qty = Integer.parseInt(req.getParameter("qty"));

                    Item item = session.get(Item.class, id);
                    int newQty = item.getQty()-qty;

                    logger.info("Updated item qty");

                    if(newQty <= 0){
                        session.remove(item);
                    }else {
                        item.setQty(newQty);
                        session.update(item);
                    }


                } else {
                    Jsonb jsonb = JsonbBuilder.create();
                    var item = jsonb.fromJson(req.getReader(), ItemDto.class);

                    Item item1 = new Item(
                            item.getId(),
                            item.getQty(),
                            item.getName(),
                            item.getExp(),
                            item.getPrice(),
                            null
                    );

                    logger.info("Updated item details");

                    session.update(item1);

                    // Set success response
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Item successfully updated");

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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            getConnection();

            String id = req.getParameter("itemId");

            System.out.println(id);

            Item item = session.get(Item.class, id);
            if (item != null) {
                session.remove(item);
                logger.info("Deleted selected item from database");

                // Set success response
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item successfully deleted");

                transaction.commit();
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Item not found");
            }
        }catch (Exception e) {

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
}
