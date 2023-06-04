package simulation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

/*
Autur: Jakub Frydrych
Lab: Wielowątkość
Data: 20.12.2022
Indeks: 263991
 */



public class MainFrame extends JFrame implements PropertyChangeListener,ActionListener, ChangeListener {

    private JLabel intensityLabel = new JLabel("Natezenie ruchu: ");
    private JSlider intensitySlider = new JSlider();

    private final JLabel eastLabel = new JLabel("Wschód");
    private final JLabel westLabel = new JLabel("Zachód");
    private final JSlider sideProportionSlider = new JSlider();
    private final JLabel atBridgeLabel = new JLabel("Na moście: ");
    private final JLabel queueLabel = new JLabel("  Kolejka: ");
    private final JSlider drivingIntensitySlider = new JSlider();
    private final JTextField atBridgeTextField = new JTextField();
    private final JTextField queueTextField = new JTextField();
    private final JScrollPane logPanel = new JScrollPane();
    private final JComboBox<?> typesOfSimulation;
    private final JTextArea logTextArea;

    private final NarrowBridge narrowBridge;

    public MainFrame(JTextArea logTextArea, NarrowBridge narrowBridge) {
       
        this.logTextArea = logTextArea;
        this.narrowBridge = narrowBridge;

        narrowBridge.addPropertyChangeListener(this);
        Font font=new Font(Font.MONOSPACED, Font.PLAIN, 16);
        setTitle("Symulacja przejazdu przez most");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550,800);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new FlowLayout(FlowLayout.CENTER));

        intensityLabel.setFont(font);
        add(intensityLabel);

        typesOfSimulation = new JComboBox<>(SimulationTypes.values());
        typesOfSimulation.setFont(font);
        typesOfSimulation.setPreferredSize(new Dimension(200, 25));
        typesOfSimulation.addActionListener(this);
        this.add(typesOfSimulation);

        intensitySlider.setPreferredSize(new Dimension(480,40));
        intensitySlider.setMinimum(0);
        intensitySlider.setMaximum(3000);
        intensitySlider.setMajorTickSpacing(1500);
        intensitySlider.setPaintTicks(true);
        intensitySlider.setPaintLabels(true);
        intensitySlider.setValue(0);

        Hashtable<Integer,  JLabel> sliderLabels= new Hashtable<>();
        sliderLabels.put(0,new JLabel("Małe"));
        sliderLabels.put(3000,new JLabel("Duze"));

        intensitySlider.setLabelTable(sliderLabels);
        intensitySlider.addChangeListener(this);
        add(intensitySlider);

        sideProportionSlider.setMinimum(0);
        sideProportionSlider.setMaximum(100);
        sideProportionSlider.setValue(50);
        sideProportionSlider.setPaintLabels(true);
        sideProportionSlider.setPaintTicks(true);
        sideProportionSlider.setMajorTickSpacing(50);
        Hashtable<Integer, JLabel> sitesLabels = new Hashtable<>();
        sitesLabels.put(0, eastLabel);
        sitesLabels.put(100, westLabel);
        sitesLabels.put(50, new JLabel("1:1"));
        sideProportionSlider.setLabelTable(sitesLabels);
        sideProportionSlider.addChangeListener(this);
        sideProportionSlider.setPreferredSize(new Dimension(500, 40));
        add(sideProportionSlider);

        atBridgeLabel.setFont(font);
        add(atBridgeLabel);

        atBridgeTextField.setPreferredSize(new Dimension(380, 25));
        atBridgeTextField.setFont(font);
        add(atBridgeTextField);

        queueLabel.setFont(font);
        add(queueLabel);

        queueTextField.setPreferredSize(new Dimension(380,25));
        queueTextField.setFont(font);
        add(queueTextField);

        logPanel.setPreferredSize(new Dimension(500, 500));
        logPanel.setViewportView(logTextArea);
        logTextArea.setFont(font);
        logTextArea.setEditable(false);
        this.add(logPanel);

        setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == typesOfSimulation) {
            SimulationTypes option = (SimulationTypes) typesOfSimulation.getSelectedItem();
            new Thread(() -> {
                try {
                    narrowBridge.changeRule(option);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == drivingIntensitySlider) {
            narrowBridge.setDelay(drivingIntensitySlider.getValue());
        } else if (e.getSource() == sideProportionSlider) {
            narrowBridge.setWestProba(sideProportionSlider.getValue());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            String bussesOnBridgeMessage = narrowBridge.getBussesOnBridgeMessage();
            String waitingBusesStringMessage = narrowBridge.getWaitingBusesMessage();
            atBridgeTextField.setText(bussesOnBridgeMessage);
            queueTextField.setText(waitingBusesStringMessage);
            System.out.println(bussesOnBridgeMessage);
        });
    }
}
