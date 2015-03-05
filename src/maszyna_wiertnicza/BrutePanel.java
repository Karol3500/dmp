package maszyna_wiertnicza;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class BrutePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -2385801152153763104L;

	// elementy interfejsu
	Graphics g = getGraphics();
	JTextField interval;
	JLabel notice;
	JCheckBox statsOn;
	JCheckBox mapOn;
	JCheckBox plotOn;
	JPanel basicSettings;
	JPanel otherSettings;
	JLabel warning;

	public BrutePanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets =  new Insets(6,5,0,5);
		gbc.gridx = 0;
		gbc.gridy = 99;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		JLabel placeholder = new JLabel("");
		add(placeholder, gbc);
		gbc.weighty = 0.0;
		
		
		
	
		
		//kontener na pozosta�e ustawienia
				otherSettings = new JPanel();
				otherSettings.setLayout(new GridBagLayout());
				GridBagConstraints gbcOS = new GridBagConstraints();
				gbcOS.fill = GridBagConstraints.NONE;
				gbcOS.anchor = GridBagConstraints.EAST;
				gbcOS.insets =  new Insets(3,5,3,5);
				gbcOS.gridx = 99;
				gbcOS.gridy = 4;
				gbcOS.weightx = 1.0;
				gbcOS.weighty = 1.0;
				JLabel placeholderx = new JLabel("");
				otherSettings.add(placeholderx, gbcOS);
				gbcOS.weightx = 0.0;
				gbcOS.weighty = 0.0;
				
				statsOn = new JCheckBox("Dokładne statystyki");
				statsOn.setSelected(false); 
				gbcOS.gridx = 0;
				gbcOS.gridy = 0;
				otherSettings.add(statsOn, gbcOS);
				
				plotOn = new JCheckBox("Rysowanie wykresu"); 
				plotOn.setSelected(false);
				gbcOS.gridx = 1;
				gbcOS.gridy = 0;
				otherSettings.add(plotOn, gbcOS);
				
				mapOn = new JCheckBox("Rysowanie mapy"); 
				mapOn.setSelected(false);
				gbcOS.gridx = 2;
				gbcOS.gridy = 0;
				otherSettings.add(mapOn, gbcOS);

				
				TitledBorder otherSetBorder = BorderFactory.createTitledBorder("Pozosta�e");
				otherSettings.setBorder(otherSetBorder);
				gbc.gridx = 0;
				gbc.gridy = 0;
				add(otherSettings, gbc);
				
				notice=new JLabel("Uwaga: Dla algorytmu brutalnego zaleca się wprowadzanie mapy z maksymalnie 13 punktami.");
				gbc.gridx = 0;
				gbc.gridy = 1;
				add(notice, gbc);
				
				warning=new JLabel("Uwaga: Użycie dodatkowych ustawień może zmniejszyć prędkość działania programu.");
				gbc.gridx = 0;
				gbc.gridy = 2;
				add(warning,gbc);
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);

	}

	public void actionPerformed(ActionEvent arg0) {

	}
}
