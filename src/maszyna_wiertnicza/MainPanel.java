package maszyna_wiertnicza;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3679573816029791253L;

	// zmienne pracy i stanu
	Window parent; // referencja okna g��wnego
	boolean running; // czy algorytm pracuje
	boolean fileLoaded; // czy za�adowano miasta
	String filePath; // �cie�ka pliku do odczytu
	int cAmount; // ilo�� miast
	int cTourAmount;
	int[] cIds; // tablica id miast
	int[] cTourIds; 
	double[][] cCoords; // tablica lokalizacji miast
	int selectedTab;

	// referencje okien komunikacyjnych
	GAPanel gapanel;
	RandomPanel randpanel;
	BrutePanel brutepanel;
	StatsPanel stats;
	PlotPanel plot;
	MapPanel map;
	MachinePanel machinepanel;
	AntPanel antpanel;
	SAPanel sapanel;
    IwoPanel iwoPanel;
	
	// algorytm genetyczny
	Core alg;
	Thread algRun;

	// elementy interfejsu
	Graphics g = getGraphics();
	JButton b1;
	JButton b2;
	JButton b3;
	JButton b_tour;
	JProgressBar pb;

	// obraz t�a
	BufferedImage obraz = null;
	Image tlo = null;

	public MainPanel(Window parent, MachinePanel machinepanel, GAPanel gapanel, AntPanel antpanel, SAPanel sapanel, RandomPanel randpanel, BrutePanel brutepanel, IwoPanel iwoPanel,
			StatsPanel chart, PlotPanel plot, MapPanel map) {

		// inicjacja zmiennych stanu
		running = false;

		// zebranie referencji do okien
		this.parent = parent;
		this.gapanel = gapanel;
		this.randpanel = randpanel;
		this.brutepanel = brutepanel;
		this.machinepanel=machinepanel;
		this.antpanel=antpanel;
		this.sapanel=sapanel;
        this.iwoPanel = iwoPanel;

		this.stats = chart;
		this.plot = plot;
		this.map = map;

		try {
			tlo = ImageIO.read(getClass().getResource("/bg.jpg"));
		} catch (Exception e) {
			System.out.println("Błąd wczytywania tła!");
		}
		setLayout(null);
		setBackground(new Color(0xffffff));

		// progress bar
		pb = new JProgressBar(0, 100);
		pb.setValue(0);
		pb.setStringPainted(true);
		pb.setBounds(435, 95, 365, 25);
		pb.setVisible(false);
		add(pb);

		// sekcja przycisk�w

		b1 = new JButton("Wczytaj mapę");
		b1.setBounds(5, 95, 115, 25);
		add(b1);
		b1.addActionListener(this);
		
		b_tour = new JButton("Wczytaj trasę");
		b_tour.setBounds(125, 95, 115, 25);
		b_tour.setEnabled(false);
		add(b_tour);
		b_tour.addActionListener(this);

		b2 = new JButton("Uruchom");
		b2.setBounds(245, 95, 90, 25);
		b2.setEnabled(false);
		add(b2);
		b2.addActionListener(this);

		b3 = new JButton("Przerwij");
		b3.setBounds(340, 95, 90, 25);
		b3.setEnabled(false);
		add(b3);
		b3.addActionListener(this);

	}

	public void actionPerformed(ActionEvent event) {
		// Odebranie uchwytu od listenera i wykonanie adekwatnej operacji
		Object source = event.getSource();
		if (source == b1) {
			// wczytywanie pliku z miastami
			fileLoaded = loadFile();
			if (fileLoaded) {
				b2.setEnabled(true);
				b_tour.setEnabled(true);
			}
		} else if (source == b2) {
			// odpalenie algorytmu z wybranymi ustawieniami
			if (!running && fileLoaded) {
				selectedTab=parent.tabbedP.getSelectedIndex();
				if(selectedTab==0 ||  selectedTab>6){ // 2 tymczasowo nie do odpalenia
					JOptionPane.showMessageDialog(null,"Aby uruchomić symulację wybierz najpierw zakładkę któregoś algorytmu.");
				} else {
				b1.setEnabled(false);
				b2.setEnabled(false);
				b3.setEnabled(true);
				b_tour.setEnabled(false);
				pb.setValue(0);
				pb.setVisible(true);
				running = true;
				runAlg();
				}
			}
		} else if (source == b3) {
			// wstrzymanie pracy algorytmu
			alg.abort();
			running = false;
			b1.setEnabled(true);
			b2.setEnabled(true);
			b3.setEnabled(false);
			pb.setVisible(false);
		}
		else if (source == b_tour) {
			if (!running && fileLoaded) {
				testRoute();
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(tlo, 0, 0, 808, 720, null);
	}

	public void runAlg() {

		//test - odpalmy wszystkie algo naraz w osobnych w�tkach
		/*
		alg = new GACore(this);
		algRun = new Thread(alg);
		algRun.setPriority(Thread.MAX_PRIORITY);
		(algRun).start();
		alg = new RandomCore(this);
		algRun = new Thread(alg);
		algRun.setPriority(Thread.MAX_PRIORITY);
		(algRun).start();
		*/
		
		
		
		try{
		if(selectedTab==1){
			alg = new GACore(this);
		} else if(selectedTab==2){
			alg = new AntCore(this);
		} else if(selectedTab==3){
			alg = new SACore(this);
		} else if(selectedTab==4){
			alg = new RandomCore(this);
		} else if(selectedTab==5) {
            alg = new BruteCore(this);
        }else if(selectedTab==6){
            alg = new IwoCore(this);
        }


		
		algRun = new Thread(alg);
		algRun.setPriority(Thread.MAX_PRIORITY);
		(algRun).start();
		} catch(Exception e){
			JOptionPane.showMessageDialog(null,"Próba uruchomienia algorytmu zakończyła się niespodziewanym błędem. Szczegóły w statystykach.");
			pb.setVisible(false);
			b1.setEnabled(true);
			b2.setEnabled(true);
			b3.setEnabled(false);
			b_tour.setEnabled(true);
			running = false;
			stats.addLine("============================================================================================================");
			stats.addDate();
			stats.addLine(">>> Wystąpił wyjątek: "+e.getClass());
			stats.addLine("Komunikat błędu: "+e.getMessage());
			e.printStackTrace();
			stats.addLine("============================================================================================================");
		}

	}
	
	public void testRoute() throws InputMismatchException {
		JFileChooser fc = new JFileChooser("input");
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "TXT & TOUR Text Files", "txt", "tour");
	        fc.setFileFilter(filter);
		int fcReturn = fc.showOpenDialog(fc);
		if (fcReturn == JFileChooser.APPROVE_OPTION) {

			System.out.println("Rozpoczęto wczytywanie pliku");
			FileReader fr = null;
			try {
				fr = new FileReader(fc.getSelectedFile().getPath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Błąd: Brak dostępu do pliku!");
				//return false;
				return;
			}
			filePath = fc.getSelectedFile().getPath();
			Scanner sca = new Scanner(fr);

			//System.out.println("Rozpocz�to parsowanie pliku");
			try {
				while (!sca.next().contains("DIMENSION")) {

				}
				while (!sca.hasNextInt()) {
					sca.next();
				}
				cTourAmount = sca.nextInt()-1;
				if(cTourAmount+1!=cAmount){
					JOptionPane.showMessageDialog(null,"Błąd: Liczba punktów trasy nie zgadza się z mapą. Trasa ta nie jest przeznaczona dla tej mapy.");
					return;
				}
				
				cTourIds = new int[cTourAmount];

				while (!sca.next().contains("TOUR_SECTION")) {

				}
				while (!sca.hasNextInt()) {
					sca.next();
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Błąd: Nie znaleziono DIMENSION lub TOUR_SECTION w pliku!");
				System.out.println("Nie znaleziono DIMENSION lub TOUR_SECTION!");
				e.printStackTrace();
				return;
			}
			int i = 0;
			while (sca.hasNextInt()) {
				try {
					int id = sca.nextInt();
					if(id!=1 && id!=-1){
						cTourIds[i] = id;
						i++;
					}
					
				} catch (Exception e) {
					System.out.println("Wystąpił błąd przy wczytywaniu identyfikatorów trasy!");
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,"Błąd: Nie udało się wczytać identyfikatorów trasy!");
					return;
				}
			}
			//System.out.println("tour:"+Arrays.toString(cTourIds));
			System.out.println("Wczytano " + i + " punktów");
			
			
			try{
			City cTourList[]=new City[cTourAmount];
			//wyliczanie d�ugo�ci trasy
			for(int j=0;j<cTourAmount;j++){
				int cityId=cTourIds[j];
				double x=cCoords[cityId-1][0];
				double y=cCoords[cityId-1][1];
				cTourList[j]=new City(cityId,x,y);
			}
			RandomCore testcore=new RandomCore(this);
			testcore.startCity= new City(cIds[0], cCoords[0][0], cCoords[0][1]);
			Specimen tester=new Specimen(testcore);
			tester.setRoute(cTourList);
			double optymalna=tester.getRate();
			JOptionPane.showMessageDialog(null,"Długość wczytanej trasy: "+round(optymalna,2));
			stats.addLine("============================================================================================================");
			stats.addDate();
			stats.addLine(">>> Wczytano trasę z pliku: "+fc.getSelectedFile().getPath());
			stats.addLine("Interwał wymiany wiertła: "+testcore.interwal_wymiany);
			stats.addLine("Długość trasy: "+round(optymalna,2));
			stats.addLine("============================================================================================================");
			} catch (Exception e){
				JOptionPane.showMessageDialog(null,"Błąd: Nie udało się obliczyć długości trasy, być może plik trasy jest wadliwy.");
				System.out.println("Wystąpił błąd przy obliczaniu długości trasy, być może błąd w pliku trasy.");
				e.printStackTrace();
			}
			


		}


		return;
	}
	
	public double round(double d,int pos){
		if(Double.isInfinite(d) || Double.isNaN(d)){
			return -1;
		} else {
		return new BigDecimal(d).setScale(pos, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}



	public boolean loadFile() throws InputMismatchException {
		JFileChooser fc = new JFileChooser("input");
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "TXT & TSP Text Files", "txt", "tsp");
	        fc.setFileFilter(filter);
		int fcReturn = fc.showOpenDialog(fc);
		if (fcReturn == JFileChooser.APPROVE_OPTION) {

			System.out.println("Rozpoczęto wczytywanie pliku");
			FileReader fr = null;
			try {
				fr = new FileReader(fc.getSelectedFile().getPath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Błąd: Brak dostępu do pliku!");
				return false;
			}
			filePath = fc.getSelectedFile().getPath();
			Scanner sca = new Scanner(fr);

			System.out.println("Rozpoczęto parsowanie pliku");
			try {
				while (!sca.next().contains("DIMENSION")) {

				}
				while (!sca.hasNextInt()) {
					sca.next();
				}
				cAmount = sca.nextInt();
				cIds = new int[cAmount];
				cCoords = new double[cAmount][2];
				while (!sca.next().contains("NODE_COORD_SECTION")) {

				}
				while (!sca.hasNextInt()) {
					sca.next();
				}
			} catch (Exception e) {
				System.out
						.println("Nie znaleziono DIMENSION lub NODE_COORD_SECTION!");
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Błąd: Nie znaleziono DIMENSION lub NODE_COORD_SECTION w pliku!");
				return false;
			}
			int i = 0;
			while (sca.hasNextInt()) {
				try {
					int id = sca.nextInt();
					//System.out.println(id);
					double x = sca.nextDouble();
					double y = sca.nextDouble();
					cIds[i] = id;
					cCoords[i][0] = x;
					cCoords[i][1] = y;
					i++;
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,"Błąd: Nie udało się wczytać listy miast. Prawdopodobnie plik jest źle sformatowany.");
					System.out.println("Wystąpił błąd przy wczytywaniu listy miast!");
					e.printStackTrace();

					return false;
				}
			}
			System.out.println("Wczytano " + i + " punktów");

			return true;
		}

		return false;
	}
}