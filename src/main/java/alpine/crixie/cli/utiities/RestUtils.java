package alpine.crixie.cli.utiities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUtils {

    static HttpURLConnection get(String request){
        try{
            var connection = (HttpURLConnection) new URL(request).openConnection();
            connection.setRequestMethod("GET");
            return connection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
