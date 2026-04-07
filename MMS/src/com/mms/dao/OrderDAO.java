package com.mms.dao;

import com.mms.model.Order;
import com.mms.util.DBConnection;
import java.sql.*;
import java.util.*;

public class OrderDAO {
    private Connection conn;
    public OrderDAO() throws SQLException { this.conn=DBConnection.getInstance().getConnection(); }

    /** Create an order with multiple items. items: int[][]{productId, quantity} */
    public boolean createOrder(int customerId, int[][] items, int promoId,
                                String notes, int processedBy,
                                ProductDAO productDAO, PromotionDAO promoDAO, CustomerDAO customerDAO) {
        try {
            conn.setAutoCommit(false);

            // Calculate total
            double total=0;
            for (int[] item : items) {
                com.mms.model.Product p=productDAO.getProductById(item[0]);
                if(p!=null) total+=p.getUnitPrice()*item[1];
            }

            // Apply discount
            double discount=0;
            if(promoId>0) discount=promoDAO.calculateDiscount(promoId,total);
            double finalAmt=total-discount;

            // Generate order code
            String orderCode="ORD-"+System.currentTimeMillis()%100000;

            // Insert order
            String sql="INSERT INTO orders (order_code,customer_id,total_amount,discount_amount,final_amount,promo_id,status,notes,processed_by) VALUES (?,?,?,?,?,'CONFIRMED',?,?)";
            int orderId;
            try (PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,orderCode);   ps.setInt(2,customerId);
                ps.setDouble(3,total);       ps.setDouble(4,discount);
                ps.setDouble(5,finalAmt);
                if(promoId>0) ps.setInt(6,promoId); else ps.setNull(6,Types.INTEGER);
                ps.setString(7,notes);       ps.setInt(8,processedBy);
                ps.executeUpdate();
                ResultSet rs=ps.getGeneratedKeys(); if(!rs.next()){conn.rollback();conn.setAutoCommit(true);return false;}
                orderId=rs.getInt(1);
            }

            // Insert order items and deduct stock
            for(int[] item : items) {
                com.mms.model.Product p=productDAO.getProductById(item[0]);
                if(p==null) continue;
                double lineTotal=p.getUnitPrice()*item[1];
                try (PreparedStatement ps=conn.prepareStatement(
                        "INSERT INTO order_items (order_id,product_id,quantity,unit_price,total_price) VALUES (?,?,?,?,?)")) {
                    ps.setInt(1,orderId); ps.setInt(2,item[0]); ps.setInt(3,item[1]);
                    ps.setDouble(4,p.getUnitPrice()); ps.setDouble(5,lineTotal); ps.executeUpdate();
                }
                // Deduct stock
                try (PreparedStatement ps=conn.prepareStatement(
                        "UPDATE products SET stock_quantity=GREATEST(stock_quantity-?,0) WHERE product_id=?")) {
                    ps.setInt(1,item[1]); ps.setInt(2,item[0]); ps.executeUpdate();
                }
            }

            // Update customer total purchases and loyalty points
            customerDAO.updateTotalPurchases(customerId,finalAmt);

            // Increment promo usage
            if(promoId>0) promoDAO.incrementUsage(promoId);

            conn.commit(); conn.setAutoCommit(true);
            System.out.println("[INFO] Order created: "+orderCode+" | Total: Rs."+String.format("%.2f",total)+" | Discount: Rs."+String.format("%.2f",discount)+" | Final: Rs."+String.format("%.2f",finalAmt));
            return true;
        } catch (SQLException e) {
            try{conn.rollback();conn.setAutoCommit(true);}catch(SQLException ex){}
            System.out.println("[DB ERROR] createOrder: "+e.getMessage()); return false;
        }
    }

    public boolean updateOrderStatus(int orderId, String status) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE orders SET status=? WHERE order_id=?")) {
            ps.setString(1,status); ps.setInt(2,orderId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public List<Order> getAllOrders() {
        List<Order> list=new ArrayList<>();
        String sql="SELECT o.*,CONCAT(c.first_name,' ',c.last_name) AS customer_name,p.promo_code " +
                   "FROM orders o JOIN customers c ON o.customer_id=c.customer_id " +
                   "LEFT JOIN promotions p ON o.promo_id=p.promo_id ORDER BY o.order_date DESC";
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllOrders: "+e.getMessage()); }
        return list;
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> list=new ArrayList<>();
        String sql="SELECT o.*,CONCAT(c.first_name,' ',c.last_name) AS customer_name,p.promo_code " +
                   "FROM orders o JOIN customers c ON o.customer_id=c.customer_id " +
                   "LEFT JOIN promotions p ON o.promo_id=p.promo_id WHERE o.customer_id=? ORDER BY o.order_date DESC";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,customerId); ResultSet rs=ps.executeQuery(); while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getOrdersByCustomer: "+e.getMessage()); }
        return list;
    }

    public void printOrderItems(int orderId) {
        String sql="SELECT oi.*,p.product_name,p.product_code FROM order_items oi JOIN products p ON oi.product_id=p.product_id WHERE oi.order_id=?";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,orderId); ResultSet rs=ps.executeQuery();
            System.out.printf("  %-8s %-30s %-6s %-12s %-12s%n","Code","Product","Qty","Unit Price","Total");
            System.out.println("  "+"-".repeat(72));
            while(rs.next())
                System.out.printf("  %-8s %-30s %-6d %-12.2f %-12.2f%n",
                    rs.getString("product_code"),rs.getString("product_name"),
                    rs.getInt("quantity"),rs.getDouble("unit_price"),rs.getDouble("total_price"));
        } catch (SQLException e) { System.out.println("[DB ERROR] printOrderItems: "+e.getMessage()); }
    }

    /** Revenue report grouped by month */
    public void printRevenueReport() {
        try (Statement st=conn.createStatement();
             ResultSet rs=st.executeQuery(
                "SELECT DATE_FORMAT(order_date,'%Y-%m') AS month, COUNT(*) AS orders, " +
                "SUM(total_amount) AS gross, SUM(discount_amount) AS discounts, SUM(final_amount) AS revenue " +
                "FROM orders WHERE status NOT IN ('CANCELLED','RETURNED') GROUP BY month ORDER BY month DESC LIMIT 12")) {
            System.out.printf("  %-10s %-8s %-14s %-12s %-12s%n","Month","Orders","Gross","Discounts","Revenue");
            System.out.println("  "+"-".repeat(58));
            double totalRev=0;
            while(rs.next()){
                System.out.printf("  %-10s %-8d %-14.2f %-12.2f %-12.2f%n",
                    rs.getString("month"),rs.getInt("orders"),
                    rs.getDouble("gross"),rs.getDouble("discounts"),rs.getDouble("revenue"));
                totalRev+=rs.getDouble("revenue");
            }
            System.out.println("  "+"-".repeat(58));
            System.out.printf("  %-19s Total Revenue : Rs. %.2f%n","",totalRev);
        } catch (SQLException e) { System.out.println("[DB ERROR] printRevenueReport: "+e.getMessage()); }
    }

    private Order map(ResultSet rs) throws SQLException {
        Order o=new Order();
        o.setOrderId(rs.getInt("order_id")); o.setOrderCode(rs.getString("order_code"));
        o.setCustomerId(rs.getInt("customer_id")); o.setCustomerName(rs.getString("customer_name"));
        o.setOrderDate(rs.getString("order_date")); o.setTotalAmount(rs.getDouble("total_amount"));
        o.setDiscountAmount(rs.getDouble("discount_amount")); o.setFinalAmount(rs.getDouble("final_amount"));
        o.setPromoId(rs.getInt("promo_id")); o.setPromoCode(rs.getString("promo_code"));
        o.setStatus(rs.getString("status")); o.setNotes(rs.getString("notes"));
        return o;
    }
}
