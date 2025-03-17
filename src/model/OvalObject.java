package model;

import view.LabelShape;

import java.awt.*;

public class OvalObject extends BasicObject {

    public OvalObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        // 繪製橢圓
        g.setColor(Color.decode("#F6F0F0"));
        g.fillOval(x, y, width, height);

        g.setColor(Color.BLACK);
        g.drawOval(x, y, width, height);

        // 若要顯示 ports，畫出 4 個小方塊
        if (showPorts) {
            g.setColor(Color.BLACK);
            Point[] ports = getPorts();
            for (Point pt : ports) {
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }
        // 繪製 label
        if (!label.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            g.setColor(labelColor);
            // 根據 labelShape 繪製不同背景
            if (labelShape == LabelShape.RECTANGLE) {
                g.fillRect(x, y - fontSize, g.getFontMetrics().stringWidth(label), fontSize);
            } else if (labelShape == LabelShape.OVAL) {
                g.fillOval(x, y - fontSize, g.getFontMetrics().stringWidth(label), fontSize);
            }
            g.setColor(Color.BLACK);
            g.drawString(label, x, y - 2);
        }


    }

    @Override
    public Point[] getPorts() {
        // 4 個連接埠：上、下、左、右
        return new Point[] {
                new Point(x + width/2, y),             // 上
                new Point(x + width/2, y + height),    // 下
                new Point(x, y + height/2),            // 左
                new Point(x + width, y + height/2)     // 右
        };
    }
}
