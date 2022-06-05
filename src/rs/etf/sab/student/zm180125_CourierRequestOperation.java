/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

public class zm180125_CourierRequestOperation implements CourierRequestOperation {

    public boolean checkIsDriverLicenceUnique(String vozackaDozvola) {
        boolean ret = true;
        Connection conn = DB.getInstance().getConn();
        String checkLicence = "select * from [Zahtev za kurira] where [Vozacka dozvola] = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkLicence);) {
            // ps.setString(1, naziv);
            ps.setString(1, vozackaDozvola);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    System.err.println("Korisnik sa vozackom dozvolom " + vozackaDozvola + " vec postoji.");
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private boolean checkIfUserExists(String korisnickoIme) {
        zm180125_UserOperations uo = new zm180125_UserOperations();
        return uo.checkIfUserExists(korisnickoIme);
    }

    private boolean checkIfUserIsCourier(String korisnickoIme) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();

        String checkLicence = "select * from [Kurir] where [Korisnicko ime] = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkLicence);) {
            ps.setString(1, korisnickoIme);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    System.err.println("Korisnik " + korisnickoIme + " je vec kurir.");
                    return true;
                } 
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean insertCourierRequest(String korisnickoIme, String vozackaDozvola) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();

        if (!checkIsDriverLicenceUnique(vozackaDozvola) || !checkIfUserExists(korisnickoIme) || checkIfUserIsCourier(korisnickoIme)) {
            return ret;
        }

        String query = "insert into [Zahtev za kurira] ([Korisnicko ime], [Vozacka dozvola]) values (?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, korisnickoIme);
            ps.setString(2, vozackaDozvola);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean deleteCourierRequest(String korisnickoIme) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();

        String query = "delete from [Zahtev za kurira] where [Korisnicko ime] = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, korisnickoIme);
            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String korisnickoIme, String vozackaDozvola) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();

        String query = "update [Zahtev za kurira] set [Vozacka dozvola] = ? where [Korisnicko ime] = ?";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, vozackaDozvola);
            ps.setString(2, korisnickoIme);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> listUsers = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select [Korisnicko ime] from [Zahtev za kurira]");) {
            while (rs.next()) {
                listUsers.add(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listUsers;
    }

    @Override
    public boolean grantRequest(String korisnickoIme) {
        boolean ret = true;
        Connection conn = DB.getInstance().getConn();

        zm180125_CourierOperations co = new zm180125_CourierOperations();
        String vozacka = "";
        String query = "Select [Vozacka dozvola] from [Zahtev za kurira] where [Korisnicko ime] = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, korisnickoIme);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    vozacka = rs.getString(1);
                } else {
                    System.err.println("Nema korisinika u bazi.");
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

        ret = deleteCourierRequest(korisnickoIme);
        if (!ret) {
            return ret;
        }

        ret = co.insertCourier(korisnickoIme, vozacka);
        return ret;

    }

}
