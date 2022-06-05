/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

public class zm180125_CourierOperations implements CourierOperations {

    @Override
    public boolean insertCourier(String korisnickoIme, String vozackaDozvola) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();

        String query = "insert into [Kurir] ([Korisnicko ime], [Vozacka dozvola], Status, Profit, [Br. ispor. paketa]) values (?, ?, 0, 0, 0)";
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
    public boolean deleteCourier(String korisnickoIme) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();

        String query = "delete from [Kurir] where [Korisnicko ime] = ?";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, korisnickoIme);
            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        List<String> listCouriers = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        String sql = "select [Korisnicko ime] from [Kurir] where Status = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, i);
            try ( ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    listCouriers.add(rs.getString(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listCouriers;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> listCouriers = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select [Korisnicko ime] from [Kurir]");) {
            while (rs.next()) {
                listCouriers.add(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listCouriers;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numOfDel) {
        BigDecimal avgProfit = new BigDecimal(0);
        Connection con = DB.getInstance().getConn();

        switch (numOfDel) {
            //IN CASE OF -1, GET AVG FOR EVERY COURIER
            case -1:
                String query1 = "select avg(Profit) from Kurir";
                try ( Statement ps = con.createStatement();  ResultSet rs = ps.executeQuery(query1);) {
                    if (rs.next()) {
                        avgProfit = rs.getBigDecimal(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(zm180125_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            //IN CASE OF NUM OF DELIVERIES != -1, GET AVG ONLY FOR THOSE WITH THAT NUM OF DELIVERIES
            default:
                String query = "select avg(Profit) from Kurir where [Br. ispor. paketa] = ? ";
                try ( PreparedStatement ps = con.prepareStatement(query);) {
                    ps.setInt(1, numOfDel);
                    try ( ResultSet rs = ps.executeQuery();) {
                        if (rs.next()) {
                            avgProfit = rs.getBigDecimal(1);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(zm180125_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(zm180125_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
        return avgProfit;
    }

}
