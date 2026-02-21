import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.math.BigInteger;

public class Polynomial {

    // Exact Lagrange interpolation at x = 0
    public static BigInteger findConstant(List<BigInteger[]> points) {
        int n = points.size();

        BigInteger finalNum = BigInteger.ZERO;
        BigInteger finalDen = BigInteger.ONE;

        for (int i = 0; i < n; i++) {
            BigInteger xi = points.get(i)[0];
            BigInteger yi = points.get(i)[1];

            BigInteger num = yi;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < n; j++) {
                if (i == j) continue;

                BigInteger xj = points.get(j)[0];
                num = num.multiply(xj.negate());      // (0 - xj)
                den = den.multiply(xi.subtract(xj));  // (xi - xj)
            }

            // Add fractions safely
            finalNum = finalNum.multiply(den).add(num.multiply(finalDen));
            finalDen = finalDen.multiply(den);

            // Reduce fraction
            BigInteger gcd = finalNum.gcd(finalDen);
            finalNum = finalNum.divide(gcd);
            finalDen = finalDen.divide(gcd);
        }

        return finalNum.divide(finalDen);
    }

    // Process one test file
    public static void runTest(String fileName) {
        try {
            System.out.println("Processing: " + fileName);

            String content = new String(
                    Files.readAllBytes(Paths.get(fileName))
            );

            JSONObject data = new JSONObject(content);
            int k = data.getJSONObject("keys").getInt("k");

            List<Long> sortedKeys = new ArrayList<>();
            for (String key : data.keySet()) {
                if (!key.equals("keys")) {
                    sortedKeys.add(Long.parseLong(key));
                }
            }
            Collections.sort(sortedKeys);

            List<BigInteger[]> points = new ArrayList<>();

            for (int i = 0; i < k; i++) {
                String keyStr = String.valueOf(sortedKeys.get(i));
                JSONObject obj = data.getJSONObject(keyStr);

                BigInteger x = new BigInteger(keyStr);
                int base = Integer.parseInt(obj.getString("base"));
                BigInteger y = new BigInteger(obj.getString("value"), base);

                points.add(new BigInteger[]{x, y});
            }

            BigInteger c = findConstant(points);
            System.out.println("Constant term (c) = " + c);
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error processing " + fileName);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runTest("testcase.json");
        runTest("testcase2.json");
    }
}