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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author PC
 */
public class zm180125_VehicleOperations implements VehicleOperations {

    @Override
    public boolean insertVehicle(String registracija, int tipGoriva, BigDecimal potrosnja, BigDecimal kapacitet) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        if (tipGoriva < 0 || tipGoriva > 2) {
            System.err.println("Prosledjeni tip goriva mora biti 0, 1 ili 2");
            return false;
        }
        String query = "insert into Vozilo ([Registracija], [Tip goriva], [Potrosnja], [Nosivost], [Popunjeno])\n"
                + " values (?, ?, ?, ?, 0) ";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, registracija);
            ps.setInt(2, tipGoriva);
            ps.setBigDecimal(3, potrosnja);
            ps.setBigDecimal(4, kapacitet);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
        //    Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public int deleteVehicles(String... strings) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String query = "delete from dbo.Vozilo where Registracija = ?";
        String addToQuery = " or Registracija = ?";

        for (int i = 1; i < strings.length; i++) {
            query += addToQuery;
        }

        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            for (int i = 0; i < strings.length; i++) {
                ps.setString(i + 1, strings[i]);
            }
            ret = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> listVehicles = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select Registracija from Vozilo");) {
            while (rs.next()) {
                listVehicles.add(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listVehicles;
    }

    @Override
    public boolean changeFuelType(String registracija, int tipGoriva) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String query = "update Vozilo set [Tip goriva] = ? where Registracija = ? and IdM is not null";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setInt(1, tipGoriva);
            ps.setString(2, registracija);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean changeConsumption(String registracija, BigDecimal potrosnja) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String query = "update Vozilo set [Potrosnja] = ? where Registracija = ? and IdM is not null";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setBigDecimal(1, potrosnja);
            ps.setString(2, registracija);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean changeCapacity(String registracija, BigDecimal nosivost) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String query = "update Vozilo set [Nosivost] = ? where Registracija = ? and IdM is not null";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setBigDecimal(1, nosivost);
            ps.setString(2, registracija);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean parkVehicle(String registracija, int IdM) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        //CHECK IF VEHICLE IS DRIVING
        
        
        String query = "  update Vozilo set [IdM] = ?, [Popunjeno] = 0\n" +
                        "  where Registracija = ?  and\n" +
                        "  Registracija not in \n" +
                        "  (select Registracija \n" +
                        "  from Vozi where [Registracija] = ?)";
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            ps.setInt(1, IdM);
            ps.setString(2, registracija);
            ps.setString(3, registracija);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
         //   Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

}
