package view;

import enums.LabelShape;

import javax.swing.*;
import java.awt.*;

public class CustomLabelDialog extends JDialog {
    private JTextField labelNameField;
    private JRadioButton rectButton, ovalButton;
    private JButton colorButton;
    private JSpinner fontSizeSpinner;
    private JButton okButton, cancelButton;
    private Color chosenColor = Color.WHITE;
    private boolean confirmed = false;

    public CustomLabelDialog(Frame owner, String currentLabel, LabelShape currentShape, Color currentColor, int currentFontSize) {
        super(owner, "Customize Label Style", true);

        // 標籤名稱
        labelNameField = new JTextField(currentLabel, 20);

        // 標籤形狀：矩形與橢圓
        rectButton = new JRadioButton("Rectangle");
        ovalButton = new JRadioButton("Oval");
        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(rectButton);
        shapeGroup.add(ovalButton);
        if(currentShape == LabelShape.RECTANGLE){
            rectButton.setSelected(true);
        } else {
            ovalButton.setSelected(true);
        }

        // 背景色選擇：使用 JColorChooser
        colorButton = new JButton("Choose Color");
        colorButton.setBackground(currentColor);
        chosenColor = currentColor;
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Label Background Color", chosenColor);
            if(newColor != null){
                chosenColor = newColor;
                colorButton.setBackground(chosenColor);
            }
        });

        // 字體大小
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(currentFontSize, 8, 72, 1));

        // 按鈕 OK 與 Cancel
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        // 佈局使用 GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Label Name:"), gbc);
        gbc.gridx = 1;
        panel.add(labelNameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Label Shape:"), gbc);
        gbc.gridx = 1;
        JPanel shapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        shapePanel.add(rectButton);
        shapePanel.add(ovalButton);
        panel.add(shapePanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Background Color:"), gbc);
        gbc.gridx = 1;
        panel.add(colorButton, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Font Size:"), gbc);
        gbc.gridx = 1;
        panel.add(fontSizeSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed(){
        return confirmed;
    }

    public String getLabelName(){
        return labelNameField.getText();
    }

    public LabelShape getLabelShape(){
        return rectButton.isSelected() ? LabelShape.RECTANGLE : LabelShape.OVAL;
    }

    public Color getChosenColor(){
        return chosenColor;
    }

    public int getFontSize(){
        return (Integer) fontSizeSpinner.getValue();
    }
}
