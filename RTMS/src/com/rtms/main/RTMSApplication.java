package com.rtms.main;

import com.rtms.dao.*;
import com.rtms.model.*;
import com.rtms.util.ConsoleUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class RTMSApplication {

    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    private static UserDAO      userDAO;
    private static DriverDAO    driverDAO;
    private static StaffDAO     staffDAO;
    private static RaceEventDAO raceEventDAO;
    private static SponsorDAO   sponsorDAO;
    private static BudgetDAO    budgetDAO;

    public static void main(String[] args) {
        ConsoleUtil.printHeader("RACING TEAM MANAGEMENT SYSTEM (RTMS)");
        System.out.println("  Connecting to database...");
        try {
            userDAO      = new UserDAO();
            driverDAO    = new DriverDAO();
            staffDAO     = new StaffDAO();
            raceEventDAO = new RaceEventDAO();
            sponsorDAO   = new SponsorDAO();
            budgetDAO    = new BudgetDAO();
            ConsoleUtil.printSuccess("Connected successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Database connection failed: " + e.getMessage());
            System.out.println("  Check config/db.properties and ensure MySQL is running.");
            System.exit(1);
        }
        while (true) {
            try {
                if (currentUser == null) showLoginMenu();
                else                     showRoleDashboard();
            } catch (Exception e) {
                ConsoleUtil.printError("Unexpected error: " + e.getMessage());
            }
        }
    }

    private static void showLoginMenu() {
        ConsoleUtil.printHeader("LOGIN");
        System.out.print("  Username : "); String u = sc.nextLine().trim();
        System.out.print("  Password : "); String p = sc.nextLine().trim();
        currentUser = userDAO.authenticate(u, p);
        if (currentUser == null) {
            ConsoleUtil.printError("Invalid credentials. Try again.");
        } else {
            if ("DRIVER".equals(currentUser.getRole())) {
                Driver full = driverDAO.getDriverByUserId(currentUser.getUserId());
                if (full != null) {
                    Driver d = (Driver) currentUser;
                    d.setDriverId(full.getDriverId()); d.setDriverCode(full.getDriverCode());
                    d.setFirstName(full.getFirstName()); d.setLastName(full.getLastName());
                    d.setNationality(full.getNationality()); d.setPhone(full.getPhone());
                    d.setLicenseNumber(full.getLicenseNumber()); d.setLicenseGrade(full.getLicenseGrade());
                    d.setLicenseExpiry(full.getLicenseExpiry()); d.setExperienceYears(full.getExperienceYears());
                    d.setTotalRaces(full.getTotalRaces()); d.setTotalWins(full.getTotalWins());
                    d.setTotalPodiums(full.getTotalPodiums()); d.setChampionshipPts(full.getChampionshipPts());
                    d.setContractStart(full.getContractStart()); d.setContractEnd(full.getContractEnd());
                    d.setSalary(full.getSalary()); d.setDriverStatus(full.getDriverStatus());
                }
            }
            ConsoleUtil.printSuccess("Welcome " + currentUser.getUsername() + "! Role: " + currentUser.getRole());
            userDAO.logAction(currentUser.getUserId(), "LOGIN", "User logged in");
        }
    }

    private static void showRoleDashboard() {
        switch (currentUser.getRole()) {
            case "ADMIN":   adminMenu();   break;
            case "MANAGER": managerMenu(); break;
            case "DRIVER":  driverMenu();  break;
            default: currentUser = null;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void adminMenu() {
        ConsoleUtil.printHeader("ADMIN DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  manageDrivers();            break;
            case 2:  manageStaff();              break;
            case 3:  manageRaceEvents();         break;
            case 4:  recordRaceResults();        break;
            case 5:  viewRaceResults();          break;
            case 6:  championshipStandings();    break;
            case 7:  manageSponsors();           break;
            case 8:  budgetFinance();            break;
            case 9:  reportsMenu();              break;
            case 10: manageUsers();              break;
            case 11: viewSystemLogs();           break;
            case 12: logout();                  break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MANAGER MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void managerMenu() {
        ConsoleUtil.printHeader("MANAGER DASHBOARD");
        currentUser.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1:  manageDrivers();            break;
            case 2:  manageStaff();              break;
            case 3:  manageRaceEvents();         break;
            case 4:  recordRaceResults();        break;
            case 5:  viewRaceResults();          break;
            case 6:  championshipStandings();    break;
            case 7:  manageSponsors();           break;
            case 8:  budgetFinance();            break;
            case 9:  reportsMenu();              break;
            case 10: logout();                  break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DRIVER MENU
    // ══════════════════════════════════════════════════════════════════════════
    private static void driverMenu() {
        Driver driver = (Driver) currentUser;
        ConsoleUtil.printHeader("DRIVER DASHBOARD");
        driver.displayDashboard();
        System.out.print("\n  Choose option: ");
        switch (readInt()) {
            case 1: viewMyProfile(driver);          break;
            case 2: viewUpcomingEvents();           break;
            case 3: viewMyRaceHistory(driver);      break;
            case 4: championshipStandings();        break;
            case 5: viewMyContract(driver);         break;
            case 6: logout();                      break;
            default: ConsoleUtil.printError("Invalid option.");
        }
    }

    // ── Driver Self-Service ───────────────────────────────────────────────────
    private static void viewMyProfile(Driver d) {
        ConsoleUtil.printSection("MY PROFILE & STATS");
        Driver full = driverDAO.getDriverById(d.getDriverId());
        if (full == null) { ConsoleUtil.printError("Profile not found."); return; }
        System.out.println("\n" + "=".repeat(55));
        System.out.printf("  %-22s: %s%n", "Driver Code",      full.getDriverCode());
        System.out.printf("  %-22s: %s%n", "Full Name",        full.getFullName());
        System.out.printf("  %-22s: %s%n", "Nationality",      nvl(full.getNationality()));
        System.out.printf("  %-22s: %s%n", "Date of Birth",    nvl(full.getDateOfBirth()));
        System.out.printf("  %-22s: %s%n", "Phone",            nvl(full.getPhone()));
        System.out.printf("  %-22s: %s (%s) — Expiry: %s%n", "License",
                full.getLicenseNumber(), nvl(full.getLicenseGrade()), nvl(full.getLicenseExpiry()));
        System.out.printf("  %-22s: %d years%n", "Experience",  full.getExperienceYears());
        System.out.println("  " + "-".repeat(55));
        System.out.println("  CAREER STATISTICS:");
        System.out.printf("  %-22s: %d%n", "Total Races",      full.getTotalRaces());
        System.out.printf("  %-22s: %d%n", "Total Wins",       full.getTotalWins());
        System.out.printf("  %-22s: %d%n", "Total Podiums",    full.getTotalPodiums());
        System.out.printf("  %-22s: %d pts%n", "Championship Points", full.getChampionshipPts());
        if (full.getTotalRaces() > 0) {
            double winRate = (full.getTotalWins() * 100.0) / full.getTotalRaces();
            System.out.printf("  %-22s: %.1f%%%n", "Win Rate", winRate);
        }
        System.out.printf("  %-22s: %s%n", "Status",           full.getDriverStatus());
        System.out.println("=".repeat(55));
    }

    private static void viewUpcomingEvents() {
        ConsoleUtil.printSection("UPCOMING RACE EVENTS");
        List<RaceEvent> list = raceEventDAO.getUpcomingEvents();
        if (list.isEmpty()) { System.out.println("  No upcoming events scheduled."); return; }
        printEventTable(list);
    }

    private static void viewMyRaceHistory(Driver d) {
        ConsoleUtil.printSection("MY RACE HISTORY");
        List<RaceEntry> list = raceEventDAO.getEntriesForDriver(d.getDriverId());
        if (list.isEmpty()) { System.out.println("  No race history found."); return; }
        printEntryTable(list);
    }

    private static void viewMyContract(Driver d) {
        ConsoleUtil.printSection("MY CONTRACT DETAILS");
        Driver full = driverDAO.getDriverById(d.getDriverId());
        if (full == null) return;
        System.out.printf("  %-22s: %s%n", "Contract Start",   nvl(full.getContractStart()));
        System.out.printf("  %-22s: %s%n", "Contract End",     nvl(full.getContractEnd()));
        System.out.printf("  %-22s: Rs. %.2f / year%n", "Salary", full.getSalary());
        System.out.printf("  %-22s: %s%n", "Status",           full.getDriverStatus());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DRIVER MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageDrivers() {
        ConsoleUtil.printSection("MANAGE DRIVERS");
        System.out.println("  1. Add New Driver");
        System.out.println("  2. View All Drivers");
        System.out.println("  3. View Driver Profile");
        System.out.println("  4. Update Driver Status");
        System.out.println("  5. Update Contract");
        System.out.println("  6. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addDriver();          break;
            case 2: viewAllDrivers();     break;
            case 3: viewDriverProfile();  break;
            case 4: updateDriverStatus(); break;
            case 5: updateContract();     break;
        }
    }

    private static void addDriver() {
        ConsoleUtil.printSection("ADD NEW DRIVER");
        Driver d = new Driver();
        System.out.print("  Username                     : "); d.setUsername(sc.nextLine().trim());
        System.out.print("  Password                     : "); d.setPassword(sc.nextLine().trim());
        System.out.print("  Email                        : "); d.setEmail(sc.nextLine().trim());
        System.out.print("  Driver Code (e.g. DRV-004)   : "); d.setDriverCode(sc.nextLine().trim());
        System.out.print("  First Name                   : "); d.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name                    : "); d.setLastName(sc.nextLine().trim());
        System.out.print("  Date of Birth (YYYY-MM-DD)   : "); d.setDateOfBirth(sc.nextLine().trim());
        System.out.print("  Nationality                  : "); d.setNationality(sc.nextLine().trim());
        System.out.print("  Phone                        : "); d.setPhone(sc.nextLine().trim());
        System.out.print("  License Number               : "); d.setLicenseNumber(sc.nextLine().trim());
        System.out.println("  License Grade: A / B / C / SUPERLICENSE");
        System.out.print("  License Grade                : "); d.setLicenseGrade(sc.nextLine().trim().toUpperCase());
        System.out.print("  License Expiry (YYYY-MM-DD)  : "); d.setLicenseExpiry(sc.nextLine().trim());
        System.out.print("  Experience (years)           : "); d.setExperienceYears(readInt());
        System.out.print("  Contract Start (YYYY-MM-DD)  : "); d.setContractStart(sc.nextLine().trim());
        System.out.print("  Contract End   (YYYY-MM-DD)  : "); d.setContractEnd(sc.nextLine().trim());
        System.out.print("  Annual Salary (Rs.)          : "); d.setSalary(readDouble());
        if (driverDAO.addDriver(d, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Driver added! ID: " + d.getDriverId());
            userDAO.logAction(currentUser.getUserId(), "ADD_DRIVER", "Code=" + d.getDriverCode());
        } else ConsoleUtil.printError("Failed. Code, license, username or email may already exist.");
    }

    private static void viewAllDrivers() {
        ConsoleUtil.printSection("ALL DRIVERS");
        List<Driver> list = driverDAO.getAllDrivers();
        if (list.isEmpty()) { System.out.println("  No drivers found."); return; }
        System.out.printf("  %-5s %-10s %-22s %-12s %-6s %-6s %-6s %-8s %-12s%n",
                "ID","Code","Name","Nationality","Races","Wins","Pods","Pts","Status");
        System.out.println("  " + "-".repeat(93));
        for (Driver d : list)
            System.out.printf("  %-5d %-10s %-22s %-12s %-6d %-6d %-6d %-8d %-12s%n",
                    d.getDriverId(), d.getDriverCode(), d.getFullName(),
                    nvl(d.getNationality()), d.getTotalRaces(), d.getTotalWins(),
                    d.getTotalPodiums(), d.getChampionshipPts(), d.getDriverStatus());
    }

    private static void viewDriverProfile() {
        viewAllDrivers();
        System.out.print("\n  Driver ID: "); int id = readInt();
        Driver d = driverDAO.getDriverById(id);
        if (d == null) { ConsoleUtil.printError("Driver not found."); return; }
        ConsoleUtil.printSection("DRIVER PROFILE — " + d.getFullName());
        System.out.printf("  %-22s: %s%n","Code",             d.getDriverCode());
        System.out.printf("  %-22s: %s%n","Name",             d.getFullName());
        System.out.printf("  %-22s: %s%n","Nationality",      nvl(d.getNationality()));
        System.out.printf("  %-22s: %s%n","DOB",              nvl(d.getDateOfBirth()));
        System.out.printf("  %-22s: %s%n","Phone",            nvl(d.getPhone()));
        System.out.printf("  %-22s: %s (%s) Exp: %s%n","License",
                d.getLicenseNumber(), nvl(d.getLicenseGrade()), nvl(d.getLicenseExpiry()));
        System.out.printf("  %-22s: %d years%n","Experience",  d.getExperienceYears());
        System.out.printf("  %-22s: %d | Wins: %d | Pods: %d | Pts: %d%n","Races",
                d.getTotalRaces(), d.getTotalWins(), d.getTotalPodiums(), d.getChampionshipPts());
        System.out.printf("  %-22s: %s — %s  Rs. %.2f%n","Contract",
                nvl(d.getContractStart()), nvl(d.getContractEnd()), d.getSalary());
        System.out.printf("  %-22s: %s%n","Status",           d.getDriverStatus());
    }

    private static void updateDriverStatus() {
        viewAllDrivers();
        System.out.print("\n  Driver ID : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INJURED / SUSPENDED / RETIRED / CONTRACT_ENDED");
        System.out.print("  New Status: "); String s = sc.nextLine().trim().toUpperCase();
        if (driverDAO.updateDriverStatus(id, s)) ConsoleUtil.printSuccess("Driver status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void updateContract() {
        viewAllDrivers();
        System.out.print("\n  Driver ID           : "); int id = readInt();
        System.out.print("  New Contract Start  : "); String start = sc.nextLine().trim();
        System.out.print("  New Contract End    : "); String end   = sc.nextLine().trim();
        System.out.print("  New Annual Salary   : "); double sal = readDouble();
        if (driverDAO.updateContract(id, start, end, sal))
            ConsoleUtil.printSuccess("Contract updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STAFF MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageStaff() {
        ConsoleUtil.printSection("MANAGE STAFF");
        System.out.println("  1. Add New Staff Member");
        System.out.println("  2. View All Staff");
        System.out.println("  3. View by Department");
        System.out.println("  4. Update Staff Status");
        System.out.println("  5. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addStaff();           break;
            case 2: viewAllStaff();       break;
            case 3: viewStaffByDept();    break;
            case 4: updateStaffStatus();  break;
        }
    }

    private static void addStaff() {
        ConsoleUtil.printSection("ADD NEW STAFF MEMBER");
        Staff s = new Staff();
        System.out.print("  Staff Code (e.g. STF-007): "); s.setStaffCode(sc.nextLine().trim());
        System.out.print("  First Name               : "); s.setFirstName(sc.nextLine().trim());
        System.out.print("  Last Name                : "); s.setLastName(sc.nextLine().trim());
        System.out.print("  Role Title               : "); s.setRoleTitle(sc.nextLine().trim());
        System.out.println("  Dept: ENGINEERING/STRATEGY/MECHANICS/LOGISTICS/MEDICAL/MEDIA/MANAGEMENT");
        System.out.print("  Department               : "); s.setDepartment(sc.nextLine().trim().toUpperCase());
        System.out.print("  Phone                    : "); s.setPhone(sc.nextLine().trim());
        System.out.print("  Email                    : "); s.setEmail(sc.nextLine().trim());
        System.out.print("  Nationality              : "); s.setNationality(sc.nextLine().trim());
        System.out.print("  Contract Start (YYYY-MM-DD): "); s.setContractStart(sc.nextLine().trim());
        System.out.print("  Contract End   (YYYY-MM-DD): "); s.setContractEnd(sc.nextLine().trim());
        System.out.print("  Annual Salary (Rs.)        : "); s.setSalary(readDouble());
        if (staffDAO.addStaff(s, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Staff member added! ID: " + s.getStaffId());
            userDAO.logAction(currentUser.getUserId(), "ADD_STAFF", "Code=" + s.getStaffCode());
        } else ConsoleUtil.printError("Failed. Staff code may already exist.");
    }

    private static void viewAllStaff() {
        ConsoleUtil.printSection("ALL STAFF");
        List<Staff> list = staffDAO.getAllStaff();
        if (list.isEmpty()) { System.out.println("  No staff found."); return; }
        System.out.printf("  %-5s %-10s %-22s %-25s %-14s %-10s%n",
                "ID","Code","Name","Role","Department","Status");
        System.out.println("  " + "-".repeat(90));
        for (Staff s : list)
            System.out.printf("  %-5d %-10s %-22s %-25s %-14s %-10s%n",
                    s.getStaffId(), s.getStaffCode(), s.getFullName(),
                    nvl(s.getRoleTitle()), nvl(s.getDepartment()), s.getStatus());
    }

    private static void viewStaffByDept() {
        System.out.println("  ENGINEERING/STRATEGY/MECHANICS/LOGISTICS/MEDICAL/MEDIA/MANAGEMENT");
        System.out.print("  Department: "); String dept = sc.nextLine().trim().toUpperCase();
        ConsoleUtil.printSection("STAFF — " + dept);
        List<Staff> list = staffDAO.getStaffByDepartment(dept);
        if (list.isEmpty()) System.out.println("  No staff in this department.");
        else for (Staff s : list)
            System.out.printf("  [%d] %s — %s | %s | Rs.%.0f%n",
                    s.getStaffId(), s.getFullName(), nvl(s.getRoleTitle()),
                    nvl(s.getNationality()), s.getSalary());
    }

    private static void updateStaffStatus() {
        viewAllStaff();
        System.out.print("\n  Staff ID   : "); int id = readInt();
        System.out.println("  Status: ACTIVE / ON_LEAVE / SUSPENDED / TERMINATED");
        System.out.print("  New Status : "); String s = sc.nextLine().trim().toUpperCase();
        if (staffDAO.updateStaffStatus(id, s)) ConsoleUtil.printSuccess("Staff status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RACE EVENT MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageRaceEvents() {
        ConsoleUtil.printSection("MANAGE RACE EVENTS");
        System.out.println("  1. Create New Race Event");
        System.out.println("  2. View All Events");
        System.out.println("  3. View Upcoming Events");
        System.out.println("  4. Update Event Status");
        System.out.println("  5. Enter Driver for Event");
        System.out.println("  6. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: createRaceEvent();      break;
            case 2: viewAllEvents();        break;
            case 3: viewUpcomingEvents();   break;
            case 4: updateEventStatus();    break;
            case 5: enterDriverForEvent();  break;
        }
    }

    private static void createRaceEvent() {
        ConsoleUtil.printSection("CREATE RACE EVENT");
        RaceEvent e = new RaceEvent();
        System.out.print("  Event Code (e.g. RE-2026-007) : "); e.setEventCode(sc.nextLine().trim().toUpperCase());
        System.out.print("  Event Name                    : "); e.setEventName(sc.nextLine().trim());
        System.out.println("  Series: F1/F2/F3/GT/RALLY/ENDURANCE/MOTOGP/SUPERBIKE/LOCAL");
        System.out.print("  Series                        : "); e.setSeries(sc.nextLine().trim().toUpperCase());
        System.out.print("  Circuit Name                  : "); e.setCircuitName(sc.nextLine().trim());
        System.out.print("  City                          : "); e.setCity(sc.nextLine().trim());
        System.out.print("  Country                       : "); e.setCountry(sc.nextLine().trim());
        System.out.print("  Race Date (YYYY-MM-DD)        : "); e.setEventDate(sc.nextLine().trim());
        System.out.print("  Qualifying Date (YYYY-MM-DD)  : "); e.setQualifyingDate(sc.nextLine().trim());
        System.out.print("  Practice Date   (YYYY-MM-DD)  : "); e.setPracticeDate(sc.nextLine().trim());
        System.out.print("  Total Laps                    : "); e.setTotalLaps(readInt());
        System.out.print("  Circuit Length (km)           : "); e.setCircuitLengthKm(readDouble());
        System.out.print("  Prize Money (Rs.)             : "); e.setPrizeMoney(readDouble());
        System.out.print("  Notes                         : "); e.setNotes(sc.nextLine().trim());
        if (raceEventDAO.createEvent(e, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Race event created! ID: " + e.getEventId());
            userDAO.logAction(currentUser.getUserId(), "CREATE_EVENT", "Code=" + e.getEventCode());
        } else ConsoleUtil.printError("Failed. Event code may already exist.");
    }

    private static void viewAllEvents() {
        ConsoleUtil.printSection("ALL RACE EVENTS");
        printEventTable(raceEventDAO.getAllEvents());
    }

    private static void printEventTable(List<RaceEvent> list) {
        if (list.isEmpty()) { System.out.println("  No events found."); return; }
        System.out.printf("  %-5s %-14s %-28s %-6s %-15s %-12s %-20s%n",
                "ID","Code","Event","Series","Country","Date","Status");
        System.out.println("  " + "-".repeat(103));
        for (RaceEvent e : list)
            System.out.printf("  %-5d %-14s %-28s %-6s %-15s %-12s %-20s%n",
                    e.getEventId(), e.getEventCode(),
                    e.getEventName().length() > 26 ? e.getEventName().substring(0, 25) + "." : e.getEventName(),
                    e.getSeries(), nvl(e.getCountry()), nvl(e.getEventDate()), e.getStatus());
    }

    private static void updateEventStatus() {
        viewAllEvents();
        System.out.print("\n  Event ID   : "); int id = readInt();
        System.out.println("  Status: UPCOMING/QUALIFYING/RACE_DAY/COMPLETED/CANCELLED/POSTPONED");
        System.out.print("  New Status : "); String s = sc.nextLine().trim().toUpperCase();
        if (raceEventDAO.updateEventStatus(id, s)) ConsoleUtil.printSuccess("Event status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    private static void enterDriverForEvent() {
        ConsoleUtil.printSection("ENTER DRIVER FOR EVENT");
        viewAllEvents();
        System.out.print("\n  Event ID  : "); int eventId = readInt();
        viewAllDrivers();
        System.out.print("\n  Driver ID : "); int driverId = readInt();
        System.out.print("  Car Number: "); String carNo = sc.nextLine().trim();
        if (raceEventDAO.enterDriverForEvent(eventId, driverId, carNo))
            ConsoleUtil.printSuccess("Driver entered for event.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RACE RESULTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void recordRaceResults() {
        ConsoleUtil.printSection("RECORD RACE RESULTS");
        viewAllEvents();
        System.out.print("\n  Event ID: "); int eventId = readInt();
        RaceEvent event = raceEventDAO.getEventById(eventId);
        if (event == null) { ConsoleUtil.printError("Event not found."); return; }

        List<RaceEntry> entries = raceEventDAO.getEntriesForEvent(eventId);
        if (entries.isEmpty()) {
            ConsoleUtil.printError("No drivers entered for this event. Use option 5 to enter drivers first.");
            return;
        }
        System.out.println("  Drivers entered for: " + event.getEventName());
        for (RaceEntry en : entries)
            System.out.printf("  [%d] %s — Car #%s%n", en.getDriverId(), en.getDriverName(), en.getCarNumber());

        System.out.print("  Driver ID to record result: "); int driverId = readInt();
        RaceEntry entry = new RaceEntry();
        entry.setEventId(eventId); entry.setDriverId(driverId);

        System.out.print("  Qualifying Position  : "); entry.setQualifyingPos(readInt());
        System.out.print("  Qualifying Time      : "); entry.setQualifyingTime(sc.nextLine().trim());
        System.out.print("  DNF? (Y/N)           : ");
        boolean dnf = sc.nextLine().trim().equalsIgnoreCase("Y");
        entry.setDnf(dnf);
        if (dnf) {
            System.out.print("  DNF Reason           : "); entry.setDnfReason(sc.nextLine().trim());
            System.out.print("  Laps Completed       : "); entry.setLapsCompleted(readInt());
            entry.setRacePos(0); entry.setPointsScored(0);
        } else {
            System.out.print("  Race Position        : "); entry.setRacePos(readInt());
            System.out.print("  Race Time            : "); entry.setRaceTime(sc.nextLine().trim());
            System.out.print("  Fastest Lap Time     : "); entry.setFastestLap(sc.nextLine().trim());
            System.out.print("  Laps Completed       : "); entry.setLapsCompleted(readInt());
            System.out.print("  Points Scored        : "); entry.setPointsScored(readInt());
        }
        System.out.print("  Notes                : "); entry.setNotes(sc.nextLine().trim());

        if (raceEventDAO.recordRaceResult(entry, driverDAO)) {
            ConsoleUtil.printSuccess("Race result recorded and driver stats updated!");
            userDAO.logAction(currentUser.getUserId(), "RECORD_RESULT",
                    "EventID=" + eventId + " DriverID=" + driverId);
        } else ConsoleUtil.printError("Failed to record result.");
    }

    private static void viewRaceResults() {
        ConsoleUtil.printSection("VIEW RACE RESULTS");
        System.out.println("  1. Results for a specific event");
        System.out.println("  2. Race history for a specific driver");
        System.out.println("  3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            viewAllEvents();
            System.out.print("\n  Event ID: "); int id = readInt();
            RaceEvent ev = raceEventDAO.getEventById(id);
            if (ev != null) { ConsoleUtil.printSection("RESULTS — " + ev.getEventName()); }
            printEntryTable(raceEventDAO.getEntriesForEvent(id));
        } else if (ch == 2) {
            viewAllDrivers();
            System.out.print("\n  Driver ID: "); int id = readInt();
            Driver d = driverDAO.getDriverById(id);
            if (d != null) { ConsoleUtil.printSection("RACE HISTORY — " + d.getFullName()); }
            printEntryTable(raceEventDAO.getEntriesForDriver(id));
        }
    }

    private static void printEntryTable(List<RaceEntry> list) {
        if (list.isEmpty()) { System.out.println("  No entries found."); return; }
        System.out.printf("  %-22s %-8s %-6s %-6s %-8s %-6s %-6s %-5s%n",
                "Event","Series","QPos","RPos","FastLap","Laps","Pts","DNF");
        System.out.println("  " + "-".repeat(72));
        for (RaceEntry e : list)
            System.out.printf("  %-22s %-8s %-6s %-6s %-8s %-6d %-6d %-5s%n",
                    e.getEventName().length()>20 ? e.getEventName().substring(0,19)+"." : e.getEventName(),
                    nvl(e.getSeries()),
                    e.getQualifyingPos() > 0 ? "P" + e.getQualifyingPos() : "-",
                    e.isDnf() ? "DNF" : (e.getRacePos() > 0 ? "P" + e.getRacePos() : "-"),
                    nvl(e.getFastestLap()), e.getLapsCompleted(),
                    e.getPointsScored(), e.isDnf() ? "YES" : "NO");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CHAMPIONSHIP STANDINGS
    // ══════════════════════════════════════════════════════════════════════════
    private static void championshipStandings() {
        ConsoleUtil.printSection("CHAMPIONSHIP STANDINGS");
        List<Driver> list = driverDAO.getAllDrivers();
        if (list.isEmpty()) { System.out.println("  No drivers found."); return; }
        System.out.printf("  %-5s %-10s %-22s %-12s %-6s %-6s %-6s %-8s%n",
                "Rank","Code","Driver","Nationality","Races","Wins","Pods","Points");
        System.out.println("  " + "-".repeat(80));
        int rank = 1;
        for (Driver d : list)
            System.out.printf("  %-5d %-10s %-22s %-12s %-6d %-6d %-6d %-8d%n",
                    rank++, d.getDriverCode(), d.getFullName(),
                    nvl(d.getNationality()), d.getTotalRaces(),
                    d.getTotalWins(), d.getTotalPodiums(), d.getChampionshipPts());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SPONSOR MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageSponsors() {
        ConsoleUtil.printSection("MANAGE SPONSORS");
        System.out.println("  1. Add New Sponsor");
        System.out.println("  2. View All Sponsors");
        System.out.println("  3. View Active Sponsors");
        System.out.println("  4. Sponsor Portfolio Summary");
        System.out.println("  5. Update Sponsor Status");
        System.out.println("  6. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: addSponsor();            break;
            case 2: viewAllSponsors();       break;
            case 3: viewActiveSponsors();    break;
            case 4: sponsorDAO.printSponsorSummary(); break;
            case 5: updateSponsorStatus();   break;
        }
    }

    private static void addSponsor() {
        ConsoleUtil.printSection("ADD NEW SPONSOR");
        Sponsor s = new Sponsor();
        System.out.print("  Sponsor Code (e.g. SP-007)   : "); s.setSponsorCode(sc.nextLine().trim());
        System.out.print("  Company Name                 : "); s.setCompanyName(sc.nextLine().trim());
        System.out.print("  Contact Person               : "); s.setContactPerson(sc.nextLine().trim());
        System.out.print("  Contact Phone                : "); s.setContactPhone(sc.nextLine().trim());
        System.out.print("  Contact Email                : "); s.setContactEmail(sc.nextLine().trim());
        System.out.print("  Industry                     : "); s.setIndustry(sc.nextLine().trim());
        System.out.println("  Type: TITLE/PRIMARY/SECONDARY/TECHNICAL/ASSOCIATE");
        System.out.print("  Sponsor Type                 : "); s.setSponsorType(sc.nextLine().trim().toUpperCase());
        System.out.print("  Contract Value (Rs.)         : "); s.setContractValue(readDouble());
        System.out.print("  Contract Start (YYYY-MM-DD)  : "); s.setContractStart(sc.nextLine().trim());
        System.out.print("  Contract End   (YYYY-MM-DD)  : "); s.setContractEnd(sc.nextLine().trim());
        System.out.print("  Logo Placement               : "); s.setLogoPlacement(sc.nextLine().trim());
        if (sponsorDAO.addSponsor(s, currentUser.getUserId())) {
            ConsoleUtil.printSuccess("Sponsor added! ID: " + s.getSponsorId());
            userDAO.logAction(currentUser.getUserId(), "ADD_SPONSOR", "Code=" + s.getSponsorCode());
        } else ConsoleUtil.printError("Failed. Sponsor code may already exist.");
    }

    private static void viewAllSponsors() {
        ConsoleUtil.printSection("ALL SPONSORS");
        printSponsorTable(sponsorDAO.getAllSponsors());
    }

    private static void viewActiveSponsors() {
        ConsoleUtil.printSection("ACTIVE SPONSORS");
        printSponsorTable(sponsorDAO.getActiveSponsors());
    }

    private static void printSponsorTable(List<Sponsor> list) {
        if (list.isEmpty()) { System.out.println("  No sponsors found."); return; }
        System.out.printf("  %-5s %-8s %-25s %-12s %-16s %-12s %-10s%n",
                "ID","Code","Company","Type","Contract Value","Contract End","Status");
        System.out.println("  " + "-".repeat(93));
        for (Sponsor s : list)
            System.out.printf("  %-5d %-8s %-25s %-12s %-16.0f %-12s %-10s%n",
                    s.getSponsorId(), s.getSponsorCode(), s.getCompanyName(),
                    s.getSponsorType(), s.getContractValue(),
                    nvl(s.getContractEnd()), s.getStatus());
    }

    private static void updateSponsorStatus() {
        viewAllSponsors();
        System.out.print("\n  Sponsor ID : "); int id = readInt();
        System.out.println("  Status: ACTIVE / INACTIVE / NEGOTIATING / EXPIRED");
        System.out.print("  New Status : "); String s = sc.nextLine().trim().toUpperCase();
        if (sponsorDAO.updateSponsorStatus(id, s)) ConsoleUtil.printSuccess("Sponsor status updated.");
        else ConsoleUtil.printError("Failed.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUDGET & FINANCE
    // ══════════════════════════════════════════════════════════════════════════
    private static void budgetFinance() {
        ConsoleUtil.printSection("BUDGET & FINANCE");
        System.out.println("  1. Record Transaction (Income)");
        System.out.println("  2. Record Transaction (Expense)");
        System.out.println("  3. View All Transactions");
        System.out.println("  4. View Income Transactions");
        System.out.println("  5. View Expense Transactions");
        System.out.println("  6. Profit & Loss Report");
        System.out.println("  7. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: recordTransaction("INCOME");   break;
            case 2: recordTransaction("EXPENSE");  break;
            case 3: viewAllTransactions();         break;
            case 4: viewTransactionsByType("INCOME");  break;
            case 5: viewTransactionsByType("EXPENSE"); break;
            case 6: budgetDAO.printProfitLossReport(); break;
        }
    }

    private static void recordTransaction(String type) {
        ConsoleUtil.printSection("RECORD " + type + " TRANSACTION");
        budgetDAO.printBudgetCategories();
        System.out.print("\n  Category ID    : "); int catId = readInt();
        BudgetTransaction t = new BudgetTransaction();
        t.setCategoryId(catId); t.setTxnType(type);
        t.setTxnRef("TXN-" + System.currentTimeMillis() % 1000000);
        System.out.print("  Amount (Rs.)   : "); t.setAmount(readDouble());
        System.out.print("  Description    : "); t.setDescription(sc.nextLine().trim());
        System.out.print("  Date (YYYY-MM-DD): "); t.setTxnDate(sc.nextLine().trim());
        System.out.print("  Event ID (0=none): "); t.setEventId(readInt());
        System.out.print("  Sponsor ID (0=none): "); t.setSponsorId(readInt());
        if (budgetDAO.addTransaction(t, currentUser.getUserId())) {
            ConsoleUtil.printSuccess(type + " recorded! Ref: " + t.getTxnRef());
            userDAO.logAction(currentUser.getUserId(), "RECORD_TXN",
                    type + " Rs." + t.getAmount());
        } else ConsoleUtil.printError("Failed.");
    }

    private static void viewAllTransactions() {
        ConsoleUtil.printSection("ALL TRANSACTIONS");
        printTransactionTable(budgetDAO.getAllTransactions());
    }

    private static void viewTransactionsByType(String type) {
        ConsoleUtil.printSection(type + " TRANSACTIONS");
        printTransactionTable(budgetDAO.getTransactionsByType(type));
    }

    private static void printTransactionTable(List<BudgetTransaction> list) {
        if (list.isEmpty()) { System.out.println("  No transactions found."); return; }
        System.out.printf("  %-5s %-14s %-22s %-10s %-16s %-12s%n",
                "ID","Ref","Category","Type","Amount","Date");
        System.out.println("  " + "-".repeat(82));
        for (BudgetTransaction t : list)
            System.out.printf("  %-5d %-14s %-22s %-10s %-16.2f %-12s%n",
                    t.getTxnId(), t.getTxnRef(), nvl(t.getCategoryName()),
                    t.getTxnType(), t.getAmount(), t.getTxnDate());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void reportsMenu() {
        ConsoleUtil.printSection("REPORTS");
        System.out.println("  1. Driver Roster");
        System.out.println("  2. Staff Roster");
        System.out.println("  3. Race Calendar");
        System.out.println("  4. Championship Standings");
        System.out.println("  5. Sponsor Portfolio");
        System.out.println("  6. P&L Summary");
        System.out.println("  7. Back");
        System.out.print("  Choice: ");
        switch (readInt()) {
            case 1: viewAllDrivers();               break;
            case 2: viewAllStaff();                 break;
            case 3: viewAllEvents();                break;
            case 4: championshipStandings();        break;
            case 5: sponsorDAO.printSponsorSummary(); break;
            case 6: budgetDAO.printProfitLossReport(); break;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  USER MANAGEMENT (Admin only)
    // ══════════════════════════════════════════════════════════════════════════
    private static void manageUsers() {
        ConsoleUtil.printSection("MANAGE USERS");
        List<User> list = userDAO.getAllUsers();
        System.out.printf("  %-5s %-20s %-30s %-12s %-8s%n","ID","Username","Email","Role","Active");
        System.out.println("  " + "-".repeat(77));
        for (User u : list)
            System.out.printf("  %-5d %-20s %-30s %-12s %-8s%n",
                    u.getUserId(), u.getUsername(), u.getEmail(),
                    u.getRole(), u.isStatus() ? "Yes" : "No");
        System.out.println("\n  1. Add User   2. Deactivate User   3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            System.out.print("  Username : "); String un = sc.nextLine().trim();
            System.out.print("  Password : "); String pw = sc.nextLine().trim();
            System.out.print("  Email    : "); String em = sc.nextLine().trim();
            System.out.println("  Role: ADMIN / MANAGER / DRIVER");
            System.out.print("  Role     : "); String rl = sc.nextLine().trim().toUpperCase();
            if (userDAO.createUser(un, pw, em, rl)) ConsoleUtil.printSuccess("User created.");
            else ConsoleUtil.printError("Failed.");
        } else if (ch == 2) {
            System.out.print("  User ID to deactivate: "); int id = readInt();
            if (userDAO.deactivateUser(id)) ConsoleUtil.printSuccess("User deactivated.");
            else ConsoleUtil.printError("Failed.");
        }
    }

    private static void viewSystemLogs() {
        ConsoleUtil.printSection("SYSTEM LOGS (Last 30)");
        userDAO.printSystemLogs();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private static void logout() {
        userDAO.logAction(currentUser.getUserId(), "LOGOUT", "User logged out");
        ConsoleUtil.printSuccess("Logged out. Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    private static int readInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private static double readDouble() {
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (Exception e) { return 0.0; }
    }

    private static String nvl(String s) { return s == null ? "-" : s; }
}
