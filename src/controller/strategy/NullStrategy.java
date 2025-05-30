package controller.strategy;

import java.awt.event.MouseEvent;

public class NullStrategy implements CanvasMouseStrategy {
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
}
