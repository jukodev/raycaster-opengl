import lombok.val;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Window {
    private long window;

    public void run() {
        init();
        loop();

        GLFW.glfwTerminate();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(800, 600, "OpenGL Test", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); //background color
    }


    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glColor3f(0,1, 0);

            drawRay(100, 100, 120, 400);
            drawRay(100, 100, 500, 220);
            drawRay(120, 400, 495, 300);
            drawRay(495, 300, 500, 100); // TODO fix negative direction of ray


            GL11.glEnd();
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    // draws pixel at given coordinates
    private void drawPixel(int x, int y, Color color){
        val ndcX = (2.0f * x / 800.0f) - 1.0f;
        val ndcY = 1.0f - (2.0f * y / 600.0f);
        if(color != null)
            GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        GL11.glVertex2f(ndcX, ndcY);
    }

    // draws a ray from start to end coordinates
    private void drawRay(int startX, int startY, int endX, int endY, Color color){
        val xLength = endX - startX;
        val yLength = endY - startY;
        float xMulti = 1;
        float yMulti = 1;
        int usedLength;

        if(Math.abs(xLength) > Math.abs(yLength)){
            yMulti = (float)(endY - startY) / (float) (endX - startX);
            usedLength = xLength;
        }else{
            xMulti = (float)(endX - startX) / (float) (endY - startY);
            usedLength = yLength;
        }

        for(float i = 0; i <= usedLength; i ++){
            drawPixel((int)(startX + i * xMulti), (int)(startY + i * yMulti), color);
        }
    }

    private void drawRay(int startX, int startY, int endX, int endY) {
        drawRay(startX, startY, endX, endY, null);
    }



    public static void main(String[] args) {
        Window app = new Window();
        app.run();
    }
}
