package view;

import javax.swing.*;
import java.awt.*;

public class ToolPanel extends JPanel {
    private Mode currentMode = Mode.SELECT;
    private JButton selectBtn;
    private JButton rectBtn;
    private JButton ovalBtn;
    private JButton assocBtn;
    private JButton genBtn;
    private JButton compBtn;

    public ToolPanel() {
        setLayout(new GridLayout(6, 1));

        selectBtn = new JButton("Select");
        assocBtn  = new JButton("Assoc");
        genBtn    = new JButton("Gen");
        compBtn   = new JButton("Comp");
        rectBtn   = new JButton("Rect");
        ovalBtn   = new JButton("Oval");


        add(selectBtn);
        add(assocBtn);
        add(genBtn);
        add(compBtn);
        add(rectBtn);
        add(ovalBtn);


        selectBtn.addActionListener(e -> setMode(Mode.SELECT));
        assocBtn.addActionListener(e -> setMode(Mode.ASSOCIATION));
        genBtn.addActionListener(e -> setMode(Mode.GENERALIZATION));
        compBtn.addActionListener(e -> setMode(Mode.COMPOSITION));
        rectBtn.addActionListener(e -> setMode(Mode.RECT));
        ovalBtn.addActionListener(e -> setMode(Mode.OVAL));

    }

    private void setMode(Mode mode) {
        this.currentMode = mode;
        updateButtonColors();

        // 你也可在這裡改變按鈕顏色或其他提示
        System.out.println("Current Mode: " + currentMode);
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    private void updateButtonColors() {
        selectBtn.setBackground(null);
        assocBtn.setBackground(null);
        genBtn.setBackground(null);
        compBtn.setBackground(null);
        rectBtn.setBackground(null);
        ovalBtn.setBackground(null);


        switch (currentMode) {
            case SELECT:
                selectBtn.setBackground(Color.BLACK);
                break;
            case ASSOCIATION:
                assocBtn.setBackground(Color.BLACK);
                break;
            case GENERALIZATION:
                genBtn.setBackground(Color.BLACK);
                break;
            case COMPOSITION:
                compBtn.setBackground(Color.BLACK);
                break;
            case RECT:
                rectBtn.setBackground(Color.BLACK);
                break;
            case OVAL:
                ovalBtn.setBackground(Color.BLACK);
                break;
            default:
                // 其他模式按需處理
                break;
        }
    }

}
