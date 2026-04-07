package com.rtms.dao;

import com.rtms.model.BudgetTransaction;
import com.rtms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {
    private Connection conn;

    public BudgetDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addTransaction(BudgetTransaction t, int recordedBy) {
        String sql = "INSERT INTO budget_transactions "
                   + "(txn_ref, category_id, txn_type, amount, description, txn_date, "
                   + " event_id, sponsor_id, recorded_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTxnRef());
            ps.setInt(2,    t.getCategoryId());
            ps.setString(3, t.getTxnType());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getDescription());
            ps.setString(6, t.getTxnDate());
            if (t.getEventId() > 0)   ps.setInt(7, t.getEventId());   else ps.setNull(7, Types.INTEGER);
            if (t.getSponsorId() > 0) ps.setInt(8, t.getSponsorId()); else ps.setNull(8, Types.INTEGER);
            ps.setInt(9, recordedBy);
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys(); if (rs.next()) t.setTxnId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.out.println("[DB ERROR] addTransaction: " + e.getMessage()); }
        return false;
    }

    public List<BudgetTransaction> getAllTransactions() {
        List<BudgetTransaction> list = new ArrayList<>();
        String sql = "SELECT bt.*, bc.category_name, e.event_name, s.company_name AS sponsor_name "
                   + "FROM budget_transactions bt "
                   + "JOIN budget_categories bc ON bt.category_id = bc.category_id "
                   + "LEFT JOIN race_events e ON bt.event_id = e.event_id "
                   + "LEFT JOIN sponsors s ON bt.sponsor_id = s.sponsor_id "
                   + "ORDER BY bt.txn_date DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllTransactions: " + e.getMessage()); }
        return list;
    }

    public List<BudgetTransaction> getTransactionsByType(String type) {
        List<BudgetTransaction> list = new ArrayList<>();
        String sql = "SELECT bt.*, bc.category_name, e.event_name, s.company_name AS sponsor_name "
                   + "FROM budget_transactions bt "
                   + "JOIN budget_categories bc ON bt.category_id = bc.category_id "
                   + "LEFT JOIN race_events e ON bt.event_id = e.event_id "
                   + "LEFT JOIN sponsors s ON bt.sponsor_id = s.sponsor_id "
                   + "WHERE bt.txn_type = ? ORDER BY bt.txn_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getTransactionsByType: " + e.getMessage()); }
        return list;
    }

    public void printProfitLossReport() {
        String sql = "SELECT bc.category_name, bt.txn_type, "
                   + "COUNT(*) AS txn_count, SUM(bt.amount) AS total "
                   + "FROM budget_transactions bt "
                   + "JOIN budget_categories bc ON bt.category_id = bc.category_id "
                   + "GROUP BY bc.category_id, bt.txn_type "
                   + "ORDER BY bt.txn_type DESC, total DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("  %-30s %-10s %-6s %-18s%n","Category","Type","Count","Amount (Rs.)");
            System.out.println("  " + "-".repeat(66));
            double totalIncome = 0, totalExpense = 0;
            while (rs.next()) {
                double amt = rs.getDouble("total");
                String type = rs.getString("txn_type");
                System.out.printf("  %-30s %-10s %-6d %-18.2f%n",
                        rs.getString("category_name"), type, rs.getInt("txn_count"), amt);
                if ("INCOME".equals(type))  totalIncome  += amt;
                else                        totalExpense += amt;
            }
            System.out.println("  " + "-".repeat(66));
            System.out.printf("  %-41s %-18.2f%n", "TOTAL INCOME:", totalIncome);
            System.out.printf("  %-41s %-18.2f%n", "TOTAL EXPENSE:", totalExpense);
            System.out.println("  " + "-".repeat(66));
            double net = totalIncome - totalExpense;
            System.out.printf("  %-41s %-18.2f%n", "NET PROFIT / (LOSS):", net);
            System.out.println("  " + (net >= 0 ? "  *** PROFITABLE ***" : "  *** LOSS ***"));
        } catch (SQLException e) { System.out.println("[DB ERROR] printProfitLossReport: " + e.getMessage()); }
    }

    public void printBudgetCategories() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM budget_categories ORDER BY budget_type, category_id")) {
            System.out.printf("  %-5s %-30s %-10s%n","ID","Category","Type");
            System.out.println("  " + "-".repeat(47));
            while (rs.next())
                System.out.printf("  %-5d %-30s %-10s%n",
                        rs.getInt("category_id"), rs.getString("category_name"), rs.getString("budget_type"));
        } catch (SQLException e) { System.out.println("[DB ERROR] printBudgetCategories: " + e.getMessage()); }
    }

    private BudgetTransaction map(ResultSet rs) throws SQLException {
        BudgetTransaction t = new BudgetTransaction();
        t.setTxnId(rs.getInt("txn_id")); t.setTxnRef(rs.getString("txn_ref"));
        t.setCategoryId(rs.getInt("category_id")); t.setCategoryName(rs.getString("category_name"));
        t.setTxnType(rs.getString("txn_type")); t.setAmount(rs.getDouble("amount"));
        t.setDescription(rs.getString("description")); t.setTxnDate(rs.getString("txn_date"));
        t.setEventId(rs.getInt("event_id")); t.setEventName(rs.getString("event_name"));
        t.setSponsorId(rs.getInt("sponsor_id")); t.setSponsorName(rs.getString("sponsor_name"));
        return t;
    }
}
