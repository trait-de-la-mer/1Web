
import com.fastcgi.FCGIInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {
    private static final String HTTP_RESPONSE = """
            HTTP/1.1 200 OK
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String HTTP_ERROR = """
            HTTP/1.1 400 Bad Request
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String RESULT_JSON = """
            {
                "time": "%s",
                "now": "%s",
                "result": %b
            }
            """;
    private static final String ERROR_JSON = """
            {
                "now": "%s",
                "reason": "%s"
            }
            """;


    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            try {
                String queryParams = readAllStream(System.in);
                Params params= new Params(queryParams);
                Instant startTime = Instant.now();
                boolean result = calculate(params.getX(), params.getY(), params.getR());
                Instant endTime = Instant.now();
                Long timeBetween = Long.valueOf(ChronoUnit.NANOS.between(startTime, endTime));
                Boolean resultObj = Boolean.valueOf(result);
                String json = String.format(RESULT_JSON, timeBetween, LocalDateTime.now(), resultObj);
                Integer massegeLenght = Integer.valueOf(String.valueOf(json.getBytes(StandardCharsets.UTF_8).length + 2));
                String response = String.format(HTTP_RESPONSE, massegeLenght, json);
                System.out.println(response);
            } catch (Exception e) {
                String json = String.format(ERROR_JSON, LocalDateTime.now(), e.getMessage());
                Integer massegeLenght = Integer.valueOf(String.valueOf(json.getBytes(StandardCharsets.UTF_8).length + 2));
                String response = String.format(HTTP_ERROR, massegeLenght, json);
                System.out.println(response);
            }
        }
    }


    public static String readAllStream(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(chunk)) != -1) {
            buffer.write(chunk, 0, bytesRead);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static boolean calculate(BigDecimal x, BigDecimal y, BigDecimal r) {
        if (x.compareTo(BigDecimal.ZERO) > 0 && y.compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }
        if ((x.compareTo(BigDecimal.ZERO) >= 0) && (y.compareTo(BigDecimal.ZERO) <= 0)){
            BigDecimal halfX = x.divide(BigDecimal.valueOf(2));
            BigDecimal halfR = r.divide(BigDecimal.valueOf(2));
            BigDecimal xMinR = halfX.subtract(halfR);
            return ((x.compareTo(r) <= 0) && (y.compareTo(xMinR) >= 0) );
        }
        if ((x.compareTo(BigDecimal.valueOf(0)) <= 0) && (y.compareTo(BigDecimal.valueOf(0)) <= 0)) {
            BigDecimal xx = x.multiply(x);
            BigDecimal yy = y.multiply(y);
            BigDecimal yyXx = yy.add(xx);
            BigDecimal rr = r.multiply(r);
            return (yyXx.compareTo(rr) <= 0);
        }
        if ((x.compareTo(BigDecimal.valueOf(0)) <= 0) && ((y.compareTo(BigDecimal.valueOf(0)) >= 0))) {
            BigDecimal halfR = r.divide(BigDecimal.valueOf(-2));
            return ((x.compareTo(halfR) >= 0) && (y.compareTo(r) <= 0));
        }
        return true;
    }
}