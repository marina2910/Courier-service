package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author PC
 */
public class zm180125_GeneralOperations implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConn();
        String deleteFrom = "delete from [Administrator]\n"
                + "delete from Utovareni\n"
                + "delete from Vozi\n"
                + "delete from Vozio\n"
                + "delete from Kurir\n"
                + "delete from [Zahtev za kurira]\n"
                + "delete from Ponuda\n"
                + "delete from [Zahtev isporuke paketa]\n"
                + "delete from Vozilo\n"
                + "delete from Magacin\n"
                + "delete from Korisnik\n"
                + "delete from Adresa\n"
                + "delete from Grad\n";

        try ( Statement stmt = conn.createStatement();) {
            stmt.executeUpdate(deleteFrom);

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
