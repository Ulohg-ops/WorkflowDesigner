package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel {
    private ToolPanel toolPanel;
    private List<BasicObject> objects = new ArrayList<>();
    private List<LinkObject> links = new ArrayList<>();

    // 連線相關變數（用於 Association/Generalization/Composition 模式）
    private boolean isLinkDragging = false;
    private BasicObject linkStartObject = null;
    private Point linkStartPoint = null;
    private Point currentDragPoint = null;

    // 選取與移動相關變數（Select 模式下）
    private Point selectionStart = null;
    private Point selectionEnd = null;
    private BasicObject draggingObject = null; // 正在移動的物件
    private Point dragOffset = null;           // 滑鼠點擊位置與物件左上角的偏移量

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 60;

    public Canvas(ToolPanel toolPanel) {
        this.toolPanel = toolPanel;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if (mode == Mode.RECT) {
                    // 建立矩形物件
                    objects.add(new RectObject(e.getX(), e.getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
                    repaint();
                } else if (mode == Mode.OVAL) {
                    // 建立橢圓物件
                    objects.add(new OvalObject(e.getX(), e.getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
                    repaint();
                } else if (mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) {
                    // 嘗試開始拉線
                    BasicObject startObj = findObjectAt(e.getPoint());
                    if (startObj != null) {
                        linkStartObject = startObj;
                        linkStartPoint = startObj.getClosestPort(e.getPoint());
                        isLinkDragging = true;
                        currentDragPoint = e.getPoint();
                    }
                } else if (mode == Mode.SELECT) {
                    BasicObject clickedObj = findObjectAt(e.getPoint());
                    if (clickedObj != null) {
                        // 點選物件：取消其他物件選取，並標記此物件被選取，同時開始移動
                        for (BasicObject obj : objects) {
                            obj.setShowPorts(false);
                        }
                        clickedObj.setShowPorts(true);
                        draggingObject = clickedObj;
                        dragOffset = new Point(e.getX() - clickedObj.x, e.getY() - clickedObj.y);
                        // 清除選取區域
                        selectionStart = null;
                        selectionEnd = null;
                        repaint();
                    } else {
                        // 點選空白處：開始區域選取，取消所有物件選取
                        selectionStart = e.getPoint();
                        selectionEnd = e.getPoint();
                        draggingObject = null;
                        for (BasicObject obj : objects) {
                            obj.setShowPorts(false);
                        }
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if ((mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) && isLinkDragging) {
                    // 拉線結束
                    BasicObject endObj = findObjectAt(e.getPoint());
                    if (endObj != null && endObj != linkStartObject) {
                        Point endPort = endObj.getClosestPort(e.getPoint());
                        LinkObject link = new LinkObject(linkStartObject, endObj, linkStartPoint, endPort, mode);
                        links.add(link);
                    }
                    isLinkDragging = false;
                    linkStartObject = null;
                    linkStartPoint = null;
                    currentDragPoint = null;
                    repaint();
                } else if (mode == Mode.SELECT) {
                    if (draggingObject != null) {
                        // 移動完成，更新連接到該物件的所有連線
                        updateLinksForObject(draggingObject);
                        draggingObject = null;
                        dragOffset = null;
                        repaint();
                    } else if (selectionStart != null && selectionEnd != null) {
                        // 結束拖曳選取區域
                        Rectangle selectionRect = new Rectangle(
                                Math.min(selectionStart.x, selectionEnd.x),
                                Math.min(selectionStart.y, selectionEnd.y),
                                Math.abs(selectionStart.x - selectionEnd.x),
                                Math.abs(selectionStart.y - selectionEnd.y)
                        );
                        boolean anySelected = false;
                        for (BasicObject obj : objects) {
                            Rectangle objRect = new Rectangle(obj.x, obj.y, obj.width, obj.height);
                            if (selectionRect.contains(objRect)) {
                                obj.setShowPorts(true);
                                anySelected = true;
                            } else {
                                obj.setShowPorts(false);
                            }
                        }
                        if (!anySelected) {
                            for (BasicObject obj : objects) {
                                obj.setShowPorts(false);
                            }
                        }
                        selectionStart = null;
                        selectionEnd = null;
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if ((mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) && isLinkDragging) {
                    currentDragPoint = e.getPoint();
                    repaint(); // 顯示橡皮筋線
                } else if (mode == Mode.SELECT) {
                    if (draggingObject != null) {
                        // 移動物件：根據滑鼠位置更新物件位置
                        int newX = e.getX() - dragOffset.x;
                        int newY = e.getY() - dragOffset.y;
                        draggingObject.x = newX;
                        draggingObject.y = newY;
                        repaint();
                    } else if (selectionStart != null) {
                        // 更新選取矩形區域
                        selectionEnd = e.getPoint();
                        repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Mode mode = toolPanel.getCurrentMode();
                if (mode == Mode.ASSOCIATION || mode == Mode.GENERALIZATION || mode == Mode.COMPOSITION) {
                    BasicObject hovered = findObjectAt(e.getPoint());
                    for (BasicObject obj : objects) {
                        obj.setShowPorts(obj == hovered);
                    }
                    repaint();
                }
            }
        });
    }

    // 當物件移動後，更新所有連線中連接該物件的連接埠
    private void updateLinksForObject(BasicObject movedObj) {
        // 以物件中心作為參考點重新計算連線端點
        Point center = new Point(movedObj.x + movedObj.width / 2, movedObj.y + movedObj.height / 2);
        for (LinkObject link : links) {
            if (link.getStartObject() == movedObj) {
                link.setStartPort(movedObj.getClosestPort(center));
            }
            if (link.getEndObject() == movedObj) {
                link.setEndPort(movedObj.getClosestPort(center));
            }
        }
    }


    // 從最上層（最後加入）開始檢查點 p 是否落在某個物件上
    private BasicObject findObjectAt(Point p) {
        for (int i = objects.size() - 1; i >= 0; i--) {
            BasicObject obj = objects.get(i);
            if (obj.contains(p)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 繪製所有物件
        for (BasicObject obj : objects) {
            obj.draw(g);
        }
        // 繪製所有連線
        for (LinkObject link : links) {
            link.draw(g);
        }
        // 畫拉線（橡皮筋線）：連線模式下使用
        if (isLinkDragging && linkStartPoint != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(linkStartPoint.x, linkStartPoint.y, currentDragPoint.x, currentDragPoint.y);
        }
        // 畫選取矩形：選取模式下使用
        if (toolPanel.getCurrentMode() == Mode.SELECT && selectionStart != null && selectionEnd != null) {
            g.setColor(Color.BLUE);
            int x = Math.min(selectionStart.x, selectionEnd.x);
            int y = Math.min(selectionStart.y, selectionEnd.y);
            int w = Math.abs(selectionStart.x - selectionEnd.x);
            int h = Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x, y, w, h);
        }
    }
}
