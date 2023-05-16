import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.awt.Color;

@AllArgsConstructor
@Getter
public class Ray   {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final Color color;
    public Ray(int startX, int startY, int endX, int endY){
        this(startX, startY, endX, endY, null);
    }
}
