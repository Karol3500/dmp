package maszyna_wiertnicza;

import java.awt.Container;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class WinThread extends Thread{
	Window mainWindow;
	
	public WinThread(){
		
	}
	
	public void run(){
		mainWindow = new Window();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
		mainWindow.setBounds(10, 10, 808, 748);
		mainWindow.setResizable(false);
		try {
			Image icon = ImageIO.read(getClass().getResource("/logo_pwr90.jpg"));
			mainWindow.setIconImage(icon);
		} catch (Exception e) {
			System.out.println("Błąd wczytywania ikony!");
		}
		mainWindow.setTitle("Optymalizacja trasy dla maszyny wiertniczej");
	}
}

class Window extends JFrame {
	private static final long serialVersionUID = 7415829889250112455L;
	
	Container contents;
	JTabbedPane tabbedP;
	MachinePanel machinepanel;
	GAPanel gapanel;
	AntPanel antpanel;
	SAPanel sapanel;
	RandomPanel randpanel;
	BrutePanel brutepanel;
    IwoPanel iwopanel;
	StatsPanel stats;
	PlotPanel plot;
	MapPanel map;
	MainPanel main;
	
	public Window() {
		Container contents = getContentPane();
		tabbedP = new JTabbedPane();
		//uwaga na kolejno��!!!
		machinepanel = new MachinePanel();
		tabbedP.addTab("Maszyna", machinepanel);
		gapanel = new GAPanel();
		tabbedP.addTab("Alg genetyczny", gapanel);
		antpanel = new AntPanel();
		tabbedP.addTab("Alg mrówkowy", antpanel);
		sapanel = new SAPanel();
		tabbedP.addTab("Alg sym. wyżarzania", sapanel);
		randpanel= new RandomPanel();
		tabbedP.addTab("Alg losowy", randpanel);
		brutepanel= new BrutePanel();
		tabbedP.addTab("Alg brutalny", brutepanel);
        iwopanel = new IwoPanel();
        tabbedP.addTab("Alg IWO", iwopanel);
        stats = new StatsPanel();
		tabbedP.addTab("Statystyki", stats);
		plot = new PlotPanel();
		tabbedP.addTab("Wykres", plot.chartPanel);
		map = new MapPanel();
		tabbedP.addTab("Mapa", map);
		contents.add(tabbedP);
		main = new MainPanel(this,machinepanel, gapanel,antpanel,sapanel, randpanel, brutepanel, iwopanel, stats, plot, map);
		contents.add(main);
		tabbedP.setBounds(2, 125, 798, 495);
		//tabbedP.setBounds(2, 35, 798, 565); //poprzednie
		
		
	}
}