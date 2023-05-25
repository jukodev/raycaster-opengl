import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class Statistics extends JFrame {
    private final Label fpsLabel;
    private final Label renderedPixels;

    private final int[] fps = new int[10];
    private int fpsIndex;

    @Setter
    private long deltaTime;
    @Setter
    private int pixelCount;

    public Statistics(){
        setSize(200, 80);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GridLayout grid = new GridLayout(2,2);
        Label fpsHeader = new Label("FPS: ");
        Label renderedPixelsHeader = new Label("Pixels drawn: ");
        fpsLabel = new Label("0");
        renderedPixels = new Label("0");
        setLayout(grid);
        add(fpsHeader);
        add(fpsLabel);
        add(renderedPixelsHeader);
        add(renderedPixels);
    }

    public void update(){
        if(deltaTime != 0){
            fps[fpsIndex] = (int) (1000 / deltaTime);
            fpsIndex = (fpsIndex + 1) % 10;
            IntStream fpsStream = IntStream.of(fps);
            fpsLabel.setText(String.valueOf(fpsStream.sum() / 10));
        }
        renderedPixels.setText(String.valueOf(pixelCount));
    }
}
