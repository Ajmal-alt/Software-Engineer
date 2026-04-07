package com.mms.dao;

import com.mms.model.Promotion;
import com.mms.util.DBConnection;
import java.sql.*;
import java.util.*;

public class PromotionDAO {
    private Connection conn;
    public PromotionDAO() throws SQLException { this.conn=DBConnection.getInstance().getConnection(); }

    public boolean addPromotion(Promotion p, int createdBy) {
        String sql="INSERT INTO promotions (promo_code,promo_name,promo_type,discount_value,min_purchase,max_discount," +
                   "start_date,end_date,usage_limit,applicable_to,description,status,created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,'ACTIVE',?)";
        try (PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,p.getPromoCode());   ps.setString(2,p.getPromoName());
            ps.setString(3,p.getPromoType());   ps.setDouble(4,p.getDiscountValue());
            ps.setDouble(5,p.getMinPurchase()); ps.setDouble(6,p.getMaxDiscount());
            ps.setString(7,p.getStartDate());   ps.setString(8,p.getEndDate());
            ps.setInt(9,p.getUsageLimit());     ps.setString(10,p.getApplicableTo());
            ps.setString(11,p.getDescription());ps.setInt(12,createdBy);
            if(ps.executeUpdate()>0){ResultSet rs=ps.getGeneratedKeys();if(rs.next())p.setPromoId(rs.getInt(1));return true;}
        } catch (SQLException e) { System.out.println("[DB ERROR] addPromotion: "+e.getMessage()); }
        return false;
    }

    public boolean updatePromoStatus(int promoId, String status) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE promotions SET status=? WHERE promo_id=?")) {
            ps.setString(1,status); ps.setInt(2,promoId); return ps.executeUpdate()>0;
        } catch (SQLException e) { return false; }
    }

    public boolean linkProductToPromo(int productId, int promoId) {
        try (PreparedStatement ps=conn.prepareStatement(
                "INSERT IGNORE INTO product_promotions (product_id,promo_id) VALUES (?,?)")) {
            ps.setInt(1,productId); ps.setInt(2,promoId); return ps.executeUpdate()>0;
        } catch (SQLException e) { System.out.println("[DB ERROR] linkProductToPromo: "+e.getMessage()); return false; }
    }

    /** Calculate discount amount for a given order total and promo */
    public double calculateDiscount(int promoId, double orderTotal) {
        try (PreparedStatement ps=conn.prepareStatement(
                "SELECT * FROM promotions WHERE promo_id=? AND status='ACTIVE' AND CURDATE() BETWEEN start_date AND end_date AND usage_count<usage_limit")) {
            ps.setInt(1,promoId); ResultSet rs=ps.executeQuery();
            if(rs.next()) {
                double minPurchase=rs.getDouble("min_purchase");
                if(orderTotal<minPurchase) return 0;
                String type=rs.getString("promo_type");
                double val=rs.getDouble("discount_value");
                double maxDisc=rs.getDouble("max_discount");
                double discount=0;
                if("PERCENTAGE_DISCOUNT".equals(type)||"BUNDLE".equals(type)) discount=orderTotal*(val/100.0);
                else if("FLAT_DISCOUNT".equals(type)) discount=val;
                if(maxDisc>0&&discount>maxDisc) discount=maxDisc;
                return discount;
            }
        } catch (SQLException e) { System.out.println("[DB ERROR] calculateDiscount: "+e.getMessage()); }
        return 0;
    }

    public void incrementUsage(int promoId) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE promotions SET usage_count=usage_count+1 WHERE promo_id=?")) {
            ps.setInt(1,promoId); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public Promotion getPromoById(int promoId) {
        try (PreparedStatement ps=conn.prepareStatement("SELECT * FROM promotions WHERE promo_id=?")) {
            ps.setInt(1,promoId); ResultSet rs=ps.executeQuery(); if(rs.next()) return map(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getPromoById: "+e.getMessage()); }
        return null;
    }

    public List<Promotion> getAllPromotions() {
        List<Promotion> list=new ArrayList<>();
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery("SELECT * FROM promotions ORDER BY promo_id")) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllPromotions: "+e.getMessage()); }
        return list;
    }

    public List<Promotion> getActivePromotions() {
        List<Promotion> list=new ArrayList<>();
        try (Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(
                "SELECT * FROM promotions WHERE status='ACTIVE' AND CURDATE() BETWEEN start_date AND end_date ORDER BY promo_id")) {
            while(rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getActivePromotions: "+e.getMessage()); }
        return list;
    }

    private Promotion map(ResultSet rs) throws SQLException {
        Promotion p=new Promotion();
        p.setPromoId(rs.getInt("promo_id")); p.setPromoCode(rs.getString("promo_code"));
        p.setPromoName(rs.getString("promo_name")); p.setPromoType(rs.getString("promo_type"));
        p.setDiscountValue(rs.getDouble("discount_value")); p.setMinPurchase(rs.getDouble("min_purchase"));
        p.setMaxDiscount(rs.getDouble("max_discount")); p.setStartDate(rs.getString("start_date"));
        p.setEndDate(rs.getString("end_date")); p.setUsageLimit(rs.getInt("usage_limit"));
        p.setUsageCount(rs.getInt("usage_count")); p.setApplicableTo(rs.getString("applicable_to"));
        p.setDescription(rs.getString("description")); p.setStatus(rs.getString("status"));
        return p;
    }
}
