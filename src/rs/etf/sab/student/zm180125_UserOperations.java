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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author PC
 */
public class zm180125_UserOperations implements UserOperations {

    @Override
    public boolean insertUser(String korisnickoIme, String ime, String prezime, String lozinka, int idA) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();

        //CHECK UPPERCASE FIRST LETTER
        if (!Character.isUpperCase(ime.charAt(0))) {
            System.err.println("Prvo slovo imena je malo");
            return ret;
        }
        if (!Character.isUpperCase(prezime.charAt(0))) {
            System.err.println("Prvo slovo prezimena je malo");
            return ret;
        }

        //CHECK IF USERNAME IS UNIQUE
        // TO DO: proveri da li ovo moze da se resi u tsqlu (a sigurno moze nekim constraintom)
        //CHECK IF PASSWORD IS OK
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',_?/*~$^+=<>]).{8,}$");
        Matcher matcher = pattern.matcher(lozinka);
        if (!matcher.matches()) {
            System.err.println("Lozinka mora imati jedno malo, jedno veliko slovo, jedan broj i jedan specijalni karakter.");
            return ret;
        }

        //INSERT IF EVERYTHING IS OK
        String query = "insert into Korisnik (Ime, Prezime, [Korisnicko ime], Sifra, [Adresa stanovanja]) values \n"
                + "(?, ?, ?, ?, ?)";

        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ime);
            ps.setString(2, prezime);
            ps.setString(3, korisnickoIme);
            ps.setString(4, lozinka);
            ps.setInt(5, idA);

            if (ps.executeUpdate() != 0) //numbers of rows executed
            {
                ret = true;
            }

        } catch (SQLException ex) {
      //      Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public boolean declareAdmin(String korisnickoIme) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();

        //CHECK IF USER EXISTS AND IS ALREADY AN ADMIN
        String checkIfAdmin = "  select [Korisnicko ime]\n"
                + "  from Administrator\n"
                + "  where [Korisnicko ime] in (select [Korisnicko ime]\n"
                + "			      from Korisnik\n"
                + "                             where [Korisnicko ime] = ?)";

        String checkIfUserExists = "select [Korisnicko ime]\n"
                + "                from Korisnik \n"
                + "                where [Korisnicko ime] = ? ";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfAdmin);  PreparedStatement ps1 = conn.prepareStatement(checkIfUserExists);) {

            ps1.setString(1, korisnickoIme);
            ps.setString(1, korisnickoIme);

            try ( ResultSet rs1 = ps1.executeQuery();  ResultSet rs = ps.executeQuery();) {
                if (rs1.next()) {
                    //
                } else {
                    System.err.println("Korisnik " + korisnickoIme + " nije u bazi.");
                    return ret;
                }
                if (rs.next()) {
                    System.err.println("Korisnik " + korisnickoIme + " je vec admin");
                    return ret;
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        //DECLARE USER AS ADMIN 
        String insert = "insert into Administrator ([Korisnicko ime]) values ( '" + korisnickoIme + "' )";
        try ( Statement stmt = conn.createStatement();) {
            if (stmt.executeUpdate(insert) > 0) {
                ret = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public int getSentPackages(String... strings) {
        Connection con = DB.getInstance().getConn();
        if (strings.length == 0) {
            System.err.println("Nije prosledjeno nijedno korisnicko ime.");
            return -1;
        }
        if (!checkIfUserExists(strings[0])) {
            System.err.println("Korisnik " + strings[0] + " nije u bazi.");
            return -1;
        }
        String query = " select sum([Br. poslatih paketa]) \n"
                + " from Korisnik where  [Korisnicko ime]  = ?";
        String addToQuery = " or [Korisnicko ime] = ?";
        for (int i = 1; i < strings.length; i++) {
            if (!checkIfUserExists(strings[i])) {
                System.err.println("Korisnik " + strings[i] + " nije u bazi.");
                return -1;
            } else {
                query += addToQuery;
            }
        }

        int ret = -1;
        try ( PreparedStatement ps = con.prepareStatement(query);) {
            for (int i = 0; i < strings.length; i++) {
                ps.setString(i + 1, strings[i]);
            }
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public int deleteUsers(String... strings) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String query = "delete from dbo.Korisnik where [Korisnicko ime] = ?";
        String addToQuery = " or [Korisnicko ime] = ?";

        for (int i = 1; i < strings.length; i++) {
            query += addToQuery;
        }

        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            for (int i = 0; i < strings.length; i++) {
                ps.setString(i + 1, strings[i]);
            }
            ret = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public List<String> getAllUsers() {
        List<String> listUsers = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select [Korisnicko ime] from Korisnik");) {
            while (rs.next()) {
                listUsers.add(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listUsers;
    }

    public boolean checkIfUserExists(String username) {
        Connection conn = DB.getInstance().getConn();
        boolean ret = false;
        //CHECK IF USER EXISTS
        String checkIfUserExists = "select [Korisnicko ime]\n"
                + "                from Korisnik \n"
                + "                where [Korisnicko ime] = ? ";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {

            ps.setString(1, username);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = true;
                } else {
                    System.err.println("Korisnik " + username + " nije u bazi.");

                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }
}
