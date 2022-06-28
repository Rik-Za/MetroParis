package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata,DefaultEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	
	public List<Fermata> getFermate(){	//In questo modo interrogo una volta sola il DAO
		if(this.fermate==null) {
		MetroDAO dao = new MetroDAO();
		this.fermate=dao.getAllFermate();
		
		this.fermateIdMap = new HashMap<>();
		for(Fermata f: fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		}
		return this.fermate;
	}
	
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){
		creaGrafo();
		Map<Fermata,Fermata> alberoInverso = visitaGrafo(partenza);
		Fermata corrente = arrivo;
		List<Fermata> percorso = new ArrayList<>();
		while(corrente!=null) {
			percorso.add(0, corrente);
			corrente=alberoInverso.get(corrente);
		}
		return percorso;
	}
	
	public void creaGrafo() {
		this.grafo=new SimpleDirectedGraph<Fermata,DefaultEdge>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		
		//List<Fermata> fermate = dao.getAllFermate(); //fatto in un metodo a parte (getFermate)
		/*Map<Integer,Fermata> fermateIdMap = new HashMap<>();
		for(Fermata f: fermate)
			fermateIdMap.put(f.getIdFermata(), f);*/
		
		Graphs.addAllVertices(this.grafo, getFermate());	//Riempio il grafo con tutti i vertici
		
		//METODO 1: Itero su ogni coppia di vertici
		/*for(Fermata partenza: fermate) {
			for(Fermata arrivo: fermate) {
				if(dao.isFermateConnesse(partenza, arrivo)) //se esiste ALMENO UNA connessione tra partenza ed arrivo
					this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		
		//METODO 2: Dato ciascun vertice, trova i vertici ad esso adiacenti
		/*for(Fermata partenza: fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id: idConnesse) {
				Fermata arrivo = null;
				for(Fermata f : fermate)	//al posto di 'fermate' potevo scrivere this.grafo.vertexSet
					if(f.getIdFermata()==id) {
						arrivo=f;
						break;
					}
						
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		
		//METODO 2b: Il DAO restituisce un elenco di oggetti fermata
		/*for(Fermata partenza: fermate) {
			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		//METODO 2c: Il DAO restituisce un elenco di ID numerici che converto in oggetti tramite una Map<Integer,Fermata> - "Identity Map"
		/*for(Fermata partenza: fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id: idConnesse) {
				Fermata arrivo = fermateIdMap.get(id);
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
			
			
		
		//METODO 3: Faccio una sola query che mi restituisce le coppie di framte da collegare (3c, usando la Map)
		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaId coppia: fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}
		//Per la stampa
		/*System.out.println(this.grafo);
		System.out.println("Vertici = "+this.grafo.vertexSet().size());	//Trovo il numero di vertici
		System.out.println("Archi = "+this.grafo.edgeSet().size());	//Trovo il numero di archi
		visitaGrafo(fermate.get(0));*/
	}
	
	public Map<Fermata,Fermata> visitaGrafo(Fermata partenza) {	//trova i vertici raggiungibili a partire da un nodo di partenza
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);
		Map<Fermata,Fermata> alberoInverso = new HashMap<>();
		alberoInverso.put(partenza, null);	//Il precedente del nodo di partenza lo poniamo noi a null, quando scandisco l'albero a partire dal fondo, questa è la condizione che mi fa capire che sono giunto al nodo di partenza
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, this.grafo));
		Fermata f;
		while(visita.hasNext())
			{
				f = visita.next();
				//System.out.println(f);
			}
			return alberoInverso;
		}
	
	
	

}
