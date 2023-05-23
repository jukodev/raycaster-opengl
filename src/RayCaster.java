import org.lwjgl.opengl.GL11;
import java.io.IOException;

public class RayCaster {
    private double horizontalX, horizontalY, verticalX, verticalY;
    private final int[] map;

    private final Texture texture;

    public RayCaster(int[] map) {
        this.map = map;
        try {
            texture = new Texture("F:\\Bibliothek\\Library\\hase32.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int RES_SCALE = 8;

    public void draw(float playerX, float playerY, float playerAngle){
        double rayPosX, rayPosY, rayAngle, distance;

        double DR = 0.0174533 / RES_SCALE;
        rayAngle = playerAngle - DR * 30 * RES_SCALE;
        if(rayAngle < 0) rayAngle += 2 * Math.PI;
        if(rayAngle > 2 * Math.PI) rayAngle -= 2 * Math.PI;

        for(int i = 0; i < 60 * RES_SCALE; i++){
            double disV = castVerticalRay(playerX, playerY, rayAngle);
            double disH = castHorizontalRay(playerX, playerY, rayAngle);
            float shading = 1f;
            if(disV < disH){
                rayPosX = verticalX;
                rayPosY = verticalY;
                distance = disV;
                shading = .5f;

            }else{
                rayPosX = horizontalX;
                rayPosY = horizontalY;
                distance = disH;
            }
            GL11.glColor3f(0,1,1);
            GL11.glLineWidth(1);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(Window.getNormalX(playerX), Window.getNormalY(playerY));
            GL11.glVertex2f(Window.getNormalX((float) rayPosX), Window.getNormalY((float) rayPosY));
            GL11.glEnd();

            drawVerticalLine(i, distance, playerAngle, rayAngle, shading,  rayPosX, rayPosY);

            rayAngle += DR;
            if(rayAngle < 0) rayAngle += 2 * Math.PI;
            if(rayAngle > 2 * Math.PI) rayAngle -= 2 * Math.PI;
        }
    }

    private void drawVerticalLine(int index, double distance, float playerAngle, double rayAngle, float shading, double rayPosX, double rayPosY) {
        double normalizedAngle = playerAngle - rayAngle;
        if (normalizedAngle < 0) {
            normalizedAngle += 2 * Math.PI;
        }
        if (normalizedAngle > 2 * Math.PI) {
            normalizedAngle -= 2 * Math.PI;
        }
        distance *= Math.cos(normalizedAngle);
        double lineH = (64 * 320) / distance;

        double lineO = 160 - lineH / 2;


        float textureY = 0;
        float textureX;
        if(shading == 1){
            textureX = (int) (rayPosX / 2.0) % 32;
            if(rayAngle > 180) textureX = 31-textureX;
        }
        else{
            textureX = (int) (rayPosY / 2.0) % 32;
            if(rayAngle > 90 && rayAngle < 270) textureX = 31-textureX;
        }

        // textureY += 32;

        float textureYStep = 32f / (float) lineH;

        for(int i = 0; i < lineH; i++){
            // float c = DataDump.ALL_TEXTURES[(int) (textureY) * 32 + (int) (textureX)] * shading;
            float r =  (texture.getColors()[((int) (textureY) * 32 + (int) (textureX)) % 1024].getRed());
            float g =  (texture.getColors()[((int) (textureY) * 32 + (int) (textureX)) % 1024].getGreen());
            float b =  (texture.getColors()[((int) (textureY) * 32 + (int) (textureX)) % 1024].getBlue());
            GL11.glColor3f(r, g, b);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glVertex2f(Window.getNormalX(index * (8 / RES_SCALE) + 530), Window.getNormalY((float) lineO + i));
            GL11.glEnd();
            textureY += textureYStep;
        }


    }

    private double castHorizontalRay(float playerX, float playerY, double rayAngle) {
        int mapX, mapY, mapIndex;
        double rayPosX, rayPosY, offsetX = 0, offsetY = 0;
        int rayDepth = 0;
        double horizontalDistance = 100000;
        double aTan = (-1 / Math.tan(rayAngle));

        // Looking up
        if (rayAngle > Math.PI) {
            rayPosY = (((int) playerY >> 6) << 6) - 0.0001;
            rayPosX = (playerY - rayPosY) * aTan + playerX;
            offsetY = -64;
            offsetX = -offsetY * aTan;
        }
        // Looking down
        else if (rayAngle < Math.PI) {
            rayPosY = (((int) playerY >> 6) << 6) + 64;
            rayPosX = (playerY - rayPosY) * aTan + playerX;
            offsetY = 64;
            offsetX = -offsetY * aTan;
        }
        // Looking left or right
        else {
            rayPosX = playerX;
            rayPosY = playerY;
            rayDepth = 8;
        }

        while (rayDepth < 8) {
            mapX = (int) rayPosX >> 6;
            mapY = (int) rayPosY >> 6;
            mapIndex = mapY * 8 + mapX;
            if (mapIndex > 0 && mapIndex < 64 && map[mapIndex] == 1) {
                horizontalX = rayPosX;
                horizontalY = rayPosY;
                horizontalDistance = distance(playerX, playerY, horizontalX, horizontalY);
                rayDepth = 8;
            } else {
                rayPosX += offsetX;
                rayPosY += offsetY;
                rayDepth++;
            }
        }
        return horizontalDistance;
    }

    private double castVerticalRay(float playerX, float playerY, double rayAngle) {
        int mapX, mapY, mapIndex;
        double rayPosX, rayPosY, offsetX = 0, offsetY = 0;
        int rayDepth = 0;
        double verticalDistance = 100000;
        double nTan = -Math.tan(rayAngle);

        // Looking left
        double p2 = Math.PI / 2;
        double p3 = 3 * Math.PI / 2;
        if (rayAngle > p2 && rayAngle < p3) {
            rayPosX = (((int) playerX >> 6) << 6) - 0.0001;
            rayPosY = (playerX - rayPosX) * nTan + playerY;
            offsetX = -64;
            offsetY = -offsetX * nTan;
        }
        // Looking right
        else if (rayAngle < p2 || rayAngle > p3) {
            rayPosX = (((int) playerX >> 6) << 6) + 64;
            rayPosY = (playerX - rayPosX) * nTan + playerY;
            offsetX = 64;
            offsetY = -offsetX * nTan;
        }
        // Looking up or down
        else {
            rayPosX = playerX;
            rayPosY = playerY;
            rayDepth = 8;
        }

        while (rayDepth < 8) {
            mapX = (int) rayPosX >> 6;
            mapY = (int) rayPosY >> 6;
            mapIndex = mapY * 8 + mapX;
            if (mapIndex > 0 && mapIndex < 64 && map[mapIndex] == 1) {
                verticalX = rayPosX;
                verticalY = rayPosY;
                verticalDistance = distance(playerX, playerY, verticalX, verticalY);
                rayDepth = 8;
            } else {
                rayPosX += offsetX;
                rayPosY += offsetY;
                rayDepth++;
            }
        }
        return verticalDistance;
    }


    private double distance(double ax, double ay, double bx, double by){
        return (Math.sqrt((bx - ax) * (bx - ax) + (by - ay)* (by - ay)));
    }
}
