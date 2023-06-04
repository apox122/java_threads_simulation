package simulation;

import javax.swing.*;
/*
Autur: Jakub Frydrych
Lab: Wielowątkość
Data: 20.12.2022
Indeks: 263991
 */


public class MainStart {
    public static void main(String[] args) {
        JTextArea logs = new JTextArea();
        NarrowBridge narrowBridge= new NarrowBridge(logs);
        SwingUtilities.invokeLater(() -> new MainFrame(logs, narrowBridge));
        new Thread(narrowBridge).start();
    }
}
