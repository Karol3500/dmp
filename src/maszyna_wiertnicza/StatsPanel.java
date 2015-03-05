package maszyna_wiertnicza;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StatsPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	JTextArea ta1;
	JScrollPane scroll;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	Calendar cal;

	public StatsPanel() {
		cal=Calendar.getInstance();
		setLayout(null);

		ta1 = new JTextArea();
		ta1.setBounds(10, 10, 775, 455);
		ta1.setLineWrap(true);
		ta1.setRows(10);
		ta1.setWrapStyleWord(true);
		ta1.setEditable(false);
		scroll = new JScrollPane(ta1);
		scroll.setBounds(10, 10, 775, 455);
		add(scroll);
	}

	public void actionPerformed(ActionEvent event) {

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
	}

	public void clearFlow() {
		cal = Calendar.getInstance();
		ta1.setText("");

	}

	public void addLine(String s) {
		ta1.append(" " + s + "\n");
	}

	public void addPhrase(String s) {
		ta1.append(" " + s);
	}

	public void addDate() {
		cal = Calendar.getInstance();
		ta1.append(" >>> " + sdf.format(cal.getTime())/*+ " >>>"*/);
	}

	public void newLine() {
		ta1.append("\n");
	}

}