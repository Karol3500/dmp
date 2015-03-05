package maszyna_wiertnicza;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cern.jet.random.Uniform;

public final class AntCore extends Core {

	Record startRecord;
	WalkedWay bestway;
	int bestAnt;

	long start; // start licznika
	long stop; // stop licznika
	boolean detailedStatsOn,plotOn,mapOn;

	// parametry
	// greedy
	public double ALPHA;// = -0.2d;
	// rapid selection
	public double BETA;// = 9.6d;

	// heuristic parameters
	public double Q;// = 0.0001d; // somewhere between 0 and 1
	public double PHEROMONE_PERSISTENCE;// = 0.3d; // between 0 and 1
	public double INITIAL_PHEROMONES;// = 0.8d; // can be anything

	// use power of 2
	public int numOfAgents;// = 2048 * 20;

	private static final int poolSize = Runtime.getRuntime()
			.availableProcessors();

	private Uniform uniform;

	private final ExecutorService threadPool = Executors
			.newFixedThreadPool(poolSize);

	private final ExecutorCompletionService<WalkedWay> agentCompletionService = new ExecutorCompletionService<WalkedWay>(
			threadPool);

	final double[][] matrix;
	double[] startDistances;
	final double[][] invertedMatrix;
	private final double[][] pheromones;
	private final Object[][] mutexes;

	public AntCore(MainPanel mainpanel) {
		super(mainpanel);
		
		

		ALPHA = Double.parseDouble(parent.antpanel.alpha.getText());
		BETA = Double.parseDouble(parent.antpanel.beta.getText());
		Q = Double.parseDouble(parent.antpanel.q.getText());
		PHEROMONE_PERSISTENCE = Double.parseDouble(parent.antpanel.f_pers
				.getText());
		INITIAL_PHEROMONES = Double.parseDouble(parent.antpanel.f_init
				.getText());
		numOfAgents = Integer.parseInt(parent.antpanel.antCount.getText());
		detailedStatsOn = parent.antpanel.statsOn.isSelected();
		plotOn=parent.antpanel.plotOn.isSelected();
		mapOn=parent.antpanel.mapOn.isSelected();

		// read the matrix
		matrix = readMatrixFromFile();
		invertedMatrix = invertMatrix();
		pheromones = initializePheromones();
		mutexes = initializeMutexObjects();
		// (double min, double max, int seed)
		uniform = new Uniform(0, matrix.length - 1,
				(int) System.currentTimeMillis());
	}

	private final Object[][] initializeMutexObjects() {
		final Object[][] localMatrix = new Object[matrix.length][matrix.length];
		int rows = matrix.length;
		for (int columns = 0; columns < matrix.length; columns++) {
			for (int i = 0; i < rows; i++) {
				localMatrix[columns][i] = new Object();
			}
		}

		return localMatrix;
	}

	final double readPheromone(int x, int y) {
		// double p;
		// synchronized (mutexes[x][y]) {
		// p = pheromones[x][y];
		// }
		// return p;
		return pheromones[x][y];
	}

	final void adjustPheromone(int x, int y, double newPheromone) {
		synchronized (mutexes[x][y]) {
			final double result = calculatePheromones(pheromones[x][y],
					newPheromone);
			if (result >= 0.0d) {
				pheromones[x][y] = result;
			} else {
				pheromones[x][y] = 0;
			}
		}
	}

	private final double calculatePheromones(double current, double newPheromone) {
		final double result = (1 - PHEROMONE_PERSISTENCE) * current
				+ newPheromone;
		return result;
	}

	final void adjustPheromone(int[] way, double newPheromone) {
		synchronized (pheromones) {
			for (int i = 0; i < way.length - 1; i++) {
				pheromones[way[i]][way[i + 1]] = calculatePheromones(
						pheromones[way[i]][way[i + 1]], newPheromone);
			}
			pheromones[way[way.length - 1]][way[0]] = calculatePheromones(
					pheromones[way.length - 1][way[0]], newPheromone);
		}
	}

	private final double[][] initializePheromones() {
		final double[][] localMatrix = new double[matrix.length][matrix.length];
		int rows = matrix.length;
		for (int columns = 0; columns < matrix.length; columns++) {
			for (int i = 0; i < rows; i++) {
				localMatrix[columns][i] = INITIAL_PHEROMONES;
			}
		}

		return localMatrix;
	}

	private final double[][] readMatrixFromFile() {
		
		if(mapOn){
			parent.map.clearAll();
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(parent.filePath)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final LinkedList<Record> records = new LinkedList<Record>();

		startRecord = null;
		boolean readAhead = false;
		String line;
		try {
			while ((line = br.readLine()) != null) {

				if (line.equals("EOF")) {
					break;
				}

				if (readAhead && !line.trim().equals("")) {
					Scanner sc = new Scanner(line);
					while (!sc.hasNextInt()) {
						sc.next();
					}
					int id_miasta = sc.nextInt();
					double x = sc.nextDouble();
					double y = sc.nextDouble();

					// String[] split = line.trim().split(" ");
					if (startRecord == null) {
						startRecord = new Record(x, y);
						startCity = new City(1, startRecord.x, startRecord.y);
		    			if(mapOn){
		    				parent.map.addNode(1, startRecord.x, startRecord.y, "cyan");
		    		}
					} else {
						records.add(new Record(x, y));
						if(mapOn){
						parent.map.addNode(id_miasta, x, y,null);
						}
					}
				}

				if (line.equals("NODE_COORD_SECTION")) {
					readAhead = true;
				}
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parent.map.setAutoScale();

		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final double[][] localMatrix = new double[records.size()][records
				.size()];

		int rIndex = 0;
		for (Record r : records) {
			int hIndex = 0;
			for (Record h : records) {
				localMatrix[rIndex][hIndex] = calculateEuclidianDistance(r.x,
						r.y, h.x, h.y);
				hIndex++;
			}
			rIndex++;
		}
		startDistances = new double[records.size()];
		int sIndex = 0;
		for (Record s : records) {
			startDistances[sIndex] = calculateEuclidianDistance(startRecord.x,
					startRecord.y, s.x, s.y);
			sIndex++;
		}

		return localMatrix;
	}

	private final double[][] invertMatrix() {
		double[][] local = new double[matrix.length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				local[i][j] = invertDouble(matrix[i][j]);
			}
		}
		return local;
	}

	private final double invertDouble(double distance) {
		if (distance == 0)
			return 0;
		else
			return 1.0d / distance;
	}

	private final double calculateEuclidianDistance(double x1, double y1,
			double x2, double y2) {
		return Math
				.abs((Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2))));
	}

	public void run() {
		
		if(plotOn){
			parent.plot.resetData();
			parent.plot.setTitle("Przebieg algorytmu mrówkowego");
			parent.plot.addXYSeries("Długość trasy");
		}
		
		

		WalkedWay bestDistance = null;

		int agentsSend = 0;
		int agentsDone = 0;
		int agentsWorking = 0;
		start = System.currentTimeMillis(); // start licznika czasu
		parent.pb.setMaximum(numOfAgents);
		for (int agentNumber = 0; !abort && agentNumber < numOfAgents; agentNumber++) {
			agentCompletionService.submit(new AntAgent(this,
					getGaussianDistributionRowIndex()));
			agentsSend++;
			agentsWorking++;
			while (!abort && agentsWorking >= poolSize) {
				WalkedWay way = null;
				try {
					way = agentCompletionService.take().get();
					if(plotOn){
						parent.plot.addPoint(0, agentsDone, way.distance);
						}
					if (detailedStatsOn) {
						String line = "Mrówka #" + agentsDone
								+ " -> długość trasy: "
								+ round(way.distance, 2);
						addLine(line);
					}
					//rysujemy mape
					 
					
					
					
				} catch (InterruptedException | ExecutionException e) {

					e.printStackTrace();
				}
				if (bestDistance == null
						|| way.distance < bestDistance.distance) {
					bestDistance = way;
					bestAnt = agentNumber;
					// System.out.println("Agent returned with new best distance of: "
					// + way.distance);
				}
				
				double time=System.currentTimeMillis(); //1367415958031
		         double miliseconds= time % 100000;
		         int seconds = (int)(miliseconds / 1000);
				if((seconds % 3 ==0) && mapOn){
		     		//System.out.println(seconds);
		 			parent.map.clearEdges();
		 			parent.map.addEdge(startCity.getNumber(), (bestDistance.way[0]+2),null);
		 			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
		 			int s;
		 			for(s=0;s<bestDistance.way.length-1;s++){
		 				if(((s+1) % interwal_wymiany)==0){
		 					parent.map.addEdge((bestDistance.way[s]+2),startCity.getNumber(),"blue");
		 					//System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
		 					parent.map.addEdge(startCity.getNumber(),(bestDistance.way[s+1]+2),"blue");
		 					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
		 				} else {
		 					parent.map.addEdge((bestDistance.way[s]+2),(bestDistance.way[s+1]+2),null);
		 					//System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
		 				}
		 			}
		 			parent.map.addEdge((bestDistance.way[s]+2),startCity.getNumber(),null);
		 			//System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
		 			parent.map.plotEdges();
		 		}
				
				agentsDone++;
				parent.pb.setValue(agentsDone);
				agentsWorking--;
			}
		}
		final int left = agentsSend - agentsDone;
		// System.out.println("Waiting for " + left +
		// " agents to finish their random walk!");

		for (int i = 0; !abort && i < left; i++) {
			WalkedWay way = null;
			try {
				way = agentCompletionService.take().get();
				int agentnum=agentsSend-left+i;
				parent.pb.setValue(agentnum);
				if(plotOn){
					parent.plot.addPoint(0, agentnum, way.distance);
					}
				if (detailedStatsOn) {
					String line = "Mrówka #" + agentnum
							+ " -> długość trasy: "
							+ round(way.distance, 2);
					addLine(line);
				}
				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bestDistance == null || way.distance < bestDistance.distance) {
				bestDistance = way;
				bestAnt = agentsDone + i;
				// System.out.println("Agent returned with new best distance of: "
				// + way.distance);
			}
		}

		threadPool.shutdownNow();
		bestway = bestDistance;
		stop = System.currentTimeMillis(); // stop licznika czasu
		showEffects();

		// return bestDistance.distance;

	}

	private final int getGaussianDistributionRowIndex() {
		return uniform.nextInt();
	}

	static class Record {
		double x;
		double y;

		public Record(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}
	}

	static class WalkedWay {
		int[] way;
		double distance;

		public WalkedWay(int[] way, double distance) {
			super();
			this.way = way;
			this.distance = distance;
		}
	}

	public void showEffects() {
		
		//TODO - NOT WORKING
		
		if(mapOn){
			/*parent.map.clearEdges();
			parent.map.addEdge(startCity.getNumber(), (bestway.way[0]+2),null);
			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
			int i;
			for(i=0;i<bestway.way.length-1;i++){
				if(((i+1) % interwal_wymiany)==0){
					parent.map.addEdge((bestway.way[i]+2),startCity.getNumber(),"blue");
					//System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
					parent.map.addEdge(startCity.getNumber(),(bestway.way[i+1]+2),"blue");
					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
				} else {
					parent.map.addEdge((bestway.way[i]+2),(bestway.way[i+1]+2),null);
					//System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
				}
			}
			parent.map.addEdge((bestway.way[i]+2),startCity.getNumber(),null);
			//System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
			parent.map.plotEdges();*/
			
			parent.map.finishedSimulation();
			
		}
		
		
		// zapis wynik�w do logu i przebiegu
		addLine("============================================================================================================");
		addDate();
		String temp = "";
		if (abort == true) {
			temp = "(z powodu przerwania w "
					+ round(100 * parent.pb.getPercentComplete(), 2) + " %) ";
		}
		addLine(">>> Algorytm MRÓWKOWY zakończył pracę " + temp
				+ "z następującym wynikiem:");
		addPhrase("Czas pracy algorytmu: " + (stop - start) / 1000.0 + " s");
		newLine();
		addLine("Interwał wymiany wiertła: " + interwal_wymiany);
		// addLine("Liczba wys�anych mr�wek="+numOfAgents);
		addLine("Długość trasy: " + round(bestway.distance, 2));
		String tempS = "";
		if (abort) {
			tempS = " ( z " + parent.pb.getValue() + ")";
		}
		addLine("Mrówka która znalazła najlepszą trasę: " + bestAnt + tempS);
		addPhrase("Najlepsza trasa: " + printRoute(bestway.way));
		newLine();

		addLine("============================================================================================================");

		// ustawienie przycisk�w g��wnego okna do stanu pozwalaj�cego na dalsze
		// badania
		parent.pb.setVisible(false);
		parent.b1.setEnabled(true);
		parent.b2.setEnabled(true);
		parent.b3.setEnabled(false);
		parent.b_tour.setEnabled(true);
		parent.running = false;
		System.gc(); 
	}

	public void addPhrase(String s) {
		parent.stats.addPhrase(s);
	}

	public void addDate() {
		parent.stats.addDate();
	}

	public void addLine(String s) {
		parent.stats.addLine(s);
	}

	public void newLine() {
		parent.stats.newLine();
	}

	public double round(double d, int pos) {
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			return -1;
		} else {
			return new BigDecimal(d).setScale(pos, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
		}
	}

	public String printRoute(int[] way) {
		if (way == null) {
			return "";
		}
		
		 String s="";
		 
	        //start ze startowego
	       s+="("+startCity.getNumber()+") ";

/*		 for(int i=0;i<way.length;i++) {
			 s+=(way[i]+2)+" ";
		 }*/
		 

	        for(int i=0;i<way.length;i++) {
	        	if(((i+1) % interwal_wymiany)==0){
	        		//wykonaj powr�t do punktu startowego oraz przejdz do nast�pnego punktu z punktu startowego
	        		  s+="<<"+startCity.getNumber()+">> ";
	        		  s+=(way[i]+2)+" ";
	        	} else { 
	        		s+=(way[i]+2)+" ";
	        	}
	        }
	        //na koniec wracamy do punktu startowego
	        s+="("+startCity.getNumber()+") ";
	        
	        
	        return s;
	}

}