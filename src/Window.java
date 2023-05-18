import lombok.Getter;
import lombok.val;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Window {

    private static final int WINDOW_WIDTH = 1024, WINDOW_HEIGHT = 512;
    private long window;
    private Player player;

    private int mapX = 8, mapY = 8, mapS = 64;
    @Getter
    private int map[] = {
            1,1,1,1,1,1,1,1,
            1,0,0,0,0,0,0,1,
            1,0,1,1,1,0,0,1,
            1,0,0,1,0,0,0,1,
            1,0,0,0,0,0,0,1,
            1,0,1,0,0,1,0,1,
            1,0,0,0,0,1,0,1,
            1,1,1,1,1,1,1,1,
    };

    public void run() {
        init();
        loop();

        GLFW.glfwTerminate();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "RayCaster", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f); //background color

        player = new Player(window, map);
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            drawMap();
            player.draw();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void drawMap(){
        int xo, yo;
        for(int y = 0; y < mapY; y++){
            for(int x = 0; x < mapX; x++){
                int color = map[y*mapX+x] == 1 ? 1 : 0;
                glColor3f(color, color, color);
                xo = x*mapS;
                yo = y*mapS;
                glBegin(GL_QUADS);
                glVertex2f(getNormalX(xo + 1), getNormalY(yo + 1));
                glVertex2f(getNormalX(xo + 1), getNormalY(mapS + yo - 1));
                glVertex2f(getNormalX(mapS + xo - 1), getNormalY(mapS + yo - 1));
                glVertex2f(getNormalX(mapS  +xo - 1), getNormalY(yo + 1));
                glEnd();
            }
        }
    }

    // draws pixel at given coordinates
    private void drawPixel(int x, int y, Color color){
        val ndcX = getNormalX(x);
        val ndcY = getNormalY(y);
        if(color != null)
            GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        GL11.glVertex2f(ndcX, ndcY);
    }


    private void drawRay(Ray ray){
        val xLength = ray.getEndX() - ray.getStartX();
        val yLength = ray.getEndY() - ray.getStartY();
        float xMulti = xLength > 0 ? 1 : -1;
        float yMulti = yLength > 0 ? 1 : -1;
        int usedLength;

        if(Math.abs(xLength) > Math.abs(yLength)){
            yMulti = (float)(yLength) / (float) Math.abs(xLength);
            usedLength = Math.abs(xLength);
        }else{
            xMulti = (float)(xLength) / (float) Math.abs(yLength);
            usedLength = Math.abs(yLength);
        }

        for(float i = 0; i <= usedLength; i ++){
            drawPixel((int)(ray.getStartX() + i * xMulti), (int)(ray.getStartY() + i * yMulti), ray.getColor());
        }
    }

    public static float getNormalX(int i){
        return ((2.0f * i / WINDOW_WIDTH) - 1.0f);
    }

    public static float getNormalY(int i){
        return (1.0f - (2.0f * i / WINDOW_HEIGHT));
    }

    public static float getNormalX(float i){
        return ((2.0f * i / WINDOW_WIDTH) - 1.0f);
    }

    public static float getNormalY(float i){
        return (1.0f - (2.0f * i / WINDOW_HEIGHT));
    }


    public static void main(String[] args) {
        Window app = new Window();
        app.run();
    }
}
