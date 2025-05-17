package Modello;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_NuovoVolo extends JFrame {
    private JPanel mainpanel;
    private JButton partenzaButton;
    private JButton arrivoButton;


    public GUI_NuovoVolo() {
        setContentPane(mainpanel);
        setTitle("Nuovo Volo");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        partenzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_Partenza();
                dispose();
            }
        });


        arrivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_Arrivo();
                dispose();
            }
        });
    }
}
