import com.alix.FooService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shaobo on 11/30/16.
 */
public class BarTest {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("com.alix", "com.pingnotes.client");
        FooService fs = (FooService) ctx.getBean("fooService");
        ExecutorService service = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        fs.foo();
                    }
                }
            });
        }
    }
}
