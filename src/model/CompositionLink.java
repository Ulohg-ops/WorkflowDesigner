package model;

import java.awt.*;

public class CompositionLink extends LinkObject {

    public CompositionLink(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super(start, end, startPort, endPort);
    }

    @Override
    protected void drawDecoration(Graphics2D g2d) {
        int diamondLength = 15;
        int diamondWidth = 10;
        double dx = endPort.x - startPort.x;
        double dy = endPort.y - startPort.y;
        double len = Math.sqrt(dx * dx + dy * dy);
        double ux = dx / len;
        double uy = dy / len;
        int bx = endPort.x - (int)(diamondLength * ux);
        int by = endPort.y - (int)(diamondLength * uy);
        double px = -uy;
        double py = ux;
        int rx = bx + (int)(diamondWidth / 2 * px);
        int ry = by + (int)(diamondWidth / 2 * py);
        int lx = bx - (int)(diamondWidth / 2 * px);
        int ly = by - (int)(diamondWidth / 2 * py);

        Polygon diamond = new Polygon();
        diamond.addPoint(endPort.x, endPort.y);
        diamond.addPoint(rx, ry);
        diamond.addPoint(bx, by);
        diamond.addPoint(lx, ly);
        g2d.draw(diamond);
    }
}
