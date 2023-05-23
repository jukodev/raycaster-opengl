import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    @Getter
    private final Color[] colors;

    public Texture(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }

        BufferedImage image = ImageIO.read(file);
        colors = new Color[1024];
        int index = 0;
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int rgb = image.getRGB(x,y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                colors[index] = new Color(red / 255.0f, green / 255.0f, blue / 255.0f);
                index++;
            }
        }
    }
}
