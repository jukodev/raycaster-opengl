import lombok.Getter;
import lombok.val;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Texture {
    @Getter
    private final Color[] colors;

    public Texture(String path) throws IOException {
        val file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }

        val image = ImageIO.read(file);
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

    // Load in all textures from folder
    public static Texture[] loadTextures(){
        val folder = new File("rsc");
        if(folder.listFiles() == null){
            throw new RuntimeException("No textures found at '/rsc'");
        }
        val files = (File[]) Arrays.stream(folder.listFiles()).filter(file -> file.getName().endsWith(".png")).toArray();
        val textures = new Texture[files.length];
        for(int i = 0; i < files.length; i++){
            try {
                textures[i] = new Texture(files[i].getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return textures;
    }
}
