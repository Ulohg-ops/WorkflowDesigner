package controller.strategy;

import controller.CanvasController;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class CompositionModeStrategy implements CanvasMouseStrategy {
    private final CanvasController controller;

    public CompositionModeStrategy(CanvasController controller) {
        this.controller = controller;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        controller.startLinkDragging(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        controller.endLinkDragging(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (controller.isLinkDragging()) {
            controller.setCurrentDragPoint(e.getPoint());
            // 終點會隨著滑鼠移動更新
            controller.getCanvas().repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        controller.handleHoveringObjectPort(e);
    }
}
