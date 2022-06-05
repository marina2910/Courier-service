/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author PC
 */
public class zm180125_PackageOperations implements PackageOperations {

    @Override
    public int insertPackage(int IdAPosiljalac, int IdAPrimalac, String korisnickoIme, int tip, BigDecimal tezina) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String query = "insert into dbo.[Zahtev isporuke paketa] "
                + "(Status, [Adresa posiljaoca], [Adresa primaoca], [Posiljaoc], [Tip paketa], [Tezina paketa], [Vreme kreiranja zahteva], [Trenutna lokacija])"
                + " values (0, ?, ?, ?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, IdAPosiljalac);
            ps.setInt(2, IdAPrimalac);
            ps.setString(3, korisnickoIme);
            ps.setInt(4, tip);
            ps.setBigDecimal(5, tezina);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            ps.setString(6, dtf.format(now));
            ps.setInt(7, IdAPosiljalac);
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
    public boolean acceptAnOffer(int IdZ) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updatePonuda = "update Ponuda set [Status] = 1 where IdZ = ? and\n"
                + "(select Status\n"
                + "  from [Zahtev isporuke paketa] Z\n"
                + "  where Z.IdZ = ? ) = 0";

        String updateZahtev = "update [Zahtev isporuke paketa] set [Status] = 1, [Vreme prihvatanja ponude] = ? where IdZ = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updatePonuda);  PreparedStatement ps2 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, IdZ);
            ps1.setInt(2, IdZ);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            ps2.setString(1, dtf.format(now));
            ps2.setInt(2, IdZ);
            
            if (ps1.executeUpdate() > 0) {
                if (ps2.executeUpdate() > 0) {
                    ret = true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean rejectAnOffer(int IdZ) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updatePonuda = "update Ponuda set [Status] = 0 where IdZ = ? and\n"
                + "(select Status\n"
                + "  from [Zahtev isporuke paketa] Z\n"
                + "  where Z.IdZ = ? ) = 0";

        String updateZahtev = "update [Zahtev isporuke paketa] set [Status] = 4";

        try ( PreparedStatement ps1 = con.prepareStatement(updatePonuda);  PreparedStatement ps2 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, IdZ);
            ps1.setInt(2, IdZ);

            if (ps1.executeUpdate() > 0) {
                if (ps2.executeUpdate() > 0) {
                    ret = true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> listPckg = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery("select IdZ from [Zahtev isporuke paketa]");) {
            while (rs.next()) {
                listPckg.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listPckg;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int tip) {
        List<Integer> listPckg = new ArrayList<>();

        Connection conn = DB.getInstance().getConn();
        String query = "select IdZ from [Zahtev isporuke paketa] where [Tip paketa] = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, tip);
            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    listPckg.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listPckg;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        List<Integer> listPckg = new ArrayList<>();
        Connection conn = DB.getInstance().getConn();
        String query = "select IdZ from [Zahtev isporuke paketa] where [Status] <> 3";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {

            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    listPckg.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listPckg;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int IdG) {
        List<Integer> listPckg = new ArrayList<>();
        Connection conn = DB.getInstance().getConn();
        String query = "  select IdZ \n"
                + "  from [Zahtev isporuke paketa] Z join Adresa A on Z.[Adresa posiljaoca] = A.IdA\n"
                + "  where [Status] not in (0, 3, 4) and A.IdG = ?\n"
                + "order by IdZ DESC";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, IdG);
            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    listPckg.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listPckg;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int IdG) {
        List<Integer> listPckg = new ArrayList<>();
        Connection conn = DB.getInstance().getConn();
        String query = "  select IdZ \n"
                + "  from [Zahtev isporuke paketa] Z join Adresa A on Z.[Trenutna lokacija] = A.IdA\n"
                + "  where A.IdG = ?\n"
                + "  order by [Vreme kreiranja zahteva] asc";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, IdG);
            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    listPckg.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listPckg;
    }

    @Override
    public boolean deletePackage(int IdZ) {
        boolean ret = true;
        Connection conn = DB.getInstance().getConn();
        String query = "  delete from Ponuda\n"
                + "  where \n"
                + "  (select P.IdZ \n"
                + "  from [Ponuda] P join [Zahtev isporuke paketa] Z on Z.IdZ = P.IdZ\n"
                + "  where Z.IdZ = ? and Z.Status in(0, 4) ) = Ponuda.IdZ\n"
                + "\n"
                + "  delete from dbo.[Zahtev isporuke paketa] \n"
                + "  where [Zahtev isporuke paketa].IdZ = ?\n"
                + "  and [Zahtev isporuke paketa].Status in (0, 4)";
        try ( PreparedStatement ps = conn.prepareStatement(query);) {
            ps.setInt(1, IdZ);
            ps.setInt(2, IdZ);

            if (ps.executeUpdate() == 0) {
                ret = false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean changeWeight(int IdZ, BigDecimal weight) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updateZahtev = "  update [Zahtev isporuke paketa] \n"
                + "  set [Tezina paketa] = ? \n"
                + "  where [Status] = 0 and [IdZ] = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setBigDecimal(1, weight);
            ps1.setInt(2, IdZ);
            if (ps1.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean changeType(int IdZ, int tip) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updateZahtev = "  update [Zahtev isporuke paketa] \n"
                + "  set [Tip paketa] = ? \n"
                + "  where [Status] = 0 and [IdZ] = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, tip);
            ps1.setInt(2, IdZ);
            if (ps1.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getDeliveryStatus(int IdZ) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
        String sql = "select [Status] from [Zahtev isporuke paketa] where IdZ = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, IdZ);
            try ( ResultSet rs = ps.executeQuery();) {
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
    public BigDecimal getPriceOfDelivery(int IdZ) {
        BigDecimal ret = new BigDecimal(-1);
        Connection conn = DB.getInstance().getConn();
        String sql = "select [Cena] from [Ponuda] where IdZ = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, IdZ);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getBigDecimal(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    private boolean checkIfUtovaren(int IdZ) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();
        
        String sql = "  Select Z.IdZ\n"
                + "  from Utovareni U left join [Zahtev isporuke paketa] Z on U.IdZ = Z.IdZ\n"
                + "  where Z.IdZ = ? and Status = 2";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, IdZ);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = true;
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
    public int getCurrentLocationOfPackage(int IdZ) {
        int ret = -1;
        Connection conn = DB.getInstance().getConn();
      
        String sql = "  Select IdG\n"
                + "  from [Zahtev isporuke paketa] Z join Adresa A on Z.[Trenutna lokacija] = A.IdA\n"
                + "  where IdZ = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, IdZ);
            try ( ResultSet rs = ps.executeQuery();) {
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
    public Date getAcceptanceTime(int IdZ) {
        Date ret = null;
        Connection conn = DB.getInstance().getConn();
        String sql = "select [Vreme prihvatanja ponude] \n"
                + "from [Zahtev isporuke paketa]\n"
                + "where IdZ = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, IdZ);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = rs.getDate(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public int getCurrentAddressForPackage(int IdZ) {
         int ret = -1;
        Connection conn = DB.getInstance().getConn();
      
        String sql = "  Select [Trenutna lokacija]\n"
                + "  from [Zahtev isporuke paketa] \n"
                + "  where IdZ = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, IdZ);
            try ( ResultSet rs = ps.executeQuery();) {
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

}
