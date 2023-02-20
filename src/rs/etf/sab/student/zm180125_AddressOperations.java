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
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author PC
 */
public class zm180125_AddressOperations implements AddressOperations {

    @Override
    public int insertAddress(String ulica, int broj, int IdG, int x, int y) {
        Connection conn = DB.getInstance().getConn();
        int ret = -1;
        //CHECK IF CITY EXISTS
        zm180125_CityOperations co = new zm180125_CityOperations();
        List<Integer> allCities = co.getAllCities();
        if(!allCities.contains(IdG)) {
            System.err.println("Ovaj grad " + IdG  + " ne postoji. ");
            return -1;
        }
        
        String query = "insert into dbo.Adresa (Ulica, Broj, y, x, IdG) values (?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, ulica);
            ps.setInt(2, broj);
            ps.setInt(3, y);
            ps.setInt(4, x);
            ps.setInt(5, IdG);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys();) {
                if(rs.next()) {
                    ret = rs.getInt(1);
                }
            } catch (SQLException ex) {
               Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            //Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int deleteAddresses(String ulica, int broj) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String query = "delete from dbo.Adresa where dbo.Adresa.Ulica = ? and dbo.Adresa.Broj = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setString(1, ulica);
            ps.setInt(2, broj);
            ret = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public boolean deleteAdress(int IdA) {
        boolean ret = true;
        Connection conn = DB.getInstance().getConn();
        //CHECK IF ADDRESS EXISTS
        List<Integer> allAddresses = getAllAddresses();
        if(!allAddresses.contains(IdA)) {
            System.err.println("Greska, u bazi ne postoji adresa sa id " + IdA);
            return false;
        }
        String query = "delete from dbo.Adresa where dbo.Adresa.IdA = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, IdA);
            if (ps.executeUpdate() == 0) {
                ret = false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public int deleteAllAddressesFromCity(int IdG) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String query = "delete from dbo.Adresa where dbo.Adresa.IdG = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, IdG);
            ret = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public List<Integer> getAllAddresses() {
        List<Integer> listAddresses = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select IdA from Adresa");) {
            while (rs.next()) {
                listAddresses.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
           // Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listAddresses;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int IdG) {
        List<Integer> listAddresses = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        String query = "select IdA from Adresa where IdG = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, IdG);
            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    listAddresses.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(listAddresses.isEmpty()) listAddresses = null;
        return listAddresses;
    }

}
