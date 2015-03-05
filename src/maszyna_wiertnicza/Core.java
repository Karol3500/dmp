package maszyna_wiertnicza;

public abstract class Core extends Thread {
	boolean abort;
	int interwal_wymiany;
	City startCity;
	MainPanel parent;
	

	
	Core(MainPanel parent){
		super();
		abort = false;
		this.parent=parent;
		
		double TL,Dc,Fn,d,n;
		TL=Double.parseDouble(parent.machinepanel.TL.getText());
		Dc=Double.parseDouble(parent.machinepanel.Dc.getText());
		Fn=Double.parseDouble(parent.machinepanel.Fn.getText());
		d=Double.parseDouble(parent.machinepanel.d.getText());
		n=Double.parseDouble(parent.machinepanel.n.getText());
		
		double Vc = (Math.PI * Dc * n) / 1000d;
		
		
		interwal_wymiany=(int)Math.floor(TL * (Vc/Fn) / d);
	}
	
	public void abort() {
		// zako�czenie dzia�ania algorytmu przed czasem
		abort = true;
	}
	
	public int getInterwal(){
		return interwal_wymiany;
	}
	
	public City getstartCity(){
		return startCity;
	}
	
}
