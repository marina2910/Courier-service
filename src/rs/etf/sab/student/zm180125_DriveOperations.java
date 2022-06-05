package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.tests.Pair;

public class zm180125_DriveOperations implements DriveOperation {

    private static double Distanca = 0;

    private int getInitialAddress(int IdG, List<Integer> redosledUtovara, String korisnickoIme) {
        int ret = -1;
        int IdZ = -1;
        Connection conn = DB.getInstance().getConn();
        String idPokupiPaket = "select  IdZ \n"
                + "             from Utovareni\n"
                + "             where [Redni broj utovara] not in (0, -1) and \n"
                + "             [Korisnicko ime] = ?  and \n"
                + "             [Redni broj utovara] = ( \n"
                + "              select max([Redni broj utovara]) \n"
                + "              from Utovareni\n"
                + "              where [Redni broj isporuke] <> -1 and \n"
                + "               [Korisnicko ime] = ?)";

        String dohvatiAdresu = "select [Trenutna lokacija]\n"
                + "             from [Zahtev isporuke paketa]\n"
                + "             where IdZ = ?";
        try ( PreparedStatement ps = conn.prepareStatement(idPokupiPaket);  PreparedStatement ps1 = conn.prepareStatement(dohvatiAdresu);) {
            ps.setString(1, korisnickoIme);
            ps.setString(2, korisnickoIme);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    IdZ = rs.getInt(1);
                }
                ps1.setInt(1, IdZ);
                try ( ResultSet rs1 = ps1.executeQuery();) {
                    if (rs1.next()) {
                        ret = rs1.getInt(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;

    }

    private class Registracija {

        public String registracija = null;
        public int IdG = -1;
    };

    private class Vozilo {

        public BigDecimal nosivost = null;
        public BigDecimal popunjeno = null;
    }

    private boolean isUserCourier(String korisnickoIme) {
        boolean ret = false;
        //CHECK IF USER EXISTS
        zm180125_UserOperations uo = new zm180125_UserOperations();
        if (!uo.checkIfUserExists(korisnickoIme)) {
            return ret;
        }
        //CHECK IF USER IS COURIER
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "select [Korisnicko ime]\n"
                + "                from Kurir \n"
                + "                where [Korisnicko ime] = ? ";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setString(1, korisnickoIme);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    ret = true;
                } else {
                    System.err.println("Korisnik " + korisnickoIme + " nije kurir.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    public Registracija getVehicle(String korisnickoIme) {
        //CHECK IF THERE IS VEHICLE IN THE CITY WHERE DRIVER COMES FROM
        Registracija registracija = new Registracija();
        Connection conn = DB.getInstance().getConn();
        String sql = "  select Registracija, IdG\n"
                + "  from Vozilo V left join Magacin M on V.IdM = M.IdM\n"
                + "  left join Adresa A on M.IdA = A.IdA\n"
                + "  where IdG in (\n"
                + "  select IdG\n"
                + "  from Kurir Kr left join Korisnik K on Kr.[Korisnicko ime] = K.[Korisnicko ime]\n"
                + "  left join Adresa A on K.[Adresa stanovanja] = A.IdA\n"
                + "  where Kr.[Korisnicko ime] = ?\n"
                + "  )";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, korisnickoIme);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    registracija.registracija = rs.getString(1);
                    registracija.IdG = rs.getInt("IdG");
                } else {
                    System.err.println("Nema slobodnih vozila u magacinu.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return registracija;
    }

    private boolean takeVehicle(String korisnickoIme, String registracija, int IdG) {
        boolean ret = false;
        Connection conn = DB.getInstance().getConn();
        int adresaMagacina = getStockroomAddressFromCity(IdG);
        String sql = "  insert into Vozi (Registracija, [Korisnicko ime], IdA)\n"
                + "  values (?, ?, ?)\n"
                + "\n"
                + "  update Kurir set Status = 1\n"
                + "  where [Korisnicko ime] = ?\n"
                + "\n"
                + "  update Vozilo set IdM = null \n"
                + "  where Registracija = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, registracija);
            ps.setString(2, korisnickoIme);
            ps.setInt(3, adresaMagacina);
            ps.setString(4, korisnickoIme);
            ps.setString(5, registracija);

            if (ps.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private Vozilo getCapacity(String registracija) {
        Vozilo vozilo = new Vozilo();
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "select [Nosivost], [Popunjeno]\n"
                + "                from Vozilo \n"
                + "                where [Registracija] = ? ";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setString(1, registracija);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    vozilo.nosivost = rs.getBigDecimal(1);
                    vozilo.popunjeno = rs.getBigDecimal(2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return vozilo;
    }

    private BigDecimal getWeight(int IdZ) {
        BigDecimal tezina = null;

        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "select [Tezina paketa]\n"
                + "                from [Zahtev isporuke paketa] \n"
                + "                where [IdZ] = ? ";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setInt(1, IdZ);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    tezina = rs.getBigDecimal(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tezina;
    }

    private int getIdStockRoomFromCity(int IdG) {
        int IdA = -1;
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "  select IdM \n"
                + "  from Magacin M join Adresa A on M.IdA = A.IdA\n"
                + "  where A.IdG = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setInt(1, IdG);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    IdA = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdA;
    }

    private int getStockroomAddressFromCity(int IdG) {
        int IdA = -1;
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "  select A.IdA \n"
                + "  from Magacin M left join Adresa A on M.IdA = A.IdA\n"
                + "  where A.IdG = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setInt(1, IdG);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    IdA = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdA;
    }

    private boolean changePackageStatus(int IdZ, int status) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updateZahtev = "  update [Zahtev isporuke paketa] \n"
                + "  set [Status] = ? \n"
                + "  where [IdZ] = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, status);
            ps1.setInt(2, IdZ);
            if (ps1.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private boolean changeVoziAddress(int IdA, String korisnickoIme, String registracija) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updateZahtev = "  update [Vozi] \n"
                + "  set [IdA] = ? \n"
                + "  where [Korisnicko ime] = ? and Registracija = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, IdA);
            ps1.setString(2, korisnickoIme);
            ps1.setString(3, registracija);
            if (ps1.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private boolean insertPackageInVehicle(String registracija, int IdZ, int redniBrUtovara, String korisnickoIme) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updateZahtev = "  insert into [Utovareni] (Registracija, IdZ, [Redni broj isporuke], [Redni broj utovara], [Korisnicko ime]) \n"
                + "  values(?, ?, 0, ?, ?) ";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setString(1, registracija);
            ps1.setInt(2, IdZ);
            ps1.setInt(3, redniBrUtovara);
            ps1.setString(4, korisnickoIme);

            if (ps1.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private double countDistance(int IdAPocetak, int IdAKraj) {
        int xPocetak = 0, yPocetak = 0, xKraj = 0, yKraj = 0;
        Connection conn = DB.getInstance().getConn();
        String getCoord = "  select x, y \n"
                + "  from Adresa\n"
                + "  where IdA = ?";

        try ( PreparedStatement ps = conn.prepareStatement(getCoord);) {
            ps.setInt(1, IdAPocetak);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    xPocetak = rs.getInt(1);
                    yPocetak = rs.getInt(2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            ps.setInt(1, IdAKraj);
            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    xKraj = rs.getInt(1);
                    yKraj = rs.getInt(2);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        double distanca = Math.sqrt((xPocetak - xKraj) * (xPocetak - xKraj) + (yPocetak - yKraj) * (yPocetak - yKraj));
        return distanca;
    }

    private int getSenderAddress(int IdZ) {
        int IdA = -1;
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "  select [Adresa posiljaoca] \n"
                + "  from [Zahtev isporuke paketa]\n"
                + "  where IdZ = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setInt(1, IdZ);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    IdA = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdA;
    }

    private int getDeliveryAddress(int IdZ) {
        int IdA = -1;
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "  select [Adresa primaoca] \n"
                + "  from [Zahtev isporuke paketa]\n"
                + "  where IdZ = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setInt(1, IdZ);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    IdA = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdA;
    }

    private boolean setDeliveryOrder(int IdZ, int redniBroj) {
        boolean ret = false;
        Connection con = DB.getInstance().getConn();
        String updateZahtev = "  update [Utovareni] \n"
                + "  set [Redni broj isporuke] = ? \n"
                + "  where [IdZ] = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, redniBroj);
            ps1.setInt(2, IdZ);
            if (ps1.executeUpdate() > 0) {
                ret = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private int getCityForAddress(int IdA) {
        int IdG = -1;
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "  select [IdG] \n"
                + "  from [Adresa]\n"
                + "  where IdA = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setInt(1, IdA);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    IdG = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdG;
    }

    private int getMaxPickUpOrder(String korisnickoIme) {
        int order = -1;
        Connection conn = DB.getInstance().getConn();
        String checkIfUserExists = "  select max([Redni broj utovara]) \n"
                + "  from [Utovareni]\n"
                + "  where [Korisnicko ime] = ?";

        try ( PreparedStatement ps = conn.prepareStatement(checkIfUserExists);) {
            ps.setString(1, korisnickoIme);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    order = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return order;
    }

    private List<Integer> getDeliveryOrder(int IdG, List<Integer> redosledUtovara, String registracija, String korisnickoIme) {
        List<Integer> deliverPckg = new ArrayList<>();
        int pocetnaAdresa = getInitialAddress(IdG, redosledUtovara, korisnickoIme); //poslednji za utovar je pocetna adresa za dostavu
      //  System.out.println("Pocetna adresa " + pocetnaAdresa);
        if (pocetnaAdresa == -1) {
            pocetnaAdresa = getStockroomAddressFromCity(IdG);
        }
        int trenutniGrad = getCityForAddress(pocetnaAdresa);

        int ukupnoPaketa = redosledUtovara.size();
        for (int j = 0; j < ukupnoPaketa; j++) {

            HashMap<Integer, Double> distancePair = new HashMap<>();
            for (int paket : redosledUtovara) {
                int IdAKraj = getDeliveryAddress(paket);
                double distanca = countDistance(pocetnaAdresa, IdAKraj);
                distancePair.put(paket, distanca);
            }

            double min = 9999999;
            int paket = -1;
            for (int i = 0; i < redosledUtovara.size(); i++) {
                if (min > distancePair.get(redosledUtovara.get(i))) {
                    min = distancePair.get(redosledUtovara.get(i));
                    paket = redosledUtovara.get(i);
                }
            }

            deliverPckg.add(paket);
            redosledUtovara.remove(new Integer(paket));
            pocetnaAdresa = getDeliveryAddress(paket);
         //   System.out.println("Trenutno sam u gradu " + trenutniGrad + " sledeca stanica je u " + getCityForAddress(pocetnaAdresa));
            if (trenutniGrad != getCityForAddress(pocetnaAdresa)) {
          //      System.out.println("U novom gradu " + getCityForAddress(pocetnaAdresa) + " koji se razlikuje od "
//                        + trenutniGrad + " skupljam pakete koje cu odneti u magacin");
                trenutniGrad = getCityForAddress(pocetnaAdresa);
                //GET LIST OF PACKAGES TO TAKE FROM CITY TO STOCKROOM
                Vozilo v = getCapacity(registracija);
                List<Integer> paketiZaMagacin = takePackagesFromCity(trenutniGrad, v.nosivost, v.popunjeno, registracija);
                //MARK DELIVERY STATUS FOR STOCKROOM AS -1
                int nextToPickUp = getMaxPickUpOrder(korisnickoIme);
                for (int paketMagacin : paketiZaMagacin) {
                    insertPackageInVehicle(registracija, paketMagacin, ++nextToPickUp, korisnickoIme);
                    setDeliveryOrder(paketMagacin, -1);
                }
            }
        }

        //POSTAVI REDNI BROJ DOSTAVLJANJA PAKETA U UTOVARU
        int i = 1;
        for (int paket : deliverPckg) {
            setDeliveryOrder(paket, i++);
        }

        //       System.out.println(deliverPckg);
        return deliverPckg;
    }

    private List<Integer> takePackagesFromCity(int IdG, BigDecimal nosivost, BigDecimal popunjeno, String registracija) {
        zm180125_PackageOperations po = new zm180125_PackageOperations();
        List<Integer> listUndelivered = po.getAllPackagesCurrentlyAtCity(IdG);
        List<Integer> takePckg = new ArrayList<>();
        int adresaMagacina = getStockroomAddressFromCity(IdG);
//        System.out.println("SVI NEISPORUCENI PAKETI IZ BGA: " + listUndelivered);
        //DOHVATI NEISPORUCENE PAKETE IZ GRADA
        BigDecimal preostalo = nosivost.subtract(popunjeno);
        for (Integer paket : listUndelivered) {
            int deliveryStatus = po.getDeliveryStatus(paket);
            if (deliveryStatus == 3 || deliveryStatus == 4 || deliveryStatus == 0) {
                continue;
            }

            // OVDE PROVERI AKO JE PAKET U MAGACINU DA GA SKUPLJAS POSLE, A NE SAD :) 
      //      System.out.println("TRENUTNA ADRESA PAKETA " + po.getCurrentAddressForPackage(paket)
//                    + " TRENUTNA ADRESA MAGACINA " + adresaMagacina);
            if (po.getCurrentAddressForPackage(paket) == adresaMagacina) {
                continue;
            }
            if (takePckg.contains(paket)) {
                continue;
            }
            BigDecimal tezinaPaketa = getWeight(paket);
            if (preostalo.compareTo(tezinaPaketa) != -1) //moze da stane paket
            {
                preostalo.subtract(tezinaPaketa);
                takePckg.add(paket);
            }
        }
//        System.out.println("Nedostavljeni paketi iz " + IdG + ": " + takePckg);

        //AKO IMA MESTA U VOZILU UZMI PAKETE IZ MAGACINA
        if (preostalo.compareTo(new BigDecimal(0)) == 1) {
            List<Integer> preuzetiPaketi = po.getAllPackagesCurrentlyAtCity(IdG); //svi preuzeti paketi
//            System.out.println("U magacinu u " + IdG + " paketi: " + preuzetiPaketi);
            for (int paket : preuzetiPaketi) {
                int deliveryStatus = po.getDeliveryStatus(paket);
                if (deliveryStatus != 2) {
                    continue;
                }

                if (takePckg.contains(paket)) {
                    continue;
                }
                BigDecimal tezinaPaketa = getWeight(paket);
                if (preostalo.compareTo(tezinaPaketa) == -1) //ne moze da stane paket
                {
                    continue;
                } else { //dodaj paket u listu
                    preostalo.subtract(tezinaPaketa);
                    takePckg.add(paket);
                }

            }
        }
 //       System.out.println("Nedostavljeni paketi iz " + IdG + ", plus iz magacina: " + takePckg);

        //      System.out.println(takePckg);
        return takePckg;
    }

    @Override
    public boolean planingDrive(String korisnickoIme) {

        //CHECK IF USER EXISTS AND IS COURIER
        if (!isUserCourier(korisnickoIme)) {
            return false;
        }

        //GET VEHICLE FROM STOCKROOM
        Registracija reg = getVehicle(korisnickoIme);
        if (reg.registracija == null) {
            return false;
        }
        //     System.out.println("KORISNIK " + korisnickoIme + " JE UZEO VOZILO " + reg.registracija);
        //UPDATE KURIR, VOZILO AND VOZI - take vechile
        if (!takeVehicle(korisnickoIme, reg.registracija, reg.IdG)) {
            return false;
        }

        //GET LIST OF PACKAGES TO TAKE FROM CITY
        Vozilo v = getCapacity(reg.registracija);
        List<Integer> redosledUtovara = takePackagesFromCity(reg.IdG, v.nosivost, v.popunjeno, reg.registracija);

        //ADD PACKAGES IN VEHICLE AND SET ORDER TO PICK THEM UP
        int i = 1;
        for (int paket : redosledUtovara) {
            insertPackageInVehicle(reg.registracija, paket, i++, korisnickoIme);
        }

        //MAKE ORDER TO SEND PACKAGES
        List<Integer> redosledIstovara = getDeliveryOrder(reg.IdG, redosledUtovara, reg.registracija, korisnickoIme);

        return true;
    }

    private void pickUpPackage(int IdZ, int adresaStanice, int trenutnaAdresa, String korisnickoIme, int cenaGoriva, BigDecimal potrosnja) {
        Connection con = DB.getInstance().getConn();

        double distance = countDistance(trenutnaAdresa, adresaStanice);
        Distanca += distance;
        //       System.out.println("Pokupljanje distanca: " + distance);
        double potrosenoGorivo = distance * cenaGoriva;
        BigDecimal cena = potrosnja.multiply(new BigDecimal(potrosenoGorivo));

        //  System.out.println("Ubacuje cena goriva " + cenaGoriva + " potrosnja " + potrosnja);
        String updateZahtev = "  update [Utovareni] \n"
                + "  set [Redni broj utovara] = 0 \n"
                + "  where [IdZ] = ?\n"
                + "\n"
                + "update [Zahtev isporuke paketa]\n"
                + "set [Status] = 2, [Trenutna lokacija] = NULL\n"
                + "where [IdZ] = ?\n"
                + "\n"
                + "update Vozi\n"
                + "set IdA = ?\n"
                + "where [Korisnicko ime] = ?\n"
                + "\n"
                + "update Kurir\n"
                + "set Profit = Profit - ?\n"
                + "where [Korisnicko ime] = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(updateZahtev);) {
            ps1.setInt(1, IdZ);
            ps1.setInt(2, IdZ);
            ps1.setInt(3, adresaStanice);
            ps1.setString(4, korisnickoIme);
            ps1.setBigDecimal(5, cena);
            ps1.setString(6, korisnickoIme);

            //   System.out.println("Prosledjeno adresa " + adresaStanice + " korisnicko ime " + korisnickoIme + " idZ " + IdZ);
            ps1.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deliverPackage(int IdZ, int adresaStanice, int trenutnaAdresa, String korisnickoIme, int cenaGoriva, BigDecimal potrosnja) {
        zm180125_PackageOperations po = new zm180125_PackageOperations();
        BigDecimal cena = po.getPriceOfDelivery(IdZ);
        double distance = countDistance(trenutnaAdresa, adresaStanice);
        double potrosenoGorivo = distance * cenaGoriva;
        Distanca += distance;
        //       System.out.println("Dostava distanca: " + distance);
        BigDecimal potrosenoNaVoznju = potrosnja.multiply(new BigDecimal(potrosenoGorivo));
        cena = cena.subtract(potrosenoNaVoznju);
        Connection con = DB.getInstance().getConn();
        String update = " delete from Utovareni\n"
                + " where IdZ = ?\n"
                + "\n"
                + " update [Zahtev isporuke paketa]\n"
                + " set Status = 3, [Trenutna lokacija] = ?\n"
                + " where IdZ = ?\n"
                + "\n"
                + " update Vozi\n"
                + " set IdA = ?\n"
                + " where [Korisnicko ime] = ?\n"
                + "\n"
                + "  update Kurir\n"
                + "  set [Br. ispor. paketa] = [Br. ispor. paketa] + 1\n"
                + " where [Korisnicko ime] = ?\n"
                + "update Kurir\n"
                + "set Profit = Profit + ?\n"
                + "where [Korisnicko ime] = ?";

        try ( PreparedStatement ps1 = con.prepareStatement(update);) {
            ps1.setInt(1, IdZ);
            ps1.setInt(2, adresaStanice);
            ps1.setInt(3, IdZ);
            ps1.setInt(4, adresaStanice);
            ps1.setString(5, korisnickoIme);
            ps1.setString(6, korisnickoIme);
            ps1.setBigDecimal(7, cena);
            ps1.setString(8, korisnickoIme);

            ps1.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Registracija getPlateNumber(String korisnickoIme) {
        Registracija registracija = new Registracija();
        Connection conn = DB.getInstance().getConn();
        String sql = "select V.Registracija, A.IdG\n"
                + "  from Vozi V left join Korisnik K on V.[Korisnicko ime] = K.[Korisnicko ime]\n"
                + "  left join Adresa A on K.[Adresa stanovanja] = A.IdA\n"
                + "  where K.[Korisnicko ime] = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, korisnickoIme);

            try ( ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    registracija.registracija = rs.getString(1);
                    registracija.IdG = rs.getInt("IdG");
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return registracija;
    }

    private void returnToStockroom(int adresaStanice, int trenutnaAdresa, String korisnickoIme, int cenaGoriva, BigDecimal potrosnja) {
        Registracija reg = getPlateNumber(korisnickoIme);
        int adresaMagacina = getStockroomAddressFromCity(reg.IdG);
        int idMagacina = getIdStockRoomFromCity(reg.IdG);
        //      System.out.println("reg  " + reg.registracija);
        //      System.out.println("idm  " + idMagacina);
        double distance = countDistance(trenutnaAdresa, adresaStanice);
        Distanca += distance;
        //     System.out.println("Pokupljanje distanca: " + distance);
        double potrosenoGorivo = distance * cenaGoriva;
        BigDecimal cena = potrosnja.multiply(new BigDecimal(potrosenoGorivo));

        Connection conn = DB.getInstance().getConn();
        String update = "  insert into Vozio ([Korisnicko ime], [Registracija]) \n"
                + "  values (?, ?)\n"
                + "\n"
                + "  update Kurir\n"
                + "  set Status = 0\n"
                + "  where [Korisnicko ime] = ?\n"
                + "\n"
                + "  delete from Vozi\n"
                + "  where [Korisnicko ime] = ?\n"
                + "\n"
                + "  update [Zahtev isporuke paketa]\n"
                + "  set [Trenutna lokacija] = ?\n"
                + "  where IdZ in (\n"
                + "   select IdZ \n"
                + "   from Utovareni\n"
                + "   where [Redni broj isporuke] = -1\n"
                + "  )\n"
                + "  delete from Utovareni\n"
                + "  where [Korisnicko ime] = ?\n"
                + "\n"
                + "update Kurir\n"
                + "set Profit = Profit - ?\n"
                + "where [Korisnicko ime] = ?";

        try ( PreparedStatement ps1 = conn.prepareStatement(update);) {
            ps1.setString(1, korisnickoIme);
            ps1.setString(2, reg.registracija);
            ps1.setString(3, korisnickoIme);
            ps1.setString(4, korisnickoIme);
            ps1.setInt(5, adresaMagacina);
            ps1.setString(6, korisnickoIme);
            ps1.setBigDecimal(7, cena);
            ps1.setString(8, korisnickoIme);
            ps1.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(zm180125_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        zm180125_VehicleOperations vo = new zm180125_VehicleOperations();
        vo.parkVehicle(reg.registracija, idMagacina);

    }

    private int getFuelPrice(int tipGoriva) {
        switch (tipGoriva) {
            case 0:
                return 15;
            case 1:
                return 32;
            case 2:
                return 36;
        }
        return 1;
    }

    ;

    @Override
    public int nextStop(String korisnickoIme) {
        int adresaStanice = -1;
        int paketZaPokupljanje = -1;
        int paketZaIsporuku = -1;
        int ret = -2;
        int trenutnaAdresa = -1;
        int tipGoriva = -1;
        Registracija vozilo = getPlateNumber(korisnickoIme);
        int adresaMagacina = getStockroomAddressFromCity(vozilo.IdG);
        BigDecimal potrosnja = null;

        Connection conn = DB.getInstance().getConn();
        String dohvatiIzVozila = "  select Vz.[Tip goriva], Vz.Potrosnja\n"
                + "  from Vozi V join Vozilo Vz on V.Registracija = Vz.Registracija\n"
                + "  where V.[Korisnicko ime] = ?";

        String trAdresa = "Select IdA from Vozi where [Korisnicko ime] = ?";

        String idPokupiPaket = "select  IdZ \n"
                + "             from Utovareni\n"
                + "             where [Redni broj utovara] not in (0, -1) and \n"
                + "             [Korisnicko ime] = ?  and \n"
                + "             [Redni broj utovara] = ( \n"
                + "              select min([Redni broj utovara]) \n"
                + "              from Utovareni\n"
                + "              where [Redni broj utovara] not in (0, -1) and \n"
                + "               [Korisnicko ime] = ?) ";

        String pokupiPaket = "  select [Trenutna lokacija]\n"
                + "  from [Zahtev isporuke paketa]\n"
                + "  where IdZ = ?\n"
                + "and (\n"
                + "select IdG \n"
                + "from [Zahtev isporuke paketa] Z left join Adresa A on Z.[Trenutna lokacija] = A.IdA\n"
                + "where IdZ = ? \n"
                + ") = \n"
                + "( select IdG \n"
                + "from Vozi V left join Adresa A on V.IdA = A.IdA\n"
                + "where [Korisnicko ime] = ? \n"
                + ")";

        String pokupiIzMagacina = "  Select IdZ\n"
                + "  from [Zahtev isporuke paketa]\n"
                + "  where [Trenutna lokacija] = ?";

        String dostaviPaket = " select [Adresa primaoca], IdZ\n"
                + "  from [Zahtev isporuke paketa]\n"
                + "  where IdZ = (\n"
                + "  select  IdZ \n"
                + "  from Utovareni\n"
                + "  where [Redni broj isporuke] not in (0, -1) and \n"
                + "  [Korisnicko ime] = ?  and \n"
                + "  [Redni broj isporuke] = ( \n"
                + "  select min([Redni broj isporuke]) \n"
                + "	from Utovareni\n"
                + "	where [Redni broj isporuke] not in (0, -1) and \n"
                + "	[Korisnicko ime] = ?) )";

        try ( PreparedStatement ps = conn.prepareStatement(idPokupiPaket);  PreparedStatement ps1 = conn.prepareStatement(pokupiPaket);  PreparedStatement ps2 = conn.prepareStatement(dostaviPaket);  PreparedStatement ps3 = conn.prepareStatement(trAdresa);  PreparedStatement ps4 = conn.prepareStatement(dohvatiIzVozila);  PreparedStatement ps5 = conn.prepareStatement(pokupiIzMagacina);) {
            ps.setString(1, korisnickoIme);
            ps.setString(2, korisnickoIme);

            ps5.setInt(1, adresaMagacina);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    paketZaPokupljanje = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            ps1.setInt(1, paketZaPokupljanje);
            ps1.setInt(2, paketZaPokupljanje);
            ps1.setString(3, korisnickoIme);

            ps2.setString(1, korisnickoIme);
            ps2.setString(2, korisnickoIme);

            ps3.setString(1, korisnickoIme);

            ps4.setString(1, korisnickoIme);
            try ( ResultSet rs1 = ps1.executeQuery();  ResultSet rs2 = ps2.executeQuery();  ResultSet rs3 = ps3.executeQuery();  ResultSet rs4 = ps4.executeQuery();  ResultSet rs5 = ps5.executeQuery();) {

                if (rs4.next()) {
                    tipGoriva = rs4.getInt(1);
                    potrosnja = rs4.getBigDecimal(2);
                }

                if (rs3.next()) {
                    trenutnaAdresa = rs3.getInt(1);
                }

                if (rs1.next()) { //PROVERI DA LI IMA PAKETA ZA SKUPLJANJE SA ADRESA POSILJAOCA
                    adresaStanice = rs1.getInt(1);
                    pickUpPackage(paketZaPokupljanje, adresaStanice, trenutnaAdresa, korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
                    if (adresaStanice == adresaMagacina && rs5.next()) {//PROVERI DA LI IMA NESTO U MAGACINU
                        paketZaIsporuku = rs5.getInt(1);
                        pickUpPackage(paketZaIsporuku, adresaStanice, trenutnaAdresa, korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
                        while (rs5.next()) {
                            paketZaIsporuku = rs5.getInt(1);
                            pickUpPackage(paketZaIsporuku, adresaStanice, trenutnaAdresa, korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
                        }

                    }
//                else if (rs5.next()) {//PROVERI DA LI IMA NESTO U MAGACINU
//                    adresaStanice = adresaMagacina;
//                    paketZaIsporuku = rs5.getInt(1);
//                    pickUpPackage(paketZaIsporuku, adresaStanice, trenutnaAdresa, korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
//                    while (rs5.next()) {
//                        paketZaIsporuku = rs5.getInt(1);
//                        pickUpPackage(paketZaIsporuku, adresaStanice, trenutnaAdresa, korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
//                    }
                } else if (rs2.next()) { //PROVERI DA LI IMA PAKETA ZA ISPORUKU
                    adresaStanice = rs2.getInt(1);
                    paketZaIsporuku = rs2.getInt(2);
                    deliverPackage(paketZaIsporuku, adresaStanice, trenutnaAdresa, korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
                    ret = paketZaIsporuku;
                } else { //VRATITI SE U MAGACIN
                    //             System.out.println("DISTANCA JE: " + Distanca);
                    returnToStockroom(trenutnaAdresa, getStockroomAddressFromCity(vozilo.IdG), korisnickoIme, getFuelPrice(tipGoriva), potrosnja);
                    ret = -1;
                }

            } catch (SQLException ex) {
                Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(zm180125_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String korisnickoIme
    ) {

        List<Integer> listPckg = new ArrayList<>();
        Connection conn = DB.getInstance().getConn();
        String query = "select U.IdZ\n"
                + "from Utovareni U left join [Zahtev isporuke paketa] Z on Z.IdZ = U.IdZ \n"
                + "where U.Registracija = (\n"
                + "select Registracija \n"
                + "from Vozi \n"
                + "where [Korisnicko ime] = ?\n"
                + ") and U.[Redni broj utovara]= 0";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, korisnickoIme);
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

}
