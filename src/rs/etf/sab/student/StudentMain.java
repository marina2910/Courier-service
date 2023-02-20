package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import rs.etf.sab.operations.*;
import rs.etf.sab.testing.StockroomTest;
import rs.etf.sab.testing.TestKlasa;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;
import rs.etf.sab.testing.AddressOperationsTest;

public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new zm180125_AddressOperations(); 
        CityOperations cityOperations = new zm180125_CityOperations(); 
        CourierOperations courierOperations = new zm180125_CourierOperations(); 
        CourierRequestOperation courierRequestOperation = new zm180125_CourierRequestOperation();
        DriveOperation driveOperation = new zm180125_DriveOperations();
        GeneralOperations generalOperations = new zm180125_GeneralOperations();
        PackageOperations packageOperations = new zm180125_PackageOperations();
        StockroomOperations stockroomOperations = new zm180125_StockroomOperations();
        UserOperations userOperations = new zm180125_UserOperations();
        VehicleOperations vehicleOperations = new zm180125_VehicleOperations();

        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations
        );
        zm180125_CityOperations co = new zm180125_CityOperations();
        zm180125_AddressOperations ao = new zm180125_AddressOperations();
        zm180125_StockroomOperations so = new zm180125_StockroomOperations();
        zm180125_UserOperations uo = new zm180125_UserOperations();
        zm180125_CourierRequestOperation cro = new zm180125_CourierRequestOperation();
        zm180125_CourierOperations cuo = new zm180125_CourierOperations();
        zm180125_VehicleOperations vo = new zm180125_VehicleOperations();
        zm180125_GeneralOperations go = new zm180125_GeneralOperations();
        zm180125_PackageOperations po = new zm180125_PackageOperations();
        zm180125_DriveOperations dro = new zm180125_DriveOperations();

//        go.eraseAll();
//        new TestKlasa().publicTwo();
//               
////        //INSERT ADDRESSES AND CITIES
//        int BG = co.insertCity("Belgrade", "11000");
//        int KG = co.insertCity("Kragujevac", "550000");
//        int VA = co.insertCity("Valjevo", "14000");
//        int CA = co.insertCity("Cacak", "32000");
//        int idAddressBG1 = ao.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
//        int idAddressBG2 = ao.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
//        int idAddressBG3 = ao.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
//        int idAddressBG4 = ao.insertAddress("Takovska", 7, BG, 11, 12);
//        ao.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
//        int idAddressKG1 = ao.insertAddress("Daniciceva", 1, KG, 4, 310);
//        int idAddressKG2 = ao.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
//        int idAddressVA1 = ao.insertAddress("Cika Ljubina", 8, VA, 102, 101);
//        ao.insertAddress("Karadjordjeva", 122, VA, 104, 103);
//        ao.insertAddress("Milovana Glisica", 45, VA, 101, 101);
//        int idAddressCA1 = ao.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
//        ao.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
//        //INSERT VEHICLES, STOCKROOM AND PARK IT
//        int idStockroomBG = so.insertStockroom(idAddressBG1);
//        int idStockroomVA = so.insertStockroom(idAddressVA1);
//        vo.insertVehicle("BG1675DA", 2, new BigDecimal(6.3D), new BigDecimal(4000.5D));
//        vo.insertVehicle("VA1675DA", 1, new BigDecimal(7.3D), new BigDecimal(500.5D));
//        vo.parkVehicle("BG1675DA", idStockroomBG);
//        vo.parkVehicle("VA1675DA", idStockroomVA);
//        //INSERT USERS AND COURIERS
//        String username = "crno.dete";
//        uo.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
//        String courierUsernameBG = "postarBG";
//        uo.insertUser(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2);
//        cuo.insertCourier(courierUsernameBG, "654321");
//        String courierUsernameVA = "postarVA";
//        uo.insertUser(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressBG2);
//        cuo.insertCourier(courierUsernameVA, "123456");
//        //INSERT PACKAGES
//        int type1 = 0;
//        BigDecimal weight1 = new BigDecimal(2);
//        int idPackage1 = po.insertPackage(idAddressBG1, idAddressCA1, username, type1, weight1);
//        po.acceptAnOffer(idPackage1);
//        int type2 = 0;
//        BigDecimal weight2 = new BigDecimal(4);
//        int idPackage2 = po.insertPackage(idAddressBG3, idAddressVA1, username, type2, weight2);
//        po.acceptAnOffer(idPackage2);
//        int type3 = 2;
//        BigDecimal weight3 = new BigDecimal(5);
//        int idPackage3 = po.insertPackage(idAddressBG4, idAddressKG1, username, type3, weight3);
//        po.acceptAnOffer(idPackage3);
//
//        System.out.println( dro.planingDrive("postarBG"));
  //      System.out.println( dro.getVehicle("postarVA"));

 //       System.out.println(po.getAcceptanceTime(idPackage2));

                go.eraseAll();
                TestRunner.runTests();
        
        
    }
}
