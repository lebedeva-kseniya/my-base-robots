package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

public class RobotCoordinatesWindow extends JInternalFrame implements PropertyChangeListener {
    private final RobotModel m_model;
    private final JLabel xCoordLabel;
    private final JLabel yCoordLabel;
    private final JLabel directionLabel;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        m_model = model;
        m_model.addPropertyChangeListener(this);

        setSize(250, 120);
        setLocation(320, 10);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Текущие координаты робота:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(new JLabel("X:"), gbc);

        gbc.gridx = 1;
        xCoordLabel = new JLabel(df.format(m_model.getRobotPositionX()));
        xCoordLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mainPanel.add(xCoordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Y:"), gbc);

        gbc.gridx = 1;
        yCoordLabel = new JLabel(df.format(m_model.getRobotPositionY()));
        yCoordLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mainPanel.add(yCoordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Направление:"), gbc);

        gbc.gridx = 1;
        directionLabel = new JLabel(df.format(Math.toDegrees(m_model.getRobotDirection())) + "°");
        directionLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mainPanel.add(directionLabel, gbc);

        getContentPane().add(mainPanel);
        pack();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            switch (evt.getPropertyName()) {
                case "positionX":
                    xCoordLabel.setText(df.format(m_model.getRobotPositionX()));
                    break;
                case "positionY":
                    yCoordLabel.setText(df.format(m_model.getRobotPositionY()));
                    break;
                case "direction":
                    directionLabel.setText(df.format(Math.toDegrees(m_model.getRobotDirection())) + "°");
                    break;
            }
        });
    }
}