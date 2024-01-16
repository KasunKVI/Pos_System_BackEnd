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
import software.kasunkavinda.pos_system_backend.dto.OrderDateDto;
import software.kasunkavinda.pos_system_backend.entity.Customer;
import software.kasunkavinda.pos_system_backend.entity.Orders;
import software.kasunkavinda.pos_system_backend.util.FactoryConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "orderApi", urlPatterns = "/orderApi")
public class OrderApi extends HttpServlet {


    static Session session;

    static Transaction transaction;

    private static void getConnection() {

        session = FactoryConfiguration.getInstance().getSession();
        transaction = session.beginTransaction();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            getConnection();

            String id = req.getParameter("orderId");


            if(id.equals("AllOrders")){
                OrderDateDto orderDateDto;

                List<Orders> orders = session.createNativeQuery("SELECT * FROM Orders", Orders.class).list();
                List<OrderDateDto> orderDateDtos = new ArrayList<>();

                for(Orders order: orders){
                    orderDateDto = new OrderDateDto();
                    orderDateDto.setId(order.getId());
                    orderDateDto.setDate(order.getDate());
                    orderDateDto.setBalance(order.getBalance());
                    orderDateDto.setCustomer_id(order.getCustomer().getNic());
                    orderDateDto.setName(order.getCustomer().getName());
                    orderDateDtos.add(orderDateDto);
                }

                transaction.commit();

                Gson gson = new Gson(); // Import Gson library for JSON manipulation
                String jsonCustomer = gson.toJson(orderDateDtos);

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(jsonCustomer);
                out.flush();

            }else {

                Orders orders = session.get(Orders.class, id);
                Customer customer = session.get(Customer.class, orders.getCustomer().getNic());
                orders.setCustomer(null);
                orders.setItems(null);

                OrderDateDto order_detail = new OrderDateDto(orders.getId(), orders.getDate(), orders.getBalance(), customer.getNic(), customer.getName());
                Gson gson = new Gson(); // Import Gson library for JSON manipulation
                String jsonCustomer = gson.toJson(order_detail);

                System.out.println(order_detail);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(jsonCustomer);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
            } else {
                Jsonb jsonb = JsonbBuilder.create();
                var updatedOrder = jsonb.fromJson(req.getReader(), OrderDateDto.class);

                Customer customer =  session.get(Customer.class, updatedOrder.getCustomer_id());
                customer.setName(updatedOrder.getName());

                session.update(customer);

                Orders orders = session.get(Orders.class, updatedOrder.getId());
                orders.setDate(updatedOrder.getDate());
                orders.setBalance(updatedOrder.getBalance());

                session.update(orders);


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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            getConnection();

            String id = req.getParameter("OrderId");


            Orders orders = session.get(Orders.class, id);

            if (orders != null) {
                session.remove(orders);
                transaction.commit();
                session.close();
            }else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            session.close();
        }
    }
}


