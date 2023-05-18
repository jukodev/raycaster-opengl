import lombok.Getter;
import lombok.val;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Player {
    @Getter
    private float xPos = 120, yPos = 120;
    @Getter
    private float deltaX, deltaY, angle;
    private final int[] map;

    public Player(long windowIndex, int[] map){

        registerKeyCallBacks(windowIndex);
        this.map = map;
        deltaX = (float) (Math.cos(angle) * 5);
        deltaY = (float) (Math.sin(angle) * 5);
    }

    private void registerKeyCallBacks(long windowIndex){

        GLFW.glfwSetKeyCallback(windowIndex, (window, key, scancode, action, mods) -> {
            if(action != GLFW.GLFW_REPEAT && action != GLFW.GLFW_PRESS) return;
            val prevX = xPos;
            val prevY = yPos;
            switch (key) {
                case GLFW.GLFW_KEY_W -> {xPos += deltaX; yPos += deltaY;}
                case GLFW.GLFW_KEY_A -> rotate(-1);
                case GLFW.GLFW_KEY_D -> rotate(1);
                case GLFW.GLFW_KEY_S -> {xPos -= deltaX; yPos -= deltaY;}
            }
            if(!checkMove(xPos, yPos)){
                xPos = prevX;
                yPos = prevY;
            }
        });
    }

    private void rotate(int direction){
        angle += .1f  * direction;
        if(angle < 0) angle += 2* Math.PI;
        if(angle > 2* Math.PI) angle -= 2* Math.PI;
        deltaX = (float) (Math.cos(angle) * 5);
        deltaY = (float) (Math.sin(angle) * 5);
    }

    public void draw(){
        val ndcX = Window.getNormalX(xPos);
        val ndcY = Window.getNormalY(yPos);
        GL11.glPointSize(8);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3f(1,1,0);
        GL11.glVertex2f(ndcX, ndcY);
        GL11.glEnd();

        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(Window.getNormalX(xPos), Window.getNormalY(yPos));
        GL11.glVertex2f(Window.getNormalX(xPos + deltaX * 5), Window.getNormalY(yPos + deltaY * 5));
        GL11.glEnd();


    }

    private boolean checkMove(float x, float y){
        val _x = (int)(x / 64);
        val _y = (int)(y / 64);
        return map[_y*8+_x] == 0;
    }
}
