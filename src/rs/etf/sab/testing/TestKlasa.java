/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.testing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import rs.etf.sab.operations.*;
import rs.etf.sab.student.*;
import rs.etf.sab.tests.Pair;

/**
 *
 * @author PC
 */
public class TestKlasa {

    AddressOperations addressOperations = new zm180125_AddressOperations();
    CityOperations cityOperations = new zm180125_CityOperations();
    CourierOperations courierOperation = new zm180125_CourierOperations();
    CourierRequestOperation courierRequestOperation = new zm180125_CourierRequestOperation();
    DriveOperation driveOperation = new zm180125_DriveOperations();
    GeneralOperations generalOperations = new zm180125_GeneralOperations();
    PackageOperations packageOperations = new zm180125_PackageOperations();
    StockroomOperations stockroomOperations = new zm180125_StockroomOperations();
    UserOperations userOperations = new zm180125_UserOperations();
    VehicleOperations vehicleOperations = new zm180125_VehicleOperations();

    Map<Integer, Pair<Integer, Integer>> addressesCoords;
    Map<Integer, BigDecimal> packagePrice;

    public TestKlasa() {
        this.addressesCoords = new HashMap<Integer, Pair<Integer, Integer>>();
        this.packagePrice = new HashMap<Integer, BigDecimal>();
    }

    public void publicOne() {
        final int BG = this.insertCity("Belgrade", "11000");
        final int KG = this.insertCity("Kragujevac", "550000");
        final int VA = this.insertCity("Valjevo", "14000");
        final int CA = this.insertCity("Cacak", "32000");
        final int idAddressBG1 = this.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        final int idAddressBG2 = this.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        final int idAddressBG3 = this.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        final int idAddressBG4 = this.insertAddress("Takovska", 7, BG, 11, 12);
        final int idAddressBG5 = this.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        final int idAddressKG1 = this.insertAddress("Daniciceva", 1, KG, 4, 310);
        final int idAddressKG2 = this.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        final int idAddressVA1 = this.insertAddress("Cika Ljubina", 8, VA, 102, 101);
        final int idAddressVA2 = this.insertAddress("Karadjordjeva", 122, VA, 104, 103);
        final int idAddressVA3 = this.insertAddress("Milovana Glisica", 45, VA, 101, 101);
        final int idAddressCA1 = this.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        final int idAddressCA2 = this.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
        final int idStockroomBG = this.insertStockroom(idAddressBG1);
        final int idStockroomVA = this.insertStockroom(idAddressVA1);
        this.insertAndParkVehicle("BG1675DA", new BigDecimal(6.3), new BigDecimal(1000.5), 2, idStockroomBG);
        this.insertAndParkVehicle("VA1675DA", new BigDecimal(7.3), new BigDecimal(500.5), 1, idStockroomVA);
        final String username = "crno.dete";
        this.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
        final String courierUsernameBG = "postarBG";
        this.insertCourier(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2, "654321");
        final String courierUsernameVA = "postarVA";
        this.insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressBG2, "123456");
        final int type1 = 0;
        final BigDecimal weight1 = new BigDecimal(2);
        final int idPackage1 = this.insertAndAcceptPackage(idAddressBG2, idAddressCA1, username, type1, weight1);
        final int type2 = 1;
        final BigDecimal weight2 = new BigDecimal(4);
        final int idPackage2 = this.insertAndAcceptPackage(idAddressBG3, idAddressVA1, username, type2, weight2);
        final int type3 = 2;
        final BigDecimal weight3 = new BigDecimal(5);
        final int idPackage3 = this.insertAndAcceptPackage(idAddressBG4, idAddressKG1, username, type3, weight3);
        Assert.assertEquals(0L, (long) this.courierOperation.getCouriersWithStatus(1).size());
        this.driveOperation.planingDrive(courierUsernameBG);
        Assert.assertTrue(this.courierOperation.getCouriersWithStatus(1).contains(courierUsernameBG));
        final int type4 = 3;
        final BigDecimal weight4 = new BigDecimal(2);
        final int idPackage4 = this.insertAndAcceptPackage(idAddressBG2, idAddressKG2, username, type4, weight4);
        Assert.assertEquals(4L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(3L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(1L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(2L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertEquals(3L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals((long) idPackage2, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals((long) idPackage1, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertEquals(-1L, (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(CA).size());
        Assert.assertEquals(1L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals((long) idPackage3, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage1));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage2));
        Assert.assertTrue(-1L != (long) this.packageOperations.getCurrentLocationOfPackage(idPackage3));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(KG).size());
        Assert.assertEquals(0L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllUndeliveredPackages().size());
        Assert.assertTrue(this.packageOperations.getAllUndeliveredPackages().contains(idPackage4));
        Assert.assertEquals(2L, (long) this.courierOperation.getCouriersWithStatus(0).size());
        final double distance = Util.getDistance(new Pair[]{this.addressesCoords.get(idAddressBG1), this.addressesCoords.get(idAddressBG2), this.addressesCoords.get(idAddressBG3), this.addressesCoords.get(idAddressBG4), this.addressesCoords.get(idAddressVA1), this.addressesCoords.get(idAddressCA1), this.addressesCoords.get(idAddressKG1), this.addressesCoords.get(idAddressBG1)});
        BigDecimal profit = this.packagePrice.get(idPackage1).add(this.packagePrice.get(idPackage2)).add(this.packagePrice.get(idPackage3));

//        System.out.println("Izracunata distanca je : " + distance);
//        System.out.println(new BigDecimal(36).multiply(new BigDecimal(6.3)).multiply(new BigDecimal(distance)));
        profit = profit.subtract(new BigDecimal(36).multiply(new BigDecimal(6.3)).multiply(new BigDecimal(distance)));
//        System.out.println("ukupan profit je " + profit + "-----" + this.courierOperation.getAverageCourierProfit(3));
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(1.05))) < 0);
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(0.95))) > 0);
    }

    public void publicTwo() {
        final int BG = this.insertCity("Belgrade", "11000");
        final int KG = this.insertCity("Kragujevac", "550000");
        final int VA = this.insertCity("Valjevo", "14000");
        final int CA = this.insertCity("Cacak", "32000");
        final int idAddressBG1 = this.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        final int idAddressBG2 = this.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        final int idAddressBG3 = this.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        final int idAddressBG4 = this.insertAddress("Takovska", 7, BG, 11, 12);
        final int idAddressBG5 = this.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        final int idAddressKG1 = this.insertAddress("Daniciceva", 1, KG, 4, 310);
        final int idAddressKG2 = this.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        final int idAddressVA1 = this.insertAddress("Cika Ljubina", 8, VA, 102, 101);
        final int idAddressVA2 = this.insertAddress("Karadjordjeva", 122, VA, 104, 103);
        final int idAddressVA3 = this.insertAddress("Milovana Glisica", 45, VA, 101, 101);
        final int idAddressCA1 = this.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        final int idAddressCA2 = this.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
        final int idStockroomBG = this.insertStockroom(idAddressBG1);
        final int idStockroomVA = this.insertStockroom(idAddressVA1);
        this.insertAndParkVehicle("BG1675DA", new BigDecimal(6.3), new BigDecimal(1000.5), 2, idStockroomBG);
        this.insertAndParkVehicle("VA1675DA", new BigDecimal(7.3), new BigDecimal(500.5), 1, idStockroomVA);
        final String username = "crno.dete";
        this.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
        final String courierUsernameBG = "postarBG";
        this.insertCourier(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2, "654321");
        final String courierUsernameVA = "postarVA";
        this.insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressVA2, "123456");
        final int type = 1;
        final BigDecimal weight = new BigDecimal(4);
        final int idPackage1 = this.insertAndAcceptPackage(idAddressBG2, idAddressKG1, username, type, weight);
        final int idPackage2 = this.insertAndAcceptPackage(idAddressKG2, idAddressBG4, username, type, weight);
        final int idPackage3 = this.insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        final int idPackage4 = this.insertAndAcceptPackage(idAddressCA2, idAddressBG4, username, type, weight);
        Assert.assertEquals(0L, (long) this.courierOperation.getCouriersWithStatus(1).size());
        this.driveOperation.planingDrive(courierUsernameBG);
        this.driveOperation.planingDrive(courierUsernameVA);
        Assert.assertEquals(2L, (long) this.courierOperation.getCouriersWithStatus(1).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals((long) idPackage1, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage1));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals((long) idPackage3, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage3));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(KG).contains(idPackage1));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameVA));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(CA).contains(idPackage3));

        final int idPackage5 = this.insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        final int idPackage6 = this.insertAndAcceptPackage(idAddressBG3, idAddressVA3, username, type, weight);
        System.out.println("PLANIRANJE ========================");
        this.driveOperation.planingDrive(courierUsernameBG);
//        int a = 2;
//        if(a == 2) return;
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage6));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertFalse(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage6));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage2));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage6));
        Assert.assertEquals((long) idPackage2, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage2));
        Assert.assertEquals((long) idPackage6, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage6));
        Assert.assertEquals(0L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage5));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage4));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals(1L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(idPackage6));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(0L, (long) this.packageOperations.getAllUndeliveredPackagesFromCity(BG).size());
        Assert.assertEquals(3L, (long) this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage2));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage4));
        Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(idPackage5));
        this.driveOperation.planingDrive(courierUsernameBG);
        Assert.assertEquals(0L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//        int a = 2;
//        if(2 == a) return;
        Assert.assertEquals(-2L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(2L, (long) this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage4));
        Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(idPackage5));
        Assert.assertEquals((long) idPackage4, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage4));
        Assert.assertEquals((long) idPackage5, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(3L, (long) this.packageOperations.getDeliveryStatus(idPackage5));
        Assert.assertEquals(-1L, (long) this.driveOperation.nextStop(courierUsernameBG));
        Assert.assertEquals(0L, (long) this.packageOperations.getAllUndeliveredPackages().size());
        Assert.assertEquals(2L, (long) this.courierOperation.getCouriersWithStatus(0).size());
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(1).compareTo(new BigDecimal(0)) > 0);
        Assert.assertTrue(this.courierOperation.getAverageCourierProfit(5).compareTo(new BigDecimal(0)) > 0);
    }

    private int insertCity(final String name, final String postalCode) {
        final int idCity = this.cityOperations.insertCity(name, postalCode);
        Assert.assertTrue(-1L != (long) idCity);
        Assert.assertTrue(this.cityOperations.getAllCities().contains(idCity));
        return idCity;
    }

    int insertAddress(final String street, final int number, final int idCity, final int x, final int y) {
        final int idAddress = this.addressOperations.insertAddress(street, number, idCity, x, y);
        Assert.assertTrue(-1L != (long) idAddress);
        Assert.assertTrue(this.addressOperations.getAllAddresses().contains(idAddress));
        this.addressesCoords.put(idAddress, (Pair<Integer, Integer>) new Pair((Object) x, (Object) y));
        return idAddress;
    }

    private String insertUser(final String username, final String firstName, final String lastName, final String password, final int idAddress) {
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password, idAddress));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username));
        return username;
    }

    String insertCourier(final String username, final String firstName, final String lastName, final String password, final int idAddress, final String driverLicenceNumber) {
        this.insertUser(username, firstName, lastName, password, idAddress);
        Assert.assertTrue(this.courierOperation.insertCourier(username, driverLicenceNumber));
        return username;
    }

    public void insertAndParkVehicle(final String licencePlateNumber, final BigDecimal fuelConsumption, final BigDecimal capacity, final int fuelType, final int idStockroom) {
        Assert.assertTrue(this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity));
        Assert.assertTrue(this.vehicleOperations.getAllVehichles().contains(licencePlateNumber));
        Assert.assertTrue(this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom));
    }

    public int insertStockroom(final int idAddress) {
        final int stockroomId = this.stockroomOperations.insertStockroom(idAddress);
        Assert.assertTrue(-1L != (long) stockroomId);
        Assert.assertTrue(this.stockroomOperations.getAllStockrooms().contains(stockroomId));
        return stockroomId;
    }

    int insertAndAcceptPackage(final int addressFrom, final int addressTo, final String userName, final int packageType, final BigDecimal weight) {
        final int idPackage = this.packageOperations.insertPackage(addressFrom, addressTo, userName, packageType, weight);
        Assert.assertTrue(-1L != (long) idPackage);
        Assert.assertTrue(this.packageOperations.acceptAnOffer(idPackage));
        Assert.assertTrue(this.packageOperations.getAllPackages().contains(idPackage));
        Assert.assertEquals(1L, (long) this.packageOperations.getDeliveryStatus(idPackage));
        final BigDecimal price
                = Util.getPackagePrice(packageType, weight, Util.getDistance(new Pair[]{this.addressesCoords.get(addressFrom), this.addressesCoords.get(addressTo)}));
        //System.out.println("cena: " + Util.getPackagePrice(packageType, weight, Util.getDistance(new Pair[] { this.addressesCoords.get(addressFrom), this.addressesCoords.get(addressTo) })));
        //System.out.println("cena1: " + new dv180009_PackageOperations().getPriceOfDelivery(idPackage));
        Assert.assertTrue(this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(1.05))) < 0);
        Assert.assertTrue(this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(0.95))) > 0);
        this.packagePrice.put(idPackage, price);
        return idPackage;
    }
}
