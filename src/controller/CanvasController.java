package controller;

import model.*;
import view.Canvas;
import enums.Mode;
import view.ToolPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.strategy.CanvasMouseStrategy;
import controller.strategy.SelectModeStrategy;
import controller.strategy.AssociationModeStrategy;
import controller.strategy.GeneralizationModeStrategy;
import controller.strategy.CompositionModeStrategy;
import controller.strategy.RectModeStrategy;
import controller.strategy.OvalModeStrategy;
import controller.strategy.NullStrategy;

// 原本所有邏輯如 startLinkDragging, handleSelectPressed，都由 CanvasController 持有並管理。
// 但套用 strategy pattern 每個 mode 拆成一個 class，才符合 SRP
public class CanvasController extends MouseAdapter implements MouseMotionListener {
    private ToolPanel toolPanel;
    private CanvasModel model;
    private Canvas canvas;

    private boolean isLinkDragging = false;
    private BasicObject linkStartObject = null;
    private Point linkStartPoint = null;
    private Point currentDragPoint = null;

    private Point selectionStart = null;
    private Point selectionEnd = null;
    private boolean isGroupDragging = false;
    private Point groupDragStartPoint = null;
    private Map<BasicObject, Point> initialPositions = new HashMap<>();

    private Map<Mode, CanvasMouseStrategy> strategyMap = new HashMap<>();
    private CanvasMouseStrategy currentStrategy = new NullStrategy();

    public CanvasController(ToolPanel toolPanel, CanvasModel model, Canvas canvas) {
        this.toolPanel = toolPanel;
        this.model = CanvasModel.getInstance();
        this.canvas = canvas;
        initStrategies();
    }

    private void initStrategies() {
    	// 每新增一種 case 就要加進去
    	strategyMap.put(Mode.SELECT, new SelectModeStrategy(this));
        strategyMap.put(Mode.ASSOCIATION, new AssociationModeStrategy(this));
        strategyMap.put(Mode.GENERALIZATION, new GeneralizationModeStrategy(this));
        strategyMap.put(Mode.COMPOSITION, new CompositionModeStrategy(this));
        strategyMap.put(Mode.RECT, new RectModeStrategy(this));
        strategyMap.put(Mode.OVAL, new OvalModeStrategy(this));
    }

    private void updateCurrentStrategy() {
        Mode mode = toolPanel.getCurrentMode();
        this.currentStrategy = strategyMap.getOrDefault(mode, new NullStrategy());
    }

    @Override
    public void mousePressed(MouseEvent e) {
// 		原本每新增一個 mode 都要新增一個 case 而且是 所有 event 都要寫
//    	public void mousePressed(MouseEvent e) {
//    	    Mode mode = toolPanel.getCurrentMode();
//    	    switch (mode) {
//    	        case RECT: ...
//    	        case ASSOCIATION: ...
//    	        case SELECT: ...
//    	    }
//    	}
        updateCurrentStrategy();
        currentStrategy.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        updateCurrentStrategy();
        currentStrategy.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateCurrentStrategy();
        currentStrategy.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateCurrentStrategy();
        currentStrategy.mouseMoved(e);
    }

    public void startLinkDragging(MouseEvent e) {
        BasicObject startObj = findObjectAt(e.getPoint());
        startObj = resolveConnectableObject(startObj);
        if (startObj == null) return;

        linkStartObject = startObj;
        linkStartPoint = startObj.getClosestPort(e.getPoint());
        isLinkDragging = true;
        currentDragPoint = e.getPoint();
    }

    public void endLinkDragging(MouseEvent e) {
        if (!isLinkDragging) return;

        BasicObject endObj = findObjectAt(e.getPoint());
        endObj = resolveConnectableObject(endObj);
        if (endObj == null || endObj == linkStartObject) {
            cleanupLinkDragging();
            return;
        }

        Point endPort = endObj.getClosestPort(e.getPoint());
        Mode mode = toolPanel.getCurrentMode();
        LinkObject link = null;
        switch (mode) {
            case ASSOCIATION:
                link = new AssociationLink(linkStartObject, endObj, linkStartPoint, endPort);
                break;
            case GENERALIZATION:
                link = new GeneralizationLink(linkStartObject, endObj, linkStartPoint, endPort);
                break;
            case COMPOSITION:
                link = new CompositionLink(linkStartObject, endObj, linkStartPoint, endPort);
                break;
            default:
                break;
        }
        if (link != null) model.getLinks().add(link);
        cleanupLinkDragging();
    }

    public void cleanupLinkDragging() {
        isLinkDragging = false;
        linkStartObject = null;
        linkStartPoint = null;
        currentDragPoint = null;
        canvas.repaint();
    }

    public boolean isLinkDragging() {
        return isLinkDragging;
    }

    public void setCurrentDragPoint(Point p) {
        this.currentDragPoint = p;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public CanvasModel getModel() {
        return model;
    }

    /*
     *    友善設計 :D 拖曳線的時候 hover 會顯示 port 
     */
    public void handleHoveringObjectPort(MouseEvent e) {
        BasicObject hovered = findObjectAt(e.getPoint());
        for (BasicObject obj : model.getObjects()) {
            obj.setShowPorts(obj == hovered);
        }
        canvas.repaint();
    }

    /*
     *     有點到物件：處理單一選取或群組拖曳準備
     *     沒點到物件 : 開始建立 selected Area 
     */
    public void handleSelectPressed(MouseEvent e) {
        BasicObject clickedObj = findObjectAt(e.getPoint());
        List<BasicObject> selectedObjects = model.getSelectedObjects();

        if (clickedObj != null) {
            if (selectedObjects.contains(clickedObj)) {
                isGroupDragging = true;
                groupDragStartPoint = e.getPoint();
                initialPositions.clear();
                for (BasicObject obj : selectedObjects) {
                    initialPositions.put(obj, new Point(obj.getX(), obj.getY()));
                }
            } else {
                selectedObjects.clear();
                for (BasicObject obj : model.getObjects()) {
                    obj.setShowPorts(false);
                }
                selectedObjects.add(clickedObj);
                clickedObj.setShowPorts(true);

                isGroupDragging = true;
                groupDragStartPoint = e.getPoint();
                initialPositions.clear();
                initialPositions.put(clickedObj, new Point(clickedObj.getX(), clickedObj.getY()));
            }
            selectionStart = null;
            selectionEnd = null;
            canvas.repaint();
        } else {
            for (BasicObject obj : model.getObjects()) {
                obj.setShowPorts(false);
            }
            selectedObjects.clear();
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    public void handleSelectReleased(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            isGroupDragging = false;
            initialPositions.clear();
            canvas.repaint();
        } else if (selectionStart != null && selectionEnd != null) {
        	// selected Area 內所有物件都顯示 port
            Rectangle selectionRect = new Rectangle(
                Math.min(selectionStart.x, selectionEnd.x),
                Math.min(selectionStart.y, selectionEnd.y),
                Math.abs(selectionStart.x - selectionEnd.x),
                Math.abs(selectionStart.y - selectionEnd.y)
            );
            selectedObjects.clear();
            for (BasicObject obj : model.getObjects()) {
                Rectangle objRect = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                if (selectionRect.contains(objRect)) {
                    obj.setShowPorts(true);
                    selectedObjects.add(obj);
                } else {
                    obj.setShowPorts(false);
                }
            }
            selectionStart = null;
            selectionEnd = null;
            for (LinkObject link : model.getLinks(	)) {
                link.reCalcDepth();
            }
            canvas.repaint();
        }
    }

    public void handleSelectDragged(MouseEvent e) {
        List<BasicObject> selectedObjects = model.getSelectedObjects();
        if (isGroupDragging) {
            int deltaX = e.getX() - groupDragStartPoint.x;
            int deltaY = e.getY() - groupDragStartPoint.y;

            for (BasicObject obj : selectedObjects) {
                if (obj instanceof CompositeObject) {
                    ((CompositeObject) obj).moveBy(deltaX, deltaY);
                } else {
                    obj.setX(obj.getX() + deltaX);
                    obj.setY(obj.getY() + deltaY);
                }
            }
            groupDragStartPoint = e.getPoint();
            canvas.repaint();
        } else if (selectionStart != null) {
            selectionEnd = e.getPoint();
            canvas.repaint();
        }
    }

    public void groupSelectedObjects() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() >= 2) {
            List<BasicObject> newChildren = new ArrayList<>(selected);
            model.getObjects().removeAll(selected);

            CompositeObject composite = new CompositeObject(newChildren);
            model.getObjects().add(composite);

            selected.clear();
            composite.setShowPorts(true);
            selected.add(composite);

            canvas.repaint();
        }
    }

    public void ungroupSelectedObject() {
        List<BasicObject> selected = model.getSelectedObjects();
        if (selected.size() == 1 && selected.get(0).isGroup()) {
            BasicObject group = selected.get(0);
            model.getObjects().remove(group);
            
            List<BasicObject> children = new ArrayList<>();
            group.ungroupTo(children);  // dynamic dispatch : 會去呼叫 compositeObject 的 ungroupTo
            model.getObjects().addAll(children);

            selected.clear();
            for (BasicObject child : children) {
                child.setShowPorts(true);
                selected.add(child);
            }

            canvas.repaint();
        }
    }

    public void drawAdditionalGuides(Graphics g) {
        if (isLinkDragging && linkStartPoint != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(linkStartPoint.x, linkStartPoint.y, currentDragPoint.x, currentDragPoint.y);
        }
        if (toolPanel.getCurrentMode() == Mode.SELECT && selectionStart != null && selectionEnd != null) {
            g.setColor(Color.BLUE);
            int x = Math.min(selectionStart.x, selectionEnd.x);
            int y = Math.min(selectionStart.y, selectionEnd.y);
            int w = Math.abs(selectionStart.x - selectionEnd.x);
            int h = Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x, y, w, h);
        }
    }

    private BasicObject resolveConnectableObject(BasicObject obj) {
        if (obj instanceof CompositeObject) return null;
        return obj;
    }

    private BasicObject findObjectAt(Point p) {
        List<BasicObject> hitObjects = new ArrayList<>();
        for (BasicObject obj : model.getObjects()) {
            if (obj.contains(p)) {
                hitObjects.add(obj);
            }
        }
        if (hitObjects.isEmpty()) return null;
        hitObjects.sort((o1, o2) -> Integer.compare(o1.getDepth(), o2.getDepth()));
        return hitObjects.get(0);
    }

}
