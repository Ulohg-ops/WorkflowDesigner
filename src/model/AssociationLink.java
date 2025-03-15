package model;

import java.awt.*;

public class AssociationLink extends LinkObject {

    public AssociationLink(BasicObject start, BasicObject end, Point startPort, Point endPort) {
        super(start, end, startPort, endPort);
    }

    @Override
    protected void drawDecoration(Graphics2D g2d) {
        int barb = 15;
        double phi = Math.toRadians(40);
        double theta = Math.atan2(endPort.y - startPort.y, endPort.x - startPort.x);
        double x1 = endPort.x - barb * Math.cos(theta + phi);
        double y1 = endPort.y - barb * Math.sin(theta + phi);
        double x2 = endPort.x - barb * Math.cos(theta - phi);
        double y2 = endPort.y - barb * Math.sin(theta - phi);

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(endPort.x, endPort.y);
        arrowHead.addPoint((int)x1, (int)y1);
        arrowHead.addPoint((int)x2, (int)y2);
        g2d.fill(arrowHead);
    }
}
