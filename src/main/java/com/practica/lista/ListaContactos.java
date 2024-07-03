package com.practica.lista;


import com.practica.genericas.FechaHora;
import com.practica.genericas.PosicionPersona;

public class ListaContactos {
	private NodoTemporal lista;
	private int size;
	
	/**
	 * Insertamos en la lista de nodos temporales, y a la vez inserto en la lista de nodos de coordenadas. 
	 * En la lista de coordenadas metemos el documento de la persona que está en esa coordenada 
	 * en un instante 
	 */
	public void insertarNodoTemporal(PosicionPersona p) {
		NodoTemporal aux = lista, ant = null;
		boolean encontrado = false;

		while (aux != null && aux.getFecha().compareTo(p.getFechaPosicion()) < 0) {
			ant = aux;
			aux = aux.getSiguiente();
		}

		if (aux != null && aux.getFecha().compareTo(p.getFechaPosicion()) == 0) {
			encontrado = true;
			insertarEnListaCoordenadas(aux, p);
		}

		if (!encontrado) {
			NodoTemporal nuevo = new NodoTemporal();
			nuevo.setFecha(p.getFechaPosicion());
			nuevo.setListaCoordenadas(new NodoPosicion(p.getCoordenada(), 1, null));
			nuevo.setSiguiente(aux);
			if (ant == null) lista = nuevo;
			else ant.setSiguiente(nuevo);
			this.size++;
		}
	}

	private void insertarEnListaCoordenadas(NodoTemporal aux, PosicionPersona p) {
		NodoPosicion np = aux.getListaCoordenadas(), npAnt = null;
		while (np != null && !np.getCoordenada().equals(p.getCoordenada())) {
			npAnt = np;
			np = np.getSiguiente();
		}
		if (np != null) {
			np.setNumPersonas(np.getNumPersonas() + 1);
		} else {
			NodoPosicion nuevoNp = new NodoPosicion(p.getCoordenada(), 1, null);
			if (npAnt == null) aux.setListaCoordenadas(nuevoNp);
			else npAnt.setSiguiente(nuevoNp);
		}
	}

	public int personasEnCoordenadas () {
		NodoPosicion aux = this.lista.getListaCoordenadas();
		if(aux==null)
			return 0;
		else {
			int cont;
			for(cont=0;aux!=null;) {
				cont += aux.getNumPersonas();
				aux=aux.getSiguiente();
			}
			return cont;
		}
	}
	
	public int tamanioLista () {
		return this.size;
	}

	public String getPrimerNodo() {
		NodoTemporal aux = lista;
		String cadena = aux.getFecha().getFecha().toString();
		cadena+= ";" +  aux.getFecha().getHora().toString();
		return cadena;
	}

	/**
	 * Métodos para comprobar que insertamos de manera correcta en las listas de 
	 * coordenadas, no tienen una utilidad en sí misma, más allá de comprobar que
	 * nuestra lista funciona de manera correcta.
	 */
	public int numPersonasEntreDosInstantes(FechaHora inicio, FechaHora fin) {
		if(this.size==0)
			return 0;
		NodoTemporal aux = lista;
		int cont = 0;
		cont = 0;
		while(aux!=null) {
			if(aux.getFecha().compareTo(inicio)>=0 && aux.getFecha().compareTo(fin)<=0) {
				NodoPosicion nodo = aux.getListaCoordenadas();
				while(nodo!=null) {
					cont = cont + nodo.getNumPersonas();
					nodo = nodo.getSiguiente();
				}				
				aux = aux.getSiguiente();
			}else {
				aux=aux.getSiguiente();
			}
		}
		return cont;
	}
	
	
	
	public int numNodosCoordenadaEntreDosInstantes(FechaHora inicio, FechaHora fin) {
		if(this.size==0)
			return 0;
		NodoTemporal aux = lista;
		int cont = 0;
		cont = 0;
		while(aux!=null) {
			if(aux.getFecha().compareTo(inicio)>=0 && aux.getFecha().compareTo(fin)<=0) {
				NodoPosicion nodo = aux.getListaCoordenadas();
				while(nodo!=null) {
					cont = cont + 1;
					nodo = nodo.getSiguiente();
				}				
				aux = aux.getSiguiente();
			}else {
				aux=aux.getSiguiente();
			}
		}
		return cont;
	}
	
	
	
	@Override
	public String toString() {
		String cadena="";
		int cont;
		cont=0;
		NodoTemporal aux = lista;
		for(cont=1; cont<size; cont++) {
			cadena += aux.getFecha().getFecha().toString();
			cadena += ";" +  aux.getFecha().getHora().toString() + " ";
			aux=aux.getSiguiente();
		}
		cadena += aux.getFecha().getFecha().toString();
		cadena += ";" +  aux.getFecha().getHora().toString();
		return cadena;
	}
	
	
	
}
