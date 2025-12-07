import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Params {
    private final BigDecimal x;
    private final BigDecimal y;
    private final BigDecimal r;

    private List<String> sorting(String[] unSortPar){
        HashMap<String, String> params = new HashMap<>();
        Main.testOut = "";
        for (String par: unSortPar) {
            Main.testOut += "|||";
            String[] letterSign = par.split("=");
            String letter = letterSign[0];
            String realSign = letterSign[1];
            Main.testOut += letter.charAt(letter.length() - 1);
            switch (letter.charAt(letter.length() - 1)) {
                case 'Y' -> {
                    params.put("y", realSign);
                }
                case 'R' -> {
                    params.put("r", realSign);
                }
                case 'X' -> {
                    params.put("x", realSign);
                }
            }
        }
        List<String> sortList = new ArrayList<>();
        sortList.add(params.get("x"));
        sortList.add(params.get("y"));
        sortList.add(params.get("r"));
        return sortList;
    }

    public Params(String query) throws ValidationException {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Missing query string");
        }
        var badParams = query.split("&");
        List<String> params = sorting(badParams);
        //Main.testOut = params.toString();
        //for (String par: badParams) {
        //    String[] letterSign = par.split("=");
        //    String letter = letterSign[0];
        //    String realSign = letterSign[1];
        //    params.add(letter + realSign);
        //}
        validateParams(params);
        this.x = new BigDecimal(params.get(0));
        this.y = new BigDecimal(params.get(1));
        this.r = new BigDecimal(params.get(2));
    }


    private static void validateParams(List<String> params) throws ValidationException {
        var x = params.get(0);
        if (x == null || x.isEmpty()) {
            throw new ValidationException("x is invalid");
        }
        try {
            var xx = Integer.parseInt(x);
            if (xx < -4 || xx > 4) {
                throw new ValidationException("x has forbidden value");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("x is not a number");
        }

        var y = params.get(1);
        if (y == null || y.isEmpty()) {
            throw new ValidationException("y is invalid");
        }
        try {
            var yy = new BigDecimal(y);
            if (yy.compareTo(BigDecimal.valueOf(-5)) < 0 || yy.compareTo(BigDecimal.valueOf(5)) > 0) {
                throw new ValidationException("y has forbidden value");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("y is not a number");
        }

        var r = params.get(2);
        if (r == null || r.isEmpty()) {
            throw new ValidationException("r is invalid");
        }

        try {
            var rr = Float.parseFloat(r);
            if (rr < 1 || rr > 5) {
                throw new ValidationException("r has forbidden value");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("r is not a number");
        }
    }


    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getR() {
        return r;
    }
}