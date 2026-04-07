package com.rtms.dao;

import com.rtms.model.RaceEntry;
import com.rtms.model.RaceEvent;
import com.rtms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RaceEventDAO {
    private Connection conn;

    public RaceEventDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── Race Events ───────────────────────────────────────────────────────────

    public boolean createEvent(RaceEvent e, int createdBy) {
        String sql = "INSERT INTO race_events "
                   + "(event_code, event_name, series, circuit_name, city, country, "
                   + " event_date, qualifying_date, practice_date, total_laps, "
                   + " circuit_length_km, prize_money, status, notes, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'UPCOMING', ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getEventCode());      ps.setString(2, e.getEventName());
            ps.setString(3, e.getSeries());         ps.setString(4, e.getCircuitName());
            ps.setString(5, e.getCity());           ps.setString(6, e.getCountry());
            ps.setString(7, e.getEventDate());      ps.setString(8, e.getQualifyingDate());
            ps.setString(9, e.getPracticeDate());   ps.setInt(10, e.getTotalLaps());
            ps.setDouble(11, e.getCircuitLengthKm()); ps.setDouble(12, e.getPrizeMoney());
            ps.setString(13, e.getNotes());         ps.setInt(14, createdBy);
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys(); if (rs.next()) e.setEventId(rs.getInt(1));
                return true;
            }
        } catch (SQLException ex) { System.out.println("[DB ERROR] createEvent: " + ex.getMessage()); }
        return false;
    }

    public boolean updateEventStatus(int eventId, String status) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE race_events SET status = ? WHERE event_id = ?")) {
            ps.setString(1, status); ps.setInt(2, eventId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public RaceEvent getEventById(int eventId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM race_events WHERE event_id = ?")) {
            ps.setInt(1, eventId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapEvent(rs);
        } catch (SQLException e) { System.out.println("[DB ERROR] getEventById: " + e.getMessage()); }
        return null;
    }

    public List<RaceEvent> getAllEvents() {
        List<RaceEvent> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM race_events ORDER BY event_date DESC")) {
            while (rs.next()) list.add(mapEvent(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getAllEvents: " + e.getMessage()); }
        return list;
    }

    public List<RaceEvent> getUpcomingEvents() {
        List<RaceEvent> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM race_events WHERE status IN ('UPCOMING','QUALIFYING','RACE_DAY') "
                     + "ORDER BY event_date")) {
            while (rs.next()) list.add(mapEvent(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getUpcomingEvents: " + e.getMessage()); }
        return list;
    }

    // ── Race Entries (Results) ────────────────────────────────────────────────

    public boolean enterDriverForEvent(int eventId, int driverId, String carNumber) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO race_entries (event_id, driver_id, car_number) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE car_number = VALUES(car_number)")) {
            ps.setInt(1, eventId); ps.setInt(2, driverId); ps.setString(3, carNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.out.println("[DB ERROR] enterDriverForEvent: " + e.getMessage()); return false; }
    }

    public boolean recordRaceResult(RaceEntry entry, DriverDAO driverDAO) {
        try {
            conn.setAutoCommit(false);
            String sql = "UPDATE race_entries SET qualifying_pos=?, qualifying_time=?, "
                       + "race_pos=?, race_time=?, fastest_lap=?, laps_completed=?, "
                       + "points_scored=?, dnf=?, dnf_reason=?, notes=? "
                       + "WHERE event_id=? AND driver_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1,    entry.getQualifyingPos());
                ps.setString(2, entry.getQualifyingTime());
                if (entry.isDnf() || entry.getRacePos() == 0) ps.setNull(3, Types.INTEGER);
                else ps.setInt(3, entry.getRacePos());
                ps.setString(4, entry.getRaceTime());
                ps.setString(5, entry.getFastestLap());
                ps.setInt(6,    entry.getLapsCompleted());
                ps.setInt(7,    entry.getPointsScored());
                ps.setBoolean(8, entry.isDnf());
                ps.setString(9, entry.getDnfReason());
                ps.setString(10, entry.getNotes());
                ps.setInt(11,   entry.getEventId());
                ps.setInt(12,   entry.getDriverId());
                ps.executeUpdate();
            }
            // Update driver stats
            int winDelta    = (!entry.isDnf() && entry.getRacePos() == 1) ? 1 : 0;
            int podiumDelta = (!entry.isDnf() && entry.getRacePos() > 0 && entry.getRacePos() <= 3) ? 1 : 0;
            driverDAO.updateStats(entry.getDriverId(), 1, winDelta, podiumDelta, entry.getPointsScored());

            conn.commit(); conn.setAutoCommit(true); return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("[DB ERROR] recordRaceResult: " + e.getMessage()); return false;
        }
    }

    public List<RaceEntry> getEntriesForEvent(int eventId) {
        List<RaceEntry> list = new ArrayList<>();
        String sql = "SELECT re.*, CONCAT(d.first_name,' ',d.last_name) AS driver_name, "
                   + "e.event_name, e.event_date, e.series "
                   + "FROM race_entries re "
                   + "JOIN drivers d ON re.driver_id = d.driver_id "
                   + "JOIN race_events e ON re.event_id = e.event_id "
                   + "WHERE re.event_id = ? ORDER BY re.race_pos IS NULL, re.race_pos";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEntry(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getEntriesForEvent: " + e.getMessage()); }
        return list;
    }

    public List<RaceEntry> getEntriesForDriver(int driverId) {
        List<RaceEntry> list = new ArrayList<>();
        String sql = "SELECT re.*, CONCAT(d.first_name,' ',d.last_name) AS driver_name, "
                   + "e.event_name, e.event_date, e.series "
                   + "FROM race_entries re "
                   + "JOIN drivers d ON re.driver_id = d.driver_id "
                   + "JOIN race_events e ON re.event_id = e.event_id "
                   + "WHERE re.driver_id = ? ORDER BY e.event_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEntry(rs));
        } catch (SQLException e) { System.out.println("[DB ERROR] getEntriesForDriver: " + e.getMessage()); }
        return list;
    }

    private RaceEvent mapEvent(ResultSet rs) throws SQLException {
        RaceEvent e = new RaceEvent();
        e.setEventId(rs.getInt("event_id"));       e.setEventCode(rs.getString("event_code"));
        e.setEventName(rs.getString("event_name")); e.setSeries(rs.getString("series"));
        e.setCircuitName(rs.getString("circuit_name")); e.setCity(rs.getString("city"));
        e.setCountry(rs.getString("country"));     e.setEventDate(rs.getString("event_date"));
        e.setQualifyingDate(rs.getString("qualifying_date")); e.setPracticeDate(rs.getString("practice_date"));
        e.setTotalLaps(rs.getInt("total_laps")); e.setCircuitLengthKm(rs.getDouble("circuit_length_km"));
        e.setPrizeMoney(rs.getDouble("prize_money")); e.setStatus(rs.getString("status"));
        e.setNotes(rs.getString("notes"));
        return e;
    }

    private RaceEntry mapEntry(ResultSet rs) throws SQLException {
        RaceEntry e = new RaceEntry();
        e.setEntryId(rs.getInt("entry_id"));     e.setEventId(rs.getInt("event_id"));
        e.setEventName(rs.getString("event_name")); e.setEventDate(rs.getString("event_date"));
        e.setSeries(rs.getString("series"));     e.setDriverId(rs.getInt("driver_id"));
        e.setDriverName(rs.getString("driver_name")); e.setCarNumber(rs.getString("car_number"));
        e.setQualifyingPos(rs.getInt("qualifying_pos")); e.setQualifyingTime(rs.getString("qualifying_time"));
        e.setRacePos(rs.getInt("race_pos"));     e.setRaceTime(rs.getString("race_time"));
        e.setFastestLap(rs.getString("fastest_lap")); e.setLapsCompleted(rs.getInt("laps_completed"));
        e.setPointsScored(rs.getInt("points_scored")); e.setDnf(rs.getBoolean("dnf"));
        e.setDnfReason(rs.getString("dnf_reason")); e.setNotes(rs.getString("notes"));
        return e;
    }
}
