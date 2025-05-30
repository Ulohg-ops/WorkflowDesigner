package controller.strategy;

import java.awt.event.MouseEvent;
import controller.CanvasController;

public class SelectModeStrategy implements CanvasMouseStrategy {
    private final CanvasController controller;

    public SelectModeStrategy(CanvasController controller) {
        this.controller = controller;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        controller.handleSelectPressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        controller.handleSelectReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        controller.handleSelectDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
