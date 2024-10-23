import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        // Giả sử bạn có đường dẫn ảnh từ URL hoặc từ file
        String[] imageUrls = {
                "C:\\Users\\Admin\\Downloads\\images.jpg",
                "C:\\Users\\Admin\\Downloads\\images (1).jpg",
                "C:\\Users\\Admin\\Downloads\\z407811279454704d3b10be2c47b86e5bd170832bde562-16753562253021567389777.webp"
        };

        // Số lượng nhãn tương ứng với dữ liệu huấn luyện
        String[] trainLabels = {"flower", "animal", "person"}; // Cập nhật nhãn
        double[][] trainData = new double[trainLabels.length][30000]; // Giả sử kích thước 100x100

        // Tải và trích xuất đặc trưng cho ảnh huấn luyện
        for (int i = 0; i < trainLabels.length; i++) {
            try {
                BufferedImage image = loadImage(imageUrls[i]);
                if (image != null) {
                    double[] features = ImagePreprocessing.extractFeatures(image); // Gọi phương thức trích xuất đặc trưng đã sửa
                    trainData[i] = features;
                }
            } catch (IOException e) {
                System.out.println("Lỗi khi xử lý ảnh thứ " + (i + 1) + ": " + e.getMessage());
            }
        }

        // Tạo và huấn luyện các mô hình
        SVMClassifier svm = new SVMClassifier(0.001, 1000);
        long startTimeSVM = System.currentTimeMillis();
        svm.train(trainData, convertLabelsToBinary(trainLabels));
        long endTimeSVM = System.currentTimeMillis();

        KNNClassifier knn = new KNNClassifier(3); // K = 3
        long startTimeKNN = System.currentTimeMillis();
        knn.train(trainData, trainLabels);
        long endTimeKNN = System.currentTimeMillis();

        DecisionTreeClassifier decisionTree = new DecisionTreeClassifier();
        long startTimeDT = System.currentTimeMillis();
        decisionTree.train(trainData, trainLabels);
        long endTimeDT = System.currentTimeMillis();

        // Xuất thời gian huấn luyện
        System.out.println("Training time (SVM): " + (endTimeSVM - startTimeSVM) + " ms");
        System.out.println("Training time (KNN): " + (endTimeKNN - startTimeKNN) + " ms");
        System.out.println("Training time (Decision Tree): " + (endTimeDT - startTimeDT) + " ms");

        int[] trueLabels = {0, 1, 2}; // Giả định nhãn thực cho hình ảnh tương ứng (flower: 0, animal: 1, person: 2)
        int correctSVM = 0, correctKNN = 0, correctDT = 0;
        String[] predictedLabelsSVM = new String[imageUrls.length]; // Thay đổi sang String
        String[] predictedLabelsKNN = new String[imageUrls.length]; // Thay đổi sang String
        String[] predictedLabelsDT = new String[imageUrls.length]; // Thay đổi sang String

        // Trong phần kiểm thử với tất cả các ảnh huấn luyện
        for (int i = 0; i < imageUrls.length; i++) {
            try {
                BufferedImage testImage = loadImage(imageUrls[i]);
                if (testImage != null) {
                    double[] testFeatures = ImagePreprocessing.extractFeatures(testImage);

                    // Dự đoán với từng mô hình
                    predictedLabelsSVM[i] = svm.predict(testFeatures);
                    predictedLabelsKNN[i] = knn.predict(testFeatures);
                    predictedLabelsDT[i] = decisionTree.predict(testFeatures);

                    // Kiểm tra độ chính xác bằng cách chuyển đổi nhãn sang chỉ số
                    if (getLabelIndex(predictedLabelsSVM[i]) == trueLabels[i]) correctSVM++;
                    if (getLabelIndex(predictedLabelsKNN[i]) == trueLabels[i]) correctKNN++;
                    if (getLabelIndex(predictedLabelsDT[i]) == trueLabels[i]) correctDT++;

                    // Xuất kết quả phân loại
                    System.out.println("Predicted label for image " + (i + 1) + " (SVM): " + predictedLabelsSVM[i]);
                    System.out.println("Predicted label for image " + (i + 1) + " (KNN): " + predictedLabelsKNN[i]);
                    System.out.println("Predicted label for image " + (i + 1) + " (Decision Tree): " + predictedLabelsDT[i]);
                } else {
                    System.out.println("Không thể tải ảnh thứ " + (i + 1));
                }
            } catch (IOException e) {
                System.out.println("Lỗi khi xử lý ảnh thứ " + (i + 1) + ": " + e.getMessage());
            }
        }

        // Tính toán độ chính xác
        double accuracySVM = (double) correctSVM / imageUrls.length;
        double accuracyKNN = (double) correctKNN / imageUrls.length;
        double accuracyDT = (double) correctDT / imageUrls.length;

        // Giả định nhãn thực cho việc tính độ nhạy và độ chính xác
        double precisionSVM = calculatePrecision(predictedLabelsSVM, trueLabels, 0);
        double recallSVM = calculateRecall(predictedLabelsSVM, trueLabels, 0);

        double precisionKNN = calculatePrecision(predictedLabelsKNN, trueLabels, 0);
        double recallKNN = calculateRecall(predictedLabelsKNN, trueLabels, 0);

        double precisionDT = calculatePrecision(predictedLabelsDT, trueLabels, 0);
        double recallDT = calculateRecall(predictedLabelsDT, trueLabels, 0);

        // Xuất kết quả
        System.out.println("Accuracy (SVM): " + accuracySVM);
        System.out.println("Precision (SVM): " + precisionSVM);
        System.out.println("Recall (SVM): " + recallSVM);

        System.out.println("Accuracy (KNN): " + accuracyKNN);
        System.out.println("Precision (KNN): " + precisionKNN);
        System.out.println("Recall (KNN): " + recallKNN);

        System.out.println("Accuracy (Decision Tree): " + accuracyDT);
        System.out.println("Precision (Decision Tree): " + precisionDT);
        System.out.println("Recall (Decision Tree): " + recallDT);
    }

    // Tải ảnh từ đường dẫn
    public static BufferedImage loadImage(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Tệp không tồn tại: " + path);
        }
        return ImageIO.read(file);
    }

    // Chuyển nhãn sang dạng nhị phân cho SVM
    public static int[] convertLabelsToBinary(String[] labels) {
        int[] binaryLabels = new int[labels.length];
        for (int i = 0; i < labels.length; i++) {
            binaryLabels[i] = labels[i].equals("flower") ? 1 : 0; // Chỉ có flower là 1, còn lại là 0
        }
        return binaryLabels;
    }

    // Tính độ chính xác
    private static double calculatePrecision(String[] predictions, int[] trueLabels, int targetLabel) {
        int truePositives = 0;
        int falsePositives = 0;

        for (int i = 0; i < predictions.length; i++) {
            if (getLabelIndex(predictions[i]) == targetLabel) {
                if (trueLabels[i] == targetLabel) {
                    truePositives++;
                } else {
                    falsePositives++;
                }
            }
        }
        return truePositives + falsePositives > 0 ? (double) truePositives / (truePositives + falsePositives) : 0.0;
    }

    // Tính độ nhạy (recall)
    private static double calculateRecall(String[] predictions, int[] trueLabels, int targetLabel) {
        int truePositives = 0;
        int falseNegatives = 0;

        for (int i = 0; i < predictions.length; i++) {
            if (getLabelIndex(predictions[i]) == targetLabel) {
                if (trueLabels[i] == targetLabel) {
                    truePositives++;
                }
            } else {
                if (trueLabels[i] == targetLabel) {
                    falseNegatives++;
                }
            }
        }
        return truePositives + falseNegatives > 0 ? (double) truePositives / (truePositives + falseNegatives) : 0.0;
    }

    // Chuyển đổi nhãn dự đoán thành chỉ số
    private static int getLabelIndex(String label) {
        switch (label) {
            case "flower":
                return 0;
            case "animal":
                return 1;
            case "person":
                return 2;
            default:
                return -1; // Không xác định
        }
    }

}
