package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {

	// Coda degli eventi
	private PriorityQueue<Event> queue = new PriorityQueue<Event>();
	
	// Parametri di simulazione
	private int NC = 10; // number of cars
	private Duration T_IN = Duration.of(10, ChronoUnit.MINUTES); // intervallo tra i clienti
	
	private final LocalTime oraApertura = LocalTime.of(8, 00);
	private final LocalTime oraChiusura = LocalTime.of(17, 00);
	
	// Modello del mondo
	private int nAuto; // auto disponibili nel deposito (tra 0 ed NC)
	
	// Valori da calcolare
	private int clienti;
	private int insoddisfatti;
	
	// Metodi per impostare i parametri
	public void setNumCars(int N) {
		this.NC = N;
	}

	public void setClientFrequency(Duration d) {
		this.T_IN = d;
	}

	// Metodi per restituire i risultati
	public int getClienti() {
		return clienti;
	}

	public int getInsoddisfatti() {
		return insoddisfatti;
	}
	
	// Simulazione vera e propria
	public void run() {
		// Preparazione iniziale (impostare variabili mondo + eventi della coda eventi)
		this.nAuto = this.NC;
		this.clienti = this.insoddisfatti = 0;
		
		this.queue.clear();
		LocalTime oraArrivoCliente = this.oraApertura;
		
		do {
			Event e = new Event(oraArrivoCliente, EventType.NEW_CLIENT);
			this.queue.add(e);
			oraArrivoCliente = oraArrivoCliente.plus(this.T_IN);
		} while(oraArrivoCliente.isBefore(this.oraChiusura));
		
		// Esecuzione del ciclo di simulazione
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
//			System.out.println(e);
			processEvent(e);			
		}
	}
	
	private void processEvent(Event e) {
		
		switch (e.getType()) {
		case NEW_CLIENT:
			
			if(this.nAuto>0) {
				// Cliente viene servito, auto noleggiata
				// 1) Aggiorno modello del mondo
				this.nAuto--;
				// 2) Aggiorno risultati della simulazione
				this.clienti++;
				// 3) Inserisco eventuali nuovi eventi
				double num = Math.random(); // [0,1)
				Duration travel;
				if(num<1.0/3.0)
					travel = Duration.of(1, ChronoUnit.HOURS);
				else if(num<2.0/3.0)
					travel = Duration.of(2, ChronoUnit.HOURS);
				else
					travel = Duration.of(3, ChronoUnit.HOURS);
				
//				System.out.println(travel);
				Event nuovo = new Event(e.getTime().plus(travel), EventType.CAR_RETURNED);		
				this.queue.add(nuovo);
				
			}else {
				// Cliente insoddisfatto
				// 1) Aggiorno modello del mondo
				// Non ho niente da fare
				// 2) Aggiorno risultati della simulazione
				this.clienti++;
				this.insoddisfatti++;
				// 3) Inserisco eventuali nuovi eventi
				// Non ce ne sono da aggiungere
			}
			
			break;

		case CAR_RETURNED:
			
			this.nAuto++;
						
			break;
		}
	}
}
