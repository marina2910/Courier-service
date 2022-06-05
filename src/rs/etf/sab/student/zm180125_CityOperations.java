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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author PC
 */
public class zm180125_CityOperations implements CityOperations {

    @Override
    public int insertCity(String naziv, String postanski_broj) {
         int ret = -1;
         Connection conn = DB.getInstance().getConn();
         
         //CHECK IF CITY ALREADY EXISTS
         String checkCity = "select * from Grad where [Postanski br.] = ?";
         try(PreparedStatement ps = conn.prepareStatement(checkCity);) {
           // ps.setString(1, naziv);
            ps.setString(1, postanski_broj);
            
            try (ResultSet rs = ps.executeQuery();) {
                if(rs.next()) {
                    System.err.println("Grad sa nazivom " + naziv + " i postanskim brojem " + postanski_broj + " vec postoji");
                    return ret;
                }
            } catch (SQLException ex) {
               Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         
         //INSERT NEW CITY
         String query = "insert into dbo.Grad (naziv, [Postanski br.]) values (?, ?)";
         try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){ 
            ps.setString(1, naziv);
            ps.setString(2, postanski_broj);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys();) {
                if(rs.next()) {
                    ret = rs.getInt(1);
                }
            } catch (SQLException ex) {
               Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         return ret;
    }

    @Override
    public int deleteCity(String... strings) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String query = "delete from dbo.Grad where Naziv = ?";
        String addToQuery = " or Naziv = ?";
        
        for(int i = 1; i < strings.length; i++) {
            query += addToQuery;
        }
        
        try(PreparedStatement ps = conn.prepareStatement(query);){ 
            for(int i = 0; i < strings.length; i++) {
                ps.setString(i+1, strings[i]);
            }
            ret = ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         return ret;
    }

    @Override
    public boolean deleteCity(int idG) {
        boolean ret = true;
        Connection conn = DB.getInstance().getConn();
        String query = "delete from dbo.Grad where dbo.Grad.IdG = ?";
        try(PreparedStatement ps = conn.prepareStatement(query);){ 
            ps.setInt(1, idG);
           if( ps.executeUpdate() == 0 ) 
               ret = false;                     
            
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> listCities = new ArrayList<>();
        
        Connection conn = DB.getInstance().getConn();
        try (Statement stmt = conn.createStatement(); 
            ResultSet rs = stmt.executeQuery("select IdG from Grad");
            ){
            while(rs.next()) {
                listCities.add(rs.getInt(1));            }

        } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    
        return listCities;
    }
}
