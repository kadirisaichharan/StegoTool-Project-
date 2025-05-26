import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class StegoTool {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Choose an option: \n1. Hide Message \n2. Extract Message");
            int choice = Integer.parseInt(reader.readLine());

            if (choice == 1) {
                System.out.println("Enter the path to the cover image:");
                String coverImagePath = "./Input/sample_image.jpg";

                System.out.println("Enter the path to the text file containing the secret message:");
                String messageFilePath = "./Input/secret_message.txt";

                System.out.println("Enter the path to save the stego image:");
                String outputImagePath = "./Output/stego_image.jpg";

                hideMessage(coverImagePath, messageFilePath, outputImagePath);
            } else if (choice == 2) {
                System.out.println("Enter the path to the stego image:");
                String stegoImagePath = "./Output/stego_image.jpg";

                extractMessage(stegoImagePath);
            } else {
                System.out.println("Invalid choice. Exiting...");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void hideMessage(String coverImagePath, String messageFilePath, String outputImagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(coverImagePath));
        String message = new String(Files.readAllBytes(new File(messageFilePath).toPath()));

        int width = image.getWidth();
        int height = image.getHeight();

        int messageIndex = 0;
        boolean messageFinished = false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);

                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                if (messageIndex < message.length()) {
                    blue = (blue & 0xFE) | ((message.charAt(messageIndex) >> (7 - (messageIndex % 8))) & 1);
                    messageIndex++;
                } else {
                    messageFinished = true;
                }

                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, newPixel);

                if (messageFinished) break;
            }
            if (messageFinished) break;
        }

        ImageIO.write(image, "png", new File(outputImagePath));
        System.out.println("Message hidden successfully in: " + outputImagePath);
    }

    public static void extractMessage(String stegoImagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(stegoImagePath));
        int width = image.getWidth();
        int height = image.getHeight();

        StringBuilder message = new StringBuilder();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xff;

                char bit = (char) (blue & 1);
                message.append(bit);
            }
        }

        System.out.println("Extracted message: " + message);
    }
}
