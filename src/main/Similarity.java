package main;

import reader.Reader;
import reader.Review;
import similarity.TF_IDF;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Similarity {
    public static void main(String[] args) throws IOException {

        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);


        /* Do this once,  sort + select externally because I'm lazy
        ==> below are the 20 most reviewed movies in the data set

        // Counts how often each film is reviewed
        Map<String, Integer> filmCount = reviews.stream().collect(Collectors.toMap(r -> r.getProduct().getProductID(), v -> 1, (v1, v2) -> v1 + 1));

        List<String> output = new LinkedList<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : filmCount.entrySet()) {
            String key = stringIntegerEntry.getKey();
            int count = stringIntegerEntry.getValue();
            output.add(count + ":" + key);
        }
        Files.write(Paths.get("data/out"), output);
        */

        List<String> all = reviews.parallelStream().unordered().map(r -> r.getText()).collect(Collectors.toList());
        final Map<String, String> filtered = reviews.stream()
                .filter(r -> Similarity.filter(r.getProduct().getProductID()))
                .collect(Collectors.toMap(r -> r.getProduct().getProductID(),
                        r -> r.getText(),
                        (l1, l2) -> l1 + " " + l2));

        List<String> IDs = new LinkedList<>();
        final List<String> top20 = filtered.entrySet().stream().peek(film -> IDs.add(film.getKey())).map(film -> film.getValue()).collect(Collectors.toList());
        String[] filmIDs = IDs.toArray(new String[0]);

        TF_IDF tf_idf = new TF_IDF(top20);
        final double[][] tfidf = tf_idf.tf_idf(top20);


        double[] mins = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[] maxs = {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};

        String[] minIDsI = {"", "", ""};
        String[] maxIDsI = {"", "", ""};
        String[] minIDsJ = {"", "", ""};
        String[] maxIDsJ = {"", "", ""};

        for (int i = 0; i < tfidf.length; i++) {
            for (int j = 0; j < i; j++) {
                double t = tfidf[i][j];
                if (t < mins[0]) {
                    mins[2] = mins[1];
                    mins[1] = mins[0];
                    mins[0] = t;
                    minIDsI[0] = filmIDs[i];
                    minIDsJ[0] = filmIDs[j];
                    continue;
                }
                if (t < mins[1]) {
                    mins[2] = mins[1];
                    mins[1] = t;
                    minIDsI[1] = filmIDs[i];
                    minIDsJ[1] = filmIDs[j];
                    continue;
                }
                if (t < mins[2]) {
                    mins[2] = t;
                    minIDsI[2] = filmIDs[i];
                    minIDsJ[2] = filmIDs[j];
                    continue;
                }
                if (t > maxs[0]) {
                    maxs[2] = maxs[1];
                    maxs[1] = maxs[0];
                    maxs[0] = t;
                    maxIDsI[0] = filmIDs[i];
                    maxIDsJ[0] = filmIDs[j];
                    continue;
                }
                if (t > maxs[1]) {
                    maxs[2] = maxs[1];
                    maxs[1] = t;
                    maxIDsI[1] = filmIDs[i];
                    maxIDsJ[1] = filmIDs[j];
                    continue;
                }
                if (t > maxs[2]) {
                    maxs[2] = t;
                    maxIDsI[2] = filmIDs[i];
                    maxIDsJ[2] = filmIDs[j];
                    continue;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("Minimum " + i + 1 + ": " + minIDsI[i] + " & " + minIDsJ[i] + ": " + mins[i]);
        }
        for (int i = 0; i < 3; i++) {
            System.out.println("Maximum " + i + 1 + ": " + maxIDsI[i] + " & " + maxIDsJ[i] + ": " + maxs[i]);
        }
    }

    private static boolean filter(String id) {
        switch (id) {
            case "B002LBKDYE":
            case "B004WO6BPS":
            case "B009NQKPUW":
            case "B000VBJEFK":
            case "7883704540":
            case "B0028OA3EY":
            case "B0028OA3EO":
            case "B008PZZND6":
            case "B006TTC57C":
            case "B002VL2PTU":
            case "B001NFNFMQ":
            case "B000067JG4":
            case "B000067JG3":
            case "B000MMMTAK":
            case "B003DBEX6K":
            case "B001TAFCBC":
            case "B0039UTDFG":
            case "B000KKQNRO":
            case "B0002Y69NQ":
            case "B005CA4SJW":
                return true;
            default:
                return false;
        }
    }
}
