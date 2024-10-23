import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DecisionTreeClassifier {
    private Map<String, String> tree = new HashMap<>();

    public void train(double[][] data, String[] labels) {
        for (int i = 0; i < data.length; i++) {
            tree.put(Arrays.toString(data[i]), labels[i]);
        }
    }

    public String predict(double[] features) {
        return tree.getOrDefault(Arrays.toString(features), "unknown");
    }
}
