import com.pingnotes.AutumnApplication;

/**
 * Created by shaobo.
 */
public class AppTest {
    public static void main(String[] args) {
        AutumnApplication.Builder builder = AutumnApplication.newBuilder()
                .setRegistryAddress("192.168.2.121:3721")
                .setServerPort(18080)
                .setScanPackage("com.alix");

        AutumnApplication app = builder.build();
        app.start();
        System.out.println(app.getContainer().allServices());
    }
}
