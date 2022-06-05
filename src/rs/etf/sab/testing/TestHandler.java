// 
// Decompiled by Procyon v0.5.36
// 

package rs.etf.sab.testing;

//import javax.validation.constraints.NotNull;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.AddressOperations;

public class TestHandler
{
    private static TestHandler testHandler;
    private AddressOperations addressOperations;
    private CityOperations cityOperations;
    private CourierOperations courierOperations;
    private CourierRequestOperation courierRequestOperation;
    private DriveOperation driveOperation;
    private GeneralOperations generalOperations;
    private PackageOperations packageOperations;
    private StockroomOperations stockroomOperations;
    private UserOperations userOperations;
    private VehicleOperations vehicleOperations;
    
    private TestHandler( final AddressOperations addressOperations, final CityOperations cityOperations, 
            final CourierOperations courierOperations, 
            final CourierRequestOperation courierRequestOperation, 
            final DriveOperation driveOperation, 
            final GeneralOperations generalOperations, 
            final PackageOperations packageOperations, 
            final StockroomOperations stockroomOperations,
            final UserOperations userOperations, 
            final VehicleOperations vehicleOperations) {
        this.addressOperations = addressOperations;
        this.cityOperations = cityOperations;
        this.courierOperations = courierOperations;
        this.courierRequestOperation = courierRequestOperation;
        this.driveOperation = driveOperation;
        this.generalOperations = generalOperations;
        this.packageOperations = packageOperations;
        this.stockroomOperations = stockroomOperations;
        this.userOperations = userOperations;
        this.vehicleOperations = vehicleOperations;
    }
    
    public static void createInstance(final AddressOperations addressOperations, final CityOperations cityOperations,
             final CourierOperations courierOperations, final CourierRequestOperation courierRequestOperation, 
             final DriveOperation driveOperation, final GeneralOperations generalOperations, 
             final PackageOperations packageOperations,  final StockroomOperations stockroomOperations, 
             final UserOperations userOperations,  final VehicleOperations vehicleOperations) {
        TestHandler.testHandler = new TestHandler(addressOperations, cityOperations, courierOperations, courierRequestOperation, driveOperation, generalOperations, packageOperations, stockroomOperations, userOperations, vehicleOperations);
    }
    
    static TestHandler getInstance() {
        return TestHandler.testHandler;
    }
    
    public AddressOperations getAddressOperations() {
        return this.addressOperations;
    }
    
    public CityOperations getCityOperations() {
        return this.cityOperations;
    }
    
    public CourierOperations getCourierOperations() {
        return this.courierOperations;
    }
    
    public CourierRequestOperation getCourierRequestOperation() {
        return this.courierRequestOperation;
    }
    
    public DriveOperation getDriveOperation() {
        return this.driveOperation;
    }
    
    public GeneralOperations getGeneralOperations() {
        return this.generalOperations;
    }
    
    public PackageOperations getPackageOperations() {
        return this.packageOperations;
    }
    
    public StockroomOperations getStockroomOperations() {
        return this.stockroomOperations;
    }
    
    public UserOperations getUserOperations() {
        return this.userOperations;
    }
    
    public VehicleOperations getVehicleOperations() {
        return this.vehicleOperations;
    }
    
    static {
        TestHandler.testHandler = null;
    }
}
