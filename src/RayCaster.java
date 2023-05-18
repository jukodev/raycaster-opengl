import org.lwjgl.opengl.GL11;

public class RayCaster {
    private final double P2 = Math.PI / 2, P3 = 3*Math.PI/2 , DR = 0.0174533;
    private double horizontalX, horizontalY, verticalX, verticalY;
    private final int[] map;

    public RayCaster(int[] map) {
        this.map = map;
    }

    public void draw(float playerX, float playerY, float playerAngle){
        double rayPosX, rayPosY, rayAngle, distance;

        rayAngle = playerAngle - DR * 30;
        if(rayAngle < 0) rayAngle += 2 * Math.PI;
        if(rayAngle > 2 * Math.PI) rayAngle -= 2 * Math.PI;

        for(int i = 0; i < 60; i++){
            double disV = castVerticalRay(playerX, playerY, rayAngle);
            double disH = castHorizontalRay(playerX, playerY, rayAngle);
            if(disV < disH){
                rayPosX = verticalX;
                rayPosY = verticalY;
                distance = disV;
                GL11.glColor3f(.7f,0,0);

            }else{
                rayPosX = horizontalX;
                rayPosY = horizontalY;
                distance = disH;
                GL11.glColor3f(.8f,0,0);
            }

            GL11.glLineWidth(1);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(Window.getNormalX(playerX), Window.getNormalY(playerY));
            GL11.glVertex2f(Window.getNormalX((float) rayPosX), Window.getNormalY((float) rayPosY));
            GL11.glEnd();

            drawVerticalLine(i, distance, playerAngle, rayAngle);

            rayAngle += DR;
            if(rayAngle < 0) rayAngle += 2 * Math.PI;
            if(rayAngle > 2 * Math.PI) rayAngle -= 2 * Math.PI;
        }
    }

    private void drawVerticalLine(int index, double distance, float playerAngle, double rayAngle) {
        double ca = playerAngle - rayAngle;
        if (ca < 0) {
            ca += 2 * Math.PI;
        }
        if (ca > 2 * Math.PI) {
            ca -= 2 * Math.PI;
        }
        distance *= Math.cos(ca);
        double lineH = (64 * 320) / distance;
        double lineO = 160 - lineH / 2;

        GL11.glLineWidth(8);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(Window.getNormalX(index * 8 + 530), Window.getNormalY((float) lineO));
        GL11.glVertex2f(Window.getNormalX(index * 8 + 530), Window.getNormalY((float) (lineH + lineO)));
        GL11.glEnd();
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
                horizontalDistance = distance(playerX, playerY, horizontalX, horizontalY, rayAngle);
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
        if (rayAngle > P2 && rayAngle < P3) {
            rayPosX = (((int) playerX >> 6) << 6) - 0.0001;
            rayPosY = (playerX - rayPosX) * nTan + playerY;
            offsetX = -64;
            offsetY = -offsetX * nTan;
        }
        // Looking right
        else if (rayAngle < P2 || rayAngle > P3) {
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
                verticalDistance = distance(playerX, playerY, verticalX, verticalY, rayAngle);
                rayDepth = 8;
            } else {
                rayPosX += offsetX;
                rayPosY += offsetY;
                rayDepth++;
            }
        }
        return verticalDistance;
    }


    private double distance(double ax, double ay, double bx, double by, double angle){
        return (Math.sqrt((bx - ax) * (bx - ax) + (by - ay)* (by - ay)));
    }
}
