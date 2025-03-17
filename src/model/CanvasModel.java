package model;

import java.util.ArrayList;
import java.util.List;


public class CanvasModel {
    private List<BasicObject> objects;
    private List<LinkObject> links;
    private List<BasicObject> selectedObjects;

    public CanvasModel() {
        this.objects = new ArrayList<>();
        this.links = new ArrayList<>();
        this.selectedObjects = new ArrayList<>();
    }
    public List<BasicObject> getObjects() {
        return objects;
    }
    public List<LinkObject> getLinks() {
        return links;
    }
    public List<BasicObject> getSelectedObjects() {
        return selectedObjects;
    }

}
