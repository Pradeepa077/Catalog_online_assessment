import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        String inputFilePath = "input.json"; // Path to your JSON input file
        JSONObject jsonObject = readJsonFile(inputFilePath);

        // Read keys
        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Arrays to store x and y values
        BigInteger[] xValues = new BigInteger[k];
        BigInteger[] yValues = new BigInteger[k];

        // Decode the y values
        int index = 0;
        for (String key : jsonObject.keySet()) {
            if (key.equals("keys")) continue; // Skip keys object
            JSONObject point = jsonObject.getJSONObject(key);
            BigInteger x = new BigInteger(key);
            BigInteger y = decodeValue(point.getString("base"), point.getString("value"));
            if (index < k) { // We only need k points
                xValues[index] = x;
                yValues[index] = y;
                index++;
            }
        }

        // Calculate the secret (constant term c) using Lagrange Interpolation
        BigInteger secret = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
        System.out.println("The constant term c is: " + secret);
    }

    private static JSONObject readJsonFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BigInteger decodeValue(String base, String value) {
        int baseNum = Integer.parseInt(base);
        return new BigInteger(value, baseNum);
    }

    private static BigInteger lagrangeInterpolation(BigInteger[] xValues, BigInteger[] yValues, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        int k = xValues.length;

        for (int i = 0; i < k; i++) {
            BigInteger xi = xValues[i];
            BigInteger yi = yValues[i];
            BigInteger term = yi;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = xValues[j];
                    term = term.multiply(x.subtract(xj)).divide(xi.subtract(xj));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
