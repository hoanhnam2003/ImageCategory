public class SVMClassifier {
    private double[] weights;  // Trọng số của mô hình
    private double bias;       // Bias
    private double learningRate;  // Tốc độ học
    private int maxIterations;    // Số lần lặp tối đa

    public SVMClassifier(double learningRate, int maxIterations) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
    }

    // Hàm huấn luyện mô hình SVM
    public void train(double[][] trainData, int[] trainLabels) {
        // Kiểm tra xem số lượng nhãn và số lượng mẫu có khớp không
        if (trainData.length != trainLabels.length) {
            throw new IllegalArgumentException("Số lượng mẫu và số lượng nhãn không khớp: "
                    + trainData.length + " != " + trainLabels.length);
        }

        int nFeatures = trainData[0].length;
        weights = new double[nFeatures];
        bias = 0;

        // Kiểm tra kích thước dữ liệu
        for (int i = 0; i < trainData.length; i++) {
            double[] data = trainData[i];
            if (data.length != nFeatures) {
                throw new IllegalArgumentException("Kích thước của trainData không đồng nhất. Hàng đầu tiên có "
                        + nFeatures + " cột, nhưng hàng " + i + " có " + data.length + " cột.");
            }
        }

        // Thuật toán Gradient Descent để tối ưu hóa mô hình
        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < trainData.length; i++) {
                double[] x = trainData[i];
                int y = trainLabels[i];

                // Tính toán dự đoán: y' = w*x + b
                double prediction = dotProduct(weights, x) + bias;

                // Kiểm tra điều kiện của Hinge Loss
                if (y * prediction < 1) {
                    // Cập nhật weights và bias
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += learningRate * (y * x[j] - 0.01 * weights[j]);
                    }
                    bias += learningRate * y;
                } else {
                    // Cập nhật chỉ có regularization
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] -= learningRate * 0.01 * weights[j];
                    }
                }
            }
        }
    }


    // Hàm phân loại dựa trên mô hình SVM đã huấn luyện
    public String predict(double[] instance) {
        double prediction = dotProduct(weights, instance) + bias;
        return prediction >= 0 ? "flower" : "not flower"; // Trả về nhãn tương ứng
    }

    // Hàm tính tích vô hướng (dot product)
    private double dotProduct(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }
}
