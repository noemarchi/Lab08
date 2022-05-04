package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model 
{
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> idMap;
	private ExtFlightDelaysDAO dao;
	
	public Model()
	{
		idMap = new HashMap<Integer, Airport>();
		dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMap);
	}
	
	public void creaGrafo(Integer distanzaMedia)
	{
		// CREO IL GRAFO VUOTO
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// AGGIUNGO I VERTICI
		Graphs.addAllVertices(grafo, idMap.values());
		
		// AGGIUNGO GLI ARCHI
		for(Rotta rotta: dao.getRotte(idMap, distanzaMedia))
		{
			DefaultWeightedEdge edge = grafo.getEdge(rotta.getA1(), rotta.getA2());
			
			// l'arco non esiste
			if(edge == null)
			{
				Graphs.addEdge(grafo, rotta.getA1(), rotta.getA2(), rotta.getPeso());
			}
			else
			{
				// l'arco esiste già
				// aggiorniamo il peso --> media tra 
				// - peso dell'arco già presente 
				// - peso della rotta "nuova"
				
				double oldPeso = grafo.getEdgeWeight(edge);
				double newPeso = (oldPeso + rotta.getPeso())/2;
				
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
	}
	
	public int nVertici()
	{
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi()
	{
		return this.grafo.edgeSet().size();
	}

	public List<Rotta> getRotte()
	{
		List<Rotta> rotte = new ArrayList<Rotta>();
		
		for(DefaultWeightedEdge e: grafo.edgeSet())
		{
			rotte.add(new Rotta(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
		}
		
		return rotte;
	}
}
