import lombok.Getter;
import lombok.val;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Player {
    @Getter
    private float xPos = 120, yPos = 120;
    @Getter
    private float deltaX, deltaY, angle;

    private double prevMouseX;
    private final int[] map;

    private int moveVert = 0, moveHor = 0;

    private long lastFrameTime;

    private final RayCaster rayCaster;

    public Player(long windowIndex, int[] map){
        registerKeyCallBacks(windowIndex);
        registerMouseCallbacks(windowIndex);
        this.map = map;
        deltaX = (float) (Math.cos(angle) * 5);
        deltaY = (float) (Math.sin(angle) * 5);
        rayCaster = new RayCaster(map);
    }

    // register movement key callbacks
    private void registerKeyCallBacks(long windowIndex){
        GLFW.glfwSetKeyCallback(windowIndex, (window, key, scancode, action, mods) -> {
            if(action == GLFW.GLFW_PRESS){
                switch (key){
                    case GLFW.GLFW_KEY_UP -> RayCaster.RES_SCALE = RayCaster.RES_SCALE < 8 ? RayCaster.RES_SCALE * 2 : 8;
                    case GLFW.GLFW_KEY_DOWN -> RayCaster.RES_SCALE = RayCaster.RES_SCALE > 1 ? RayCaster.RES_SCALE / 2 : 1;
                }
            }

            if(action != GLFW.GLFW_RELEASE && action != GLFW.GLFW_PRESS) return;
            val pressed = action == GLFW.GLFW_PRESS ? 1 : 0;

            switch (key) {
                case GLFW.GLFW_KEY_W -> moveHor = pressed;
                case GLFW.GLFW_KEY_A -> moveVert = pressed; // orthogonal vector
                case GLFW.GLFW_KEY_D -> moveVert = -pressed;
                case GLFW.GLFW_KEY_S -> moveHor = -pressed;
                case GLFW.GLFW_KEY_ESCAPE -> GLFW.glfwSetWindowShouldClose(windowIndex, true);
            }
        });
    }

    // register callbacks needed to rotate the player with the mouse
    private void registerMouseCallbacks(long windowIndex) {
        GLFW.glfwSetCursorPosCallback(windowIndex, (window, xpos, ypos) -> {
            if (prevMouseX != -1) {
                double dx = xpos - prevMouseX;
                if (dx != 0) {
                    angle += dx * 0.01;  // Adjust the rotation speed as needed
                    deltaX = (float) (Math.cos(angle) * 5);
                    deltaY = (float) (Math.sin(angle) * 5);
                }
            }
            prevMouseX = xpos;
        });

        GLFW.glfwSetCursorEnterCallback(windowIndex, (window, entered) -> {
            if (entered) {
                prevMouseX = 768;
                GLFW.glfwSetCursorPos(window, prevMouseX, 256);
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            } else {
                prevMouseX = -1;
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }
        });
    }

    private void movePlayer(){
        val prevX = xPos;
        val prevY = yPos;
        float deltaTime = (System.currentTimeMillis() - lastFrameTime) / 100f;
        lastFrameTime = System.currentTimeMillis();

        if(moveVert != 0){
            xPos += moveVert * deltaY * deltaTime;
            yPos -= moveVert * deltaX * deltaTime;
        }
        if(moveHor != 0){
            xPos += moveHor * deltaX * deltaTime;
            yPos += moveHor * deltaY * deltaTime;
        }

        if(!checkMove(xPos, yPos)){
            xPos = prevX;
            yPos = prevY;
        }
    }

    public void draw(){
        movePlayer();
        rayCaster.draw(xPos, yPos, angle); // draw the "3D render"

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

    // check for possible collisions with walls
    private boolean checkMove(float x, float y){
        val _x = (int)(x / 64);
        val _y = (int)(y / 64);
        return map[_y*8+_x] == 0;
    }
}
