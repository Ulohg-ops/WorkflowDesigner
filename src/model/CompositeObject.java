package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompositeObject extends BasicObject {
    private List<BasicObject> children = new ArrayList<>();

    public CompositeObject(List<BasicObject> children) {
        // 初步以第一個物件的座標與尺寸初始化，
        // 之後呼叫 updateBounds() 重新計算群組範圍
        super(0, 0, 0, 0);
        this.children.addAll(children);
        updateBounds();
        // 被群組時，預設不顯示連接埠
        this.showPorts = false;
    }

    // 依據 children 重新計算 bounding box
    private void updateBounds() {
        if (children.isEmpty()) {
            x = y = width = height = 0;
            return;
        }
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (BasicObject child : children) {
            minX = Math.min(minX, child.x);
            minY = Math.min(minY, child.y);
            maxX = Math.max(maxX, child.x + child.width);
            maxY = Math.max(maxY, child.y + child.height);
        }
        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
    }

    public List<BasicObject> getChildren() {
        return children;
    }

    @Override
    public void draw(Graphics g) {
        // 繪製 composite 物件的邊框（可視需求加上底色等）
        g.setColor(Color.MAGENTA);
        g.drawRect(x, y, width, height);
        // 同時繪製各子物件（注意：子物件的座標是全域座標）
        for (BasicObject child : children) {
            // 暫時保存原本的 showPorts 狀態
            boolean originalShowPorts = child.isShowPorts();
            // 強制關閉子物件的 port 顯示
            child.setShowPorts(false);
            child.draw(g);
            // 還原原來的狀態
            child.setShowPorts(originalShowPorts);
        }
        // 如果 composite 自身被選取 (showPorts 為 true) 才顯示自己的連接埠
        if (showPorts) {
            Point[] ports = getPorts();
            g.setColor(Color.BLACK);
            for (Point pt : ports) {
                g.fillRect(pt.x - 3, pt.y - 3, 6, 6);
            }
        }
    }

    // 移動 composite 物件：同時移動所有子物件，並重新計算邊界
    public void moveBy(int dx, int dy) {
        for (BasicObject child : children) {
            child.x += dx;
            child.y += dy;
        }
        updateBounds();
    }

    @Override
    public Point[] getPorts() {
        // 以 bounding box 的四個中點作為連接埠
        return new Point[] {
                new Point(x + width/2, y),          // 上
                new Point(x + width/2, y + height),   // 下
                new Point(x, y + height/2),           // 左
                new Point(x + width, y + height/2)    // 右
        };
    }

    @Override
    public Point getClosestPort(Point p) {
        Point[] ports = getPorts();
        Point closest = ports[0];
        double minDist = p.distance(ports[0]);
        for (int i = 1; i < ports.length; i++) {
            double dist = p.distance(ports[i]);
            if (dist < minDist) {
                minDist = dist;
                closest = ports[i];
            }
        }
        return closest;
    }
}
