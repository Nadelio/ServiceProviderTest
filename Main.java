import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader<Service> loader = loadExtensions();

        for (var service : loader) {
            System.out.println("Running: " + service.getClass().getName());
            service.execute();
        }
    }

    public static ServiceLoader<Service> loadExtensions(){
        var path = Path.of("Services");

        URL[] urls;
        try (var files = Files.list(path)) {
            urls = files.filter((e) -> e.toString().endsWith(".jar"))
                    .map((e) -> {
                        try {
                            return e.toUri().toURL();
                        } catch (MalformedURLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }).toArray(URL[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var classLoader = new URLClassLoader(urls);
        ServiceLoader<Service> loader = ServiceLoader.load(Service.class, classLoader);
        return loader;
    }
}