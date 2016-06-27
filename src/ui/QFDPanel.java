package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class QFDPanel extends FeaturePanel{
	JFormattedTextField evField;
	JLabel evLabel;
	public QFDPanel() {
		super();
		super.setLayout(new FlowLayout());
		super.setBorder(BorderFactory.createTitledBorder("Additional Parameters Distance"));
        evLabel = new JLabel("Limit number of eigenvalues :");
        evField = new JFormattedTextField(NumberFormat.getNumberInstance());
        evField.setValue(new Integer(1));
        
        evField.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
			    Object source = e.getSource();
			    Integer v = 0;
			    if (source == evField) {
			        v = ((Number)evField.getValue()).intValue();
			        if (v > 0 && v <= 256){
			        	evField.setValue(new Integer(v));
			        	return;
			        }
			    } else {
			    }
			    evField.setValue(new Integer(0));
			}
        });
        
        evField.setColumns(10);
        super.add(evLabel);
        super.add(evField);
        
		super.repaint();
    }
	public JFormattedTextField getEvField() {
		return evField;
	}
	public void setEvField(JFormattedTextField evField) {
		this.evField = evField;
	}
	
}
