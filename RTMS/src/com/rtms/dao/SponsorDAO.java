package com.rtms.dao;

import com.rtms.model.BudgetTransaction;
import com.rtms.model.Sponsor;
import com.rtms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SponsorDAO {
    private Connection conn;

    public SponsorDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addSponsor(Sponsor s, int createdBy) {
        String sql = "INSERT INTO sponsors "
                   + "(sponsor_code, company_name, contact_person, contact_phone, contact_email, "
                   + " industry, sponsor_type, contract_value, contract_start, contract_end, "
                   + " logo_placement, status, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getSponsorCode());   ps.setString(2, s.getCompanyName());
            ps.setString(3, s.getContactPerson()); ps.setString(4, s.getContactPhone());
            ps.setString(5, s.getContactEmail());  ps.setString(6, s.getIndustry());
            ps.setString(7, s.getSponsorType());   ps.setDouble(8, s.getContractValue());
            ps.setString(9, s.getContractStart()); ps.setString(10, s.getContractEnd());
            ps.setString(11, s.getLogoPlacement()); ps.setInt(12, createdBy);
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys(); if (rs.next()) s.setSponsorId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.out.println("[DB ERROR] addSponsor: " + e.getMessage()); }
        return false;
    }

    public boolean updateSponsorStatus(int sponsorId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE sponsors SET status = ? WHERE sponsor_id = ?")) {
            ps.setString(1, status); ps.setInt(2, sponsorId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Sponsor> getAllSponsors() {
        List<Sponsor> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM sponsors ORDER BY FIELD(sponsor_type,'TITLE','PRIMARY','SECONDARY','TECHNICAL','ASSOCIATE'), sponsor_id")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllSponsors: " + e.getMessage()); }
        return list;
    }

    public List<Sponsor> getActiveSponsors() {
        List<Sponsor> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM sponsors WHERE status='ACTIVE' "
                     + "ORDER BY FIELD(sponsor_type,'TITLE','PRIMARY','SECONDARY','TECHNICAL','ASSOCIATE')")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getActiveSponsors: " + e.getMessage()); }
        return list;
    }

    public void printSponsorSummary() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT sponsor_type, COUNT(*) AS cnt, SUM(contract_value) AS total_value "
                     + "FROM sponsors WHERE status='ACTIVE' GROUP BY sponsor_type "
                     + "ORDER BY FIELD(sponsor_type,'TITLE','PRIMARY','SECONDARY','TECHNICAL','ASSOCIATE')")) {
            System.out.printf("  %-15s %-8s %-20s%n", "Type", "Count", "Total Value (Rs.)");
            System.out.println("  " + "-".repeat(45));
            double grand = 0;
            while (rs.next()) {
                System.out.printf("  %-15s %-8d %-20.2f%n",
                        rs.getString("sponsor_type"), rs.getInt("cnt"), rs.getDouble("total_value"));
                grand += rs.getDouble("total_value");
            }
            System.out.println("  " + "-".repeat(45));
            System.out.printf("  %-24s %-20.2f%n", "TOTAL CONTRACT VALUE:", grand);
        } catch (SQLException e) { System.out.println("[DB ERROR] printSponsorSummary: " + e.getMessage()); }
    }

    private Sponsor map(ResultSet rs) throws SQLException {
        Sponsor s = new Sponsor();
        s.setSponsorId(rs.getInt("sponsor_id")); s.setSponsorCode(rs.getString("sponsor_code"));
        s.setCompanyName(rs.getString("company_name")); s.setContactPerson(rs.getString("contact_person"));
        s.setContactPhone(rs.getString("contact_phone")); s.setContactEmail(rs.getString("contact_email"));
        s.setIndustry(rs.getString("industry")); s.setSponsorType(rs.getString("sponsor_type"));
        s.setContractValue(rs.getDouble("contract_value")); s.setContractStart(rs.getString("contract_start"));
        s.setContractEnd(rs.getString("contract_end")); s.setLogoPlacement(rs.getString("logo_placement"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
