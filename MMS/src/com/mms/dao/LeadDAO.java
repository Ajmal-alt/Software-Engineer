package com.mms.dao;

import com.mms.model.*;
import com.mms.util.DBConnection;
import java.sql.*;
import java.util.*;

public class LeadDAO {
    private Connection conn;
    public LeadDAO() throws SQLException { this.conn=DBConnection.getInstance().getConnection(); }

    public boolean addLead(Lead l, int createdBy) {
        String sql="INSERT INTO leads (lead_code,first_name,last_name,email,phone,company,designation," +
                   "city,state,source,interest_area,budget_range,status,priority,notes,assigned_to,created_by) " +
                   "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'NEW',?,?,?,?)";
        try (PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,l.getLeadCode());   ps.setString(2,l.getFirstName());
            ps.setString(3,l.getLastName());   ps.setString(4,l.getEmail());
            ps.setString(5,l.getPhone());      ps.setString(6,l.getCompany());
            ps.setString(7,l.getDesignation());ps.setString(8,l.getCity());
            ps.setString(9,l.getState());      ps.setString(10,l.getSource());
            ps.setString(11,l.getInterestArea()); ps.setString(12,l.getBudgetRange());
            ps.setString(13,l.getPriority());  ps.setString(14,l.getNotes());
            ps.setInt(15,l.getAssignedTo());   ps.setInt(16,createdBy);
            if (ps.executeUpdate()>0) {
                ResultSet rs=ps.getGeneratedKeys(); if(rs.next()) l.setLeadId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.out.println("[DB ERROR] addLead: "+e.getMessage()); }
        return false;
    }

    public boolean updateLeadStatus(int leadId, String status, String notes) {
        try (PreparedStatement ps=conn.prepareStatement(
                "UPDATE leads SET status=?,notes=? WHERE lead_id=?")) {
            ps.setString(1,status); ps.setString(2,notes); ps.setInt(3,leadId);
            return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public boolean reassignLead(int leadId, int userId) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE leads SET assigned_to=? WHERE lead_id=?")) {
            ps.setInt(1,userId); ps.setInt(2,leadId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public boolean logActivity(LeadActivity a) {
        String sql="INSERT INTO lead_activities (lead_id,activity_type,description,outcome,next_action,next_action_date,performed_by) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,a.getLeadId());        ps.setString(2,a.getActivityType());
            ps.setString(3,a.getDescription());ps.setString(4,a.getOutcome());
            ps.setString(5,a.getNextAction()); ps.setString(6,a.getNextActionDate());
            ps.setInt(7,Integer.parseInt(a.getPerformedByName()));
            return ps.executeUpdate()>0;
        } catch (SQLException e) { System.out.println("[DB ERROR] logActivity: "+e.getMessage()); return false; }
    }

    public Lead getLeadById(int leadId) {
        String sql="SELECT l.*,u.username AS assigned_name FROM leads l LEFT JOIN users u ON l.assigned_to=u.user_id WHERE l.lead_id=?";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,leadId); ResultSet rs=ps.executeQuery(); if(rs.next()) return map(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getLeadById: "+e.getMessage()); }
        return null;
    }

    public List<Lead> getAllLeads() {
        List<Lead> list=new ArrayList<>();
        String sql="SELECT l.*,u.username AS assigned_name FROM leads l LEFT JOIN users u ON l.assigned_to=u.user_id ORDER BY l.created_at DESC";
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllLeads: "+e.getMessage()); }
        return list;
    }

    public List<Lead> getLeadsByUser(int userId) {
        List<Lead> list=new ArrayList<>();
        String sql="SELECT l.*,u.username AS assigned_name FROM leads l LEFT JOIN users u ON l.assigned_to=u.user_id WHERE l.assigned_to=? ORDER BY l.created_at DESC";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,userId); ResultSet rs=ps.executeQuery(); while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getLeadsByUser: "+e.getMessage()); }
        return list;
    }

    public List<Lead> getLeadsByStatus(String status) {
        List<Lead> list=new ArrayList<>();
        String sql="SELECT l.*,u.username AS assigned_name FROM leads l LEFT JOIN users u ON l.assigned_to=u.user_id WHERE l.status=? ORDER BY l.priority DESC";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,status); ResultSet rs=ps.executeQuery(); while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getLeadsByStatus: "+e.getMessage()); }
        return list;
    }

    public List<LeadActivity> getActivitiesByLead(int leadId) {
        List<LeadActivity> list=new ArrayList<>();
        String sql="SELECT la.*,CONCAT(l.first_name,' ',l.last_name) AS lead_name,u.username AS agent_name " +
                   "FROM lead_activities la JOIN leads l ON la.lead_id=l.lead_id " +
                   "LEFT JOIN users u ON la.performed_by=u.user_id WHERE la.lead_id=? ORDER BY la.activity_date DESC";
        try (PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,leadId); ResultSet rs=ps.executeQuery();
            while(rs.next()) {
                LeadActivity a=new LeadActivity();
                a.setActivityId(rs.getInt("activity_id")); a.setLeadId(rs.getInt("lead_id"));
                a.setLeadName(rs.getString("lead_name"));  a.setActivityType(rs.getString("activity_type"));
                a.setDescription(rs.getString("description")); a.setOutcome(rs.getString("outcome"));
                a.setActivityDate(rs.getString("activity_date")); a.setNextAction(rs.getString("next_action"));
                a.setNextActionDate(rs.getString("next_action_date")); a.setPerformedByName(rs.getString("agent_name"));
                list.add(a);
            }
        } catch (SQLException e) { System.out.println("[DB ERROR] getActivitiesByLead: "+e.getMessage()); }
        return list;
    }

    private Lead map(ResultSet rs) throws SQLException {
        Lead l=new Lead();
        l.setLeadId(rs.getInt("lead_id")); l.setLeadCode(rs.getString("lead_code"));
        l.setFirstName(rs.getString("first_name")); l.setLastName(rs.getString("last_name"));
        l.setEmail(rs.getString("email")); l.setPhone(rs.getString("phone"));
        l.setCompany(rs.getString("company")); l.setDesignation(rs.getString("designation"));
        l.setCity(rs.getString("city")); l.setState(rs.getString("state"));
        l.setSource(rs.getString("source")); l.setInterestArea(rs.getString("interest_area"));
        l.setBudgetRange(rs.getString("budget_range")); l.setStatus(rs.getString("status"));
        l.setPriority(rs.getString("priority")); l.setNotes(rs.getString("notes"));
        l.setAssignedTo(rs.getInt("assigned_to")); l.setAssignedToName(rs.getString("assigned_name"));
        l.setCreatedAt(rs.getString("created_at"));
        return l;
    }
}
