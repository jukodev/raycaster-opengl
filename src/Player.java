import lombok.Getter;
import lombok.val;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Player {
    @Getter
    private float xPos = 130, yPos = 130;
    private int[] map;

    public Player(long windowIndex, int[] map){

        registerKeyCallBacks(windowIndex);
        this.map = map;
    }

    private void registerKeyCallBacks(long windowIndex){

        GLFW.glfwSetKeyCallback(windowIndex, (window, key, scancode, action, mods) -> {
            if(action != GLFW.GLFW_REPEAT && action != GLFW.GLFW_PRESS) return;
            val prevX = xPos;
            val prevY = yPos;
            switch (key) {
                case GLFW.GLFW_KEY_W -> yPos -= 5;
                case GLFW.GLFW_KEY_A -> xPos -= 5;
                case GLFW.GLFW_KEY_S -> yPos += 5;
                case GLFW.GLFW_KEY_D -> xPos += 5;
            }
            if(!checkMove(xPos, yPos)){
                xPos = prevX;
                yPos = prevY;
            }
        });
    }

    public void draw(){
        val ndcX = (2.0f * xPos / 800.0f) - 1.0f;
        val ndcY = 1.0f - (2.0f * yPos / 600.0f);
        GL11.glPointSize(8);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3f(1,1,0);
        GL11.glVertex2f(ndcX, ndcY);
        GL11.glEnd();
    }

    private boolean checkMove(float x, float y){
        val _x = (int)(x / 64);
        val _y = (int)(y / 64);
        System.out.println(x + " " + y );
        return map[_y*8+_x] == 0;
    }
}
