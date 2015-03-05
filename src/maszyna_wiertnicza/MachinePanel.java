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

public class MachinePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -2385801152153763104L;

	// elementy interfejsu
	Graphics g = getGraphics();
	JLabel legend;
	JTextField TL;
	JTextField Dc;
	JTextField Vc;
	JTextField n;
	JTextField Fn;
	JTextField d;
	JLabel lTL,lDc,lVc,ln,lFn,ld;
	JPanel machineSettings;
	
	JPanel instructions;
	JLabel linstructions;
	

	public MachinePanel() {
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
		machineSettings = new JPanel();
		machineSettings.setLayout(new GridBagLayout());
		GridBagConstraints gbcOS = new GridBagConstraints();
		gbcOS.fill = GridBagConstraints.NONE;
		gbcOS.anchor = GridBagConstraints.WEST;
		gbcOS.insets =  new Insets(3,5,3,5);
		gbcOS.gridx = 99;
		gbcOS.gridy = 4;
		gbcOS.weightx = 1.0;
		gbcOS.weighty = 1.0;
		JLabel placeholderx = new JLabel("");
		machineSettings.add(placeholderx, gbcOS);
		gbcOS.weightx = 0.0;
		gbcOS.weighty = 0.0;
		
		
		TL = new JTextField("4", 5);
		lTL = new JLabel("TL (m)  | Trwałość wiertła");
		lTL.setLabelFor(TL);
		gbcOS.gridx = 0;
		gbcOS.gridy = 0;
		machineSettings.add(TL, gbcOS);
		gbcOS.gridx = 1;
		gbcOS.gridy = 0;
		machineSettings.add(lTL, gbcOS);
		
		Dc = new JTextField("20", 5);
		lDc = new JLabel("Dc (mm)  | Średnica wiertła");
		lDc.setLabelFor(Dc);
		gbcOS.gridx = 0;
		gbcOS.gridy = 1;
		machineSettings.add(Dc, gbcOS);
		gbcOS.gridx = 1;
		gbcOS.gridy = 1;
		machineSettings.add(lDc, gbcOS);
		
		
		n = new JTextField("3184", 5);
		ln = new JLabel("n (obr/min)  | Prędkość obrotowa wrzeciona");
		ln.setLabelFor(n);
		gbcOS.gridx = 0;
		gbcOS.gridy = 2;
		machineSettings.add(n, gbcOS);
		gbcOS.gridx = 1;
		gbcOS.gridy = 2;
		machineSettings.add(ln, gbcOS);
		
		Fn = new JTextField("0.80", 5);
		lFn = new JLabel("Fn (mm/obr)  | Posuw na obrót");
		lFn.setLabelFor(Fn);
		gbcOS.gridx = 0;
		gbcOS.gridy = 3;
		machineSettings.add(Fn, gbcOS);
		gbcOS.gridx = 1;
		gbcOS.gridy = 3;
		machineSettings.add(lFn, gbcOS);
		
		d = new JTextField("50", 5);
		ld = new JLabel("d (mm)  | Głębokość otworów");
		ld.setLabelFor(d);
		gbcOS.gridx = 0;
		gbcOS.gridy = 4;
		machineSettings.add(d, gbcOS);
		gbcOS.gridx = 1;
		gbcOS.gridy = 4;
		machineSettings.add(ld, gbcOS);
		
				
				
				
				
				
				
				
				TitledBorder otherSetBorder = BorderFactory.createTitledBorder("Ustawienia maszyny wiertniczej");
				machineSettings.setBorder(otherSetBorder);
				gbc.gridx = 0;
				gbc.gridy = 0;
				add(machineSettings, gbc);
				
				

				
				
				instructions = new JPanel();
				instructions.setLayout(new GridBagLayout());
				GridBagConstraints gbci = new GridBagConstraints();
				gbci.fill = GridBagConstraints.NONE;
				gbci.anchor = GridBagConstraints.WEST;
				gbci.insets =  new Insets(3,5,3,5);
				gbci.gridx = 99;
				gbci.gridy = 4;
				gbci.weightx = 1.0;
				gbci.weighty = 1.0;
				JLabel placeholder1 = new JLabel("");
				instructions.add(placeholder1, gbci);
				gbci.weightx = 0.0;
				gbci.weighty = 0.0;
				
				linstructions=new JLabel("<html>Aby program wyświetał się poprawnie, należy go umieścić w tym samym folderze, w którym znajduje się folder img. <br>Użytowanie programu przebiega następująco:<br>1) Ustawienie parametrów maszyny wiertniczej służących do obliczenia interwału wymiany wiertła<br>2) Wczytanie mapy punktów<br>3) Przejście do zakładki dowolnego algorytmu i ustawienie jego parametrów<br>4) Uruchomienie symulacji<br>5) Odczytanie wyników w zakładce statystyki<br><br>Dodatkowo po wczytaniu mapy można wczytać trasę z pliku aby wyliczyć jej długość.</html>");
				gbci.gridx = 0;
				gbci.gridy = 0;
				instructions.add(linstructions, gbci);
				
				TitledBorder instrSetBorder = BorderFactory.createTitledBorder("Instrukcja użytkowania programu");
				instructions.setBorder(instrSetBorder);
				gbc.gridx = 0;
				gbc.gridy = 1;
				add(instructions, gbc);
				
				
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);

	}

	public void actionPerformed(ActionEvent arg0) {

	}
}
