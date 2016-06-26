package ui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class QFDPanel extends FeaturePanel{
	public QFDPanel() {
		super.setLayout(new FlowLayout());
		super.setBorder(BorderFactory.createTitledBorder("Additional Parameters Distance"));
        JLabel evLabel = new JLabel("Limit number of eigenvalues :");
        JTextField evField = new JTextField("4");
        super.add(evLabel);
        super.add(evField);
        
		super.repaint();
    }
}
