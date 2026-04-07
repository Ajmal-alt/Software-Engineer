package com.mms.dao;

import com.mms.model.Customer;
import com.mms.util.DBConnection;
import java.sql.*;
import java.util.*;

public class CustomerDAO {
    private Connection conn;
    public CustomerDAO() throws SQLException { this.conn=DBConnection.getInstance().getConnection(); }

    public boolean addCustomer(Customer c, int createdBy) {
        String sql="INSERT INTO customers (customer_code,first_name,last_name,email,phone,company,designation," +
                   "address,city,state,pincode,customer_type,segment,status,lead_id,assigned_to,created_by) " +
                   "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,'ACTIVE',?,?,?)";
        try (PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,c.getCustomerCode()); ps.setString(2,c.getFirstName());
            ps.setString(3,c.getLastName());     ps.setString(4,c.getEmail());
            ps.setString(5,c.getPhone());        ps.setString(6,c.getCompany());
            ps.setString(7,c.getDesignation());  ps.setString(8,c.getAddress());
            ps.setString(9,c.getCity());         ps.setString(10,c.getState());
            ps.setString(11,c.getPincode());     ps.setString(12,c.getCustomerType());
            ps.setString(13,c.getSegment());
            if(c.getLeadId()>0) ps.setInt(14,c.getLeadId()); else ps.setNull(14,Types.INTEGER);
            ps.setInt(15,createdBy); ps.setInt(16,createdBy);
            if(ps.executeUpdate()>0){ResultSet rs=ps.getGeneratedKeys();if(rs.next())c.setCustomerId(rs.getInt(1));return true;}
        } catch (SQLException e) { System.out.println("[DB ERROR] addCustomer: "+e.getMessage()); }
        return false;
    }

    /** Convert a lead to customer (marks lead as CONVERTED) */
    public boolean convertLeadToCustomer(int leadId, Customer c, int userId) {
        try {
            conn.setAutoCommit(false);
            // Mark lead converted
            try (PreparedStatement ps=conn.prepareStatement("UPDATE leads SET status='CONVERTED' WHERE lead_id=?")) {
                ps.setInt(1,leadId); ps.executeUpdate();
            }
            c.setLeadId(leadId);
            boolean added=addCustomer(c,userId);
            if(!added){conn.rollback();conn.setAutoCommit(true);return false;}
            conn.commit(); conn.setAutoCommit(true); return true;
        } catch (SQLException e) {
            try{conn.rollback();conn.setAutoCommit(true);}catch(SQLException ex){}
            System.out.println("[DB ERROR] convertLeadToCustomer: "+e.getMessage()); return false;
        }
    }

    public boolean updateCustomerStatus(int customerId, String status) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE customers SET status=? WHERE customer_id=?")) {
            ps.setString(1,status); ps.setInt(2,customerId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateSegment(int customerId, String segment) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE customers SET segment=? WHERE customer_id=?")) {
            ps.setString(1,segment); ps.setInt(2,customerId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public Customer getCustomerById(int customerId) {
        String sql="SELECT c.*,u.username AS assigned_name FROM customers c LEFT JOIN users u ON c.assigned_to=u.user_id WHERE c.customer_id=?";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,customerId); ResultSet rs=ps.executeQuery(); if(rs.next()) return map(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getCustomerById: "+e.getMessage()); }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list=new ArrayList<>();
        String sql="SELECT c.*,u.username AS assigned_name FROM customers c LEFT JOIN users u ON c.assigned_to=u.user_id ORDER BY c.customer_id";
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllCustomers: "+e.getMessage()); }
        return list;
    }

    public List<Customer> getCustomersBySegment(String segment) {
        List<Customer> list=new ArrayList<>();
        try (PreparedStatement ps=conn.prepareStatement("SELECT c.*,u.username AS assigned_name FROM customers c LEFT JOIN users u ON c.assigned_to=u.user_id WHERE c.segment=?")) {
            ps.setString(1,segment); ResultSet rs=ps.executeQuery(); while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getCustomersBySegment: "+e.getMessage()); }
        return list;
    }

    public void updateTotalPurchases(int customerId, double orderAmount) {
        try (PreparedStatement ps=conn.prepareStatement(
                "UPDATE customers SET total_purchases=total_purchases+?,loyalty_points=loyalty_points+FLOOR(?/100) WHERE customer_id=?")) {
            ps.setDouble(1,orderAmount); ps.setDouble(2,orderAmount); ps.setInt(3,customerId); ps.executeUpdate();
        } catch (SQLException e) { System.out.println("[DB ERROR] updateTotalPurchases: "+e.getMessage()); }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c=new Customer();
        c.setCustomerId(rs.getInt("customer_id")); c.setCustomerCode(rs.getString("customer_code"));
        c.setFirstName(rs.getString("first_name")); c.setLastName(rs.getString("last_name"));
        c.setEmail(rs.getString("email")); c.setPhone(rs.getString("phone"));
        c.setCompany(rs.getString("company")); c.setDesignation(rs.getString("designation"));
        c.setAddress(rs.getString("address")); c.setCity(rs.getString("city"));
        c.setState(rs.getString("state")); c.setPincode(rs.getString("pincode"));
        c.setCustomerType(rs.getString("customer_type")); c.setSegment(rs.getString("segment"));
        c.setTotalPurchases(rs.getDouble("total_purchases")); c.setLoyaltyPoints(rs.getInt("loyalty_points"));
        c.setStatus(rs.getString("status")); c.setLeadId(rs.getInt("lead_id"));
        c.setAssignedToName(rs.getString("assigned_name")); c.setCreatedAt(rs.getString("created_at"));
        return c;
    }
}
