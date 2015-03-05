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

public class GAPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -2385801152153763104L;

	// elementy interfejsu
	Graphics g = getGraphics();
	ButtonGroup selectionMethod;
	JRadioButton srb1;
	JRadioButton srb2;
	JRadioButton srb3;
	JCheckBox statsOn;
	JCheckBox mapOn;
	JCheckBox plotOn;
	JTextField genAmount;
	JLabel genAmL;
	JTextField selAmount;
	JLabel selAmL;
	JTextField kHybrid;
	JLabel kHybL;
	JTextField kMutation;
	JLabel kMutL;
	JTextField sAmount;
	JLabel sAmL;
	JTextField swMaxLen;
	JLabel swMaxLenL;
	JTextField rotInt;
	JLabel rotIntL;
	JPanel basicSettings;
	JPanel otherSettings;
	JPanel selSettings;
	JLabel warning;



	public GAPanel() {
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
		
		
		// kontener na ustawienia selekcji
		selSettings = new JPanel();
		selSettings.setLayout(new GridBagLayout());
		GridBagConstraints gbcSS = new GridBagConstraints();
		gbcSS.fill = GridBagConstraints.NONE;
		gbcSS.anchor = GridBagConstraints.WEST;
		gbcSS.insets =  new Insets(3,5,3,5);
		gbcSS.gridx = 99;
		gbcSS.gridy = 3;
		gbcSS.weightx = 1.0;
		gbcSS.weighty = 1.0;
		JLabel placeholder2 = new JLabel("");
		selSettings.add(placeholder2, gbcSS);
		gbcSS.weightx = 0.0;
		gbcSS.weighty = 0.0;
		
		//grupa przycisk�w (selekcja)
		selectionMethod = new ButtonGroup(); 
		srb1 = new JRadioButton("Turniej"); 
		selectionMethod.add(srb1); 
		gbcSS.gridx = 0;
		gbcSS.gridy = 0;
		selSettings.add(srb1, gbcSS);
		srb2 = new JRadioButton("Ruletka");
		selectionMethod.add(srb2);
		gbcSS.gridx = 0;
		gbcSS.gridy = 1;
		selSettings.add(srb2, gbcSS);
		srb3 = new JRadioButton("Ranking");
		selectionMethod.add(srb3);
		gbcSS.gridx = 0;
		gbcSS.gridy = 2;
		selSettings.add(srb3, gbcSS);
		
		srb1.setSelected(true);
		
		
		TitledBorder selSetBorder = BorderFactory.createTitledBorder("Metoda selekcji");
		selSettings.setBorder(selSetBorder);
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(selSettings, gbc);

		
		

		
		// warto�ci uzupe�niane
		
		// kontener na ustawienia podstawowe algorytmu
		basicSettings = new JPanel();
		basicSettings.setLayout(new GridBagLayout());
		GridBagConstraints gbcBS = new GridBagConstraints();
		gbcBS.fill = GridBagConstraints.NONE;
		gbcBS.anchor = GridBagConstraints.WEST;
		gbcBS.insets =  new Insets(3,5,3,5);
		gbcBS.gridx = 99;
		gbcBS.gridy = 4;
		gbcBS.weightx = 1.0;
		gbcBS.weighty = 1.0;
		JLabel placeholder5 = new JLabel("");
		basicSettings.add(placeholder5, gbcBS);
		gbcBS.weightx = 0.0;
		gbcBS.weighty = 0.0;
		
		genAmount = new JTextField("800", 5);
		genAmL = new JLabel("Liczba pokoleń");
		genAmL.setLabelFor(genAmount);
		gbcBS.gridx = 0;
		gbcBS.gridy = 0;
		basicSettings.add(genAmount, gbcBS);
		gbcBS.gridx = 1;
		gbcBS.gridy = 0;
		basicSettings.add(genAmL, gbcBS);
		sAmount = new JTextField("400", 5);
		sAmL = new JLabel("Liczba osobników");
		sAmL.setLabelFor(sAmount);
		gbcBS.gridx = 0;
		gbcBS.gridy = 1;
		basicSettings.add(sAmount, gbcBS);
		gbcBS.gridx = 1;
		gbcBS.gridy = 1;
		basicSettings.add(sAmL, gbcBS);
		kHybrid = new JTextField("65", 5);
		kHybL = new JLabel("Prawdopodobieństwo krzyżowania (%)");
		kHybL.setLabelFor(kHybrid);
		gbcBS.gridx = 0;
		gbcBS.gridy = 2;
		basicSettings.add(kHybrid, gbcBS);
		gbcBS.gridx = 1;
		gbcBS.gridy = 2;
		basicSettings.add(kHybL, gbcBS);
		kMutation = new JTextField("20", 5);
		kMutL = new JLabel("Prawdopodobieństwo mutacji (%)");
		kMutL.setLabelFor(kMutation);
		gbcBS.gridx = 0;
		gbcBS.gridy = 3;
		basicSettings.add(kMutation, gbcBS);
		gbcBS.gridx = 1;
		gbcBS.gridy = 3;
		basicSettings.add(kMutL, gbcBS);
		
		
		TitledBorder basicSetBorder = BorderFactory.createTitledBorder("Ustawienia algorytmu genetycznego");
		basicSettings.setBorder(basicSetBorder);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(basicSettings, gbc);
		
		
		
		
		
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
		basicSettings.add(placeholderx, gbcOS);
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

		
		TitledBorder otherSetBorder = BorderFactory.createTitledBorder("Pozostałe");
		otherSettings.setBorder(otherSetBorder);
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(otherSettings, gbc);
		
		warning=new JLabel("Uwaga: Użycie dodatkowych ustawień może zmniejszyć prędkość działania programu.");
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(warning,gbc);
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		// tekst opis�w
		// g.drawString("x", 35, 20);
	}

	public void actionPerformed(ActionEvent arg0) {

	}
}
