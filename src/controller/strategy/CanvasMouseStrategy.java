package controller.strategy;
import java.awt.event.MouseEvent;

public interface CanvasMouseStrategy {
    void mousePressed(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseDragged(MouseEvent e);
    void mouseMoved(MouseEvent e);
}

