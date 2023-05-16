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

        window = GLFW.glfwCreateWindow(800, 600, "RayCaster", 0, 0);
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

            drawRay(new Ray(100, 100, 120, 400));
            drawRay(new Ray(100, 100, 500, 220));
            drawRay(new Ray(120, 400, 495, 300));
            drawRay(new Ray(495, 300, 500,  220));


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




    public static void main(String[] args) {
        Window app = new Window();
        app.run();
    }
}
