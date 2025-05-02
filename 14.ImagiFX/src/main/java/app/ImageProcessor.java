package app;

import java.awt.image.BufferedImage;

public class ImageProcessor {

	public BufferedImage applyGrayscale(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b); // Luminance formula
                int grayRgb = (gray << 16) | (gray << 8) | gray;
                dest.setRGB(x, y, grayRgb);
            }
        }
        return dest;
    }

    public BufferedImage applyHot(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                r = Math.min(255, r + 50); // Increase red channel
                int newRgb = (r << 16) | (g << 8) | b;
                dest.setRGB(x, y, newRgb);
            }
        }
        return dest;
    }

    public BufferedImage applyCold(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                b = Math.min(255, b + 50); // Increase blue channel
                int newRgb = (r << 16) | (g << 8) | b;
                dest.setRGB(x, y, newRgb);
            }
        }
        return dest;
    }

    public BufferedImage applyBrighter(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                r = Math.min(255, (int)(r * 1.2)); // Increase brightness by 20%
                g = Math.min(255, (int)(g * 1.2));
                b = Math.min(255, (int)(b * 1.2));
                int newRgb = (r << 16) | (g << 8) | b;
                dest.setRGB(x, y, newRgb);
            }
        }
        return dest;
    }

    public BufferedImage applyContrast(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        double contrastFactor = 1.5; // Increase contrast
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                r = Math.min(255, Math.max(0, (int)((r - 128) * contrastFactor + 128)));
                g = Math.min(255, Math.max(0, (int)((g - 128) * contrastFactor + 128)));
                b = Math.min(255, Math.max(0, (int)((b - 128) * contrastFactor + 128)));
                int newRgb = (r << 16) | (g << 8) | b;
                dest.setRGB(x, y, newRgb);
            }
        }
        return dest;
    }

    public BufferedImage applyPixelate(BufferedImage src) {
        int pixelSize = 10; // Size of pixelation blocks
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y += pixelSize) {
            for (int x = 0; x < src.getWidth(); x += pixelSize) {
                int rgb = src.getRGB(x, y);
                for (int dy = 0; dy < pixelSize && y + dy < src.getHeight(); dy++) {
                    for (int dx = 0; dx < pixelSize && x + dx < src.getWidth(); dx++) {
                        dest.setRGB(x + dx, y + dy, rgb);
                    }
                }
            }
        }
        return dest;
    }
    
    

}
