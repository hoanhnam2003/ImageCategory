import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImagePreprocessing {

    public static double[] extractFeatures(BufferedImage image) {
        // Resize ảnh về kích thước cố định, ví dụ 100x100
        BufferedImage resizedImage = resizeImage(image, 100, 100);

        // Trích xuất đặc trưng từ ảnh resized
        double[] features = new double[100 * 100]; // Mảng để chứa đặc trưng (giả sử 100x100)
        int index = 0;
        for (int y = 0; y < resizedImage.getHeight(); y++) {
            for (int x = 0; x < resizedImage.getWidth(); x++) {
                int pixel = resizedImage.getRGB(x, y);
                // Có thể chuyển đổi giá trị pixel RGB thành đặc trưng (giả sử chỉ lấy giá trị độ sáng)
                features[index++] = pixelToFeature(pixel);
            }
        }
        return features;
    }

    // Phương thức resize ảnh
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    // Phương thức chuyển đổi pixel thành giá trị đặc trưng
    private static double pixelToFeature(int pixel) {
        // Ví dụ: chỉ sử dụng giá trị độ sáng (grayscale) của pixel
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = pixel & 0xff;
        return (red + green + blue) / 3.0; // Trả về giá trị độ sáng trung bình
    }
}
