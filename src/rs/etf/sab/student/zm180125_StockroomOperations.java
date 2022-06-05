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
import rs.etf.sab.operations.StockroomOperations;

public class zm180125_StockroomOperations implements StockroomOperations {

    @Override
    public int insertStockroom(int IdA) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();

        //CHECK IF THAT CITY ALREADY HAS STOCKROOM
        String checkCity = " select *\n"
                + " from Adresa A\n"
                + " where A.IdG in (\n"
                + "	select IdG from Magacin join Adresa on Magacin.IdA = Adresa.IdA\n"
                + "	) and A.IdA = ?";
        try ( PreparedStatement ps = conn.prepareStatement(checkCity);) {
            ps.setInt(1, IdA);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    System.err.println("U ovom gradu vec postoji magacin.");
                    return ret;
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        //INSERT NEW STOCKROOM IF CITY DOES NOT HAVE ONE
        String query = "insert into Magacin (idA) values ( ? )";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, IdA);
            ps.executeUpdate();
            try ( ResultSet rs = ps.getGeneratedKeys();) {
                if (rs.next()) {
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
    public boolean deleteStockroom(int IdM) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();
        String query = " delete from Magacin\n"
                + "  where IdM not in (select IdM from Vozilo where IdM = ?)\n"
                + "  and IdM = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setInt(1, IdM);
            ps.setInt(2, IdM);
            if (ps.executeUpdate() > 0) {
                ret = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public int deleteStockroomFromCity(int IdG) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String deleteIt = "  delete from Magacin\n"
                + "  where IdM not in (select IdM from Vozilo)\n"
                + "  and ( \n"
                + "  Select IdG \n"
                + "  from Magacin M join Adresa A on M.IdA = A.IdA\n"
                + "  where A.IdG = ?\n"
                + "  ) is not null";
        String getIt = "  select IdM from Magacin\n"
                + "  where IdM not in (select IdM from Vozilo)\n"
                + "  and ( \n"
                + "  Select IdG \n"
                + "  from Magacin M join Adresa A on M.IdA = A.IdA\n"
                + "  where A.IdG = ?\n"
                + "  ) is not null";
        try ( PreparedStatement ps = conn.prepareStatement(getIt);
                PreparedStatement ps1 = conn.prepareStatement(deleteIt);) {
            ps.setInt(1, IdG);
            ps1.setInt(1, IdG);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
                ps1.executeUpdate();
               
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public List<Integer> getAllStockrooms() {
        List<Integer> listStockrooms = new ArrayList<>();
        Connection conn = DB.getInstance().getConn();

        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select IdM from Magacin");) {
            while (rs.next()) {
                listStockrooms.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listStockrooms;
    }

}
