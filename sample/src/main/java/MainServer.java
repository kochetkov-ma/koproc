import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MainServer {

    public static void main(String[] args) throws Exception {
        int port = 8000;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Throwable throwable) {
                System.err.println("[KOPROC SAMPLE] Error. " + throwable.toString());
            }
        }

        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("[KOPROC SAMPLE] Started");
    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(final HttpExchange t) throws IOException {
            System.out.println("[KOPROC SAMPLE] Request: " + t.getRequestMethod() + " " + t.getRequestURI());

            final String response = "OK";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

            System.out.println("[KOPROC SAMPLE] Response: " + t.getResponseCode() + " " + t.getResponseBody());
        }
    }
}
