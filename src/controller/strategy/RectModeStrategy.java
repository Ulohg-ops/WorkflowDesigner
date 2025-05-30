package controller.strategy;

import java.awt.event.MouseEvent;
import controller.CanvasController;
import model.RectObject;
import view.Canvas;

public class RectModeStrategy implements CanvasMouseStrategy {
    private final CanvasController controller;

    public RectModeStrategy(CanvasController controller) {
        this.controller = controller;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        controller.getModel().getObjects().add(new RectObject(
            e.getX(), e.getY(), Canvas.DEFAULT_WIDTH, Canvas.DEFAULT_HEIGHT
        ));
        controller.getCanvas().repaint();
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
}
