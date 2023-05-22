import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    @Getter
    private final Color[] colors = new Color[1024];

    public Texture(String path){
        File file = new File(path);
        try {
            BufferedImage image = ImageIO.read(file);
            int index = 0;
            for(int y = 0; y < 32; y++){
                for(int x = 0; x < 32; x++){
                    int rgb = image.getRGB(x,y);
                    colors [index] = new Color((byte) (rgb >> 16), (byte) (rgb >> 8), (byte) (rgb & 0xFF));
                    index++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("File not found: " + path);
        }
    }

    public record Color(byte r, byte g, byte b){
        public byte getR(){return r;}
        public byte getG(){return g;}
        public byte getB(){return b;}
    }

}
