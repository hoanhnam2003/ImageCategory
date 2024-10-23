import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KNNClassifier {
    private double[][] trainingData;
    private String[] trainingLabels;
    private int k;

    public KNNClassifier(int k) {
        this.k = k;
    }

    public void train(double[][] data, String[] labels) {
        this.trainingData = data;
        this.trainingLabels = labels;

        // Kiểm tra giá trị k
        if (k > trainingData.length) {
            k = trainingData.length; // Đặt k về số lượng dữ liệu huấn luyện
        }
        if (k < 1) {
            throw new IllegalArgumentException("K phải lớn hơn 0.");
        }
    }

    // Thay đổi kiểu trả về thành String
    public String predict(double[] features) {
        double[] distances = new double[trainingData.length];
        for (int i = 0; i < trainingData.length; i++) {
            distances[i] = euclideanDistance(features, trainingData[i]);
        }

        // Tìm k nhãn gần nhất
        int[] nearestIndices = getKNearestIndices(distances);
        return getPredictedLabel(nearestIndices, distances);
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private int[] getKNearestIndices(double[] distances) {
        // Tạo mảng chỉ số
        Integer[] indices = new Integer[distances.length];
        for (int i = 0; i < distances.length; i++) {
            indices[i] = i; // Gán chỉ số
        }

        // Sắp xếp chỉ số theo khoảng cách
        Arrays.sort(indices, (i1, i2) -> Double.compare(distances[i1], distances[i2]));

        // Chuyển đổi mảng chỉ số Integer[] thành int[]
        int[] nearestIndices = new int[k];
        for (int i = 0; i < k; i++) {
            nearestIndices[i] = indices[i]; // Lấy k chỉ số gần nhất
        }

        return nearestIndices;
    }

    private String getPredictedLabel(int[] nearestIndices, double[] distances) {
        Map<String, Double> labelWeightMap = new HashMap<>();

        for (int index : nearestIndices) {
            String label = trainingLabels[index];
            // Tính trọng số dựa trên khoảng cách (trong trường hợp này là nghịch đảo của khoảng cách)
            double weight = 1 / (distances[index] + 1e-5); // Thêm 1e-5 để tránh chia cho 0
            labelWeightMap.put(label, labelWeightMap.getOrDefault(label, 0.0) + weight);
        }

        // Tìm nhãn có trọng số cao nhất
        String predictedLabel = null;
        double maxWeight = -1;

        for (Map.Entry<String, Double> entry : labelWeightMap.entrySet()) {
            if (entry.getValue() > maxWeight) {
                maxWeight = entry.getValue();
                predictedLabel = entry.getKey();
            }
        }

        // Trả về nhãn dự đoán
        return predictedLabel;
    }
}
