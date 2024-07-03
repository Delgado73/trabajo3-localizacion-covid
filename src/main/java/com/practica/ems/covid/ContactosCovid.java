package com.practica.ems.covid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.practica.excecption.EmsDuplicateLocationException;
import com.practica.excecption.EmsDuplicatePersonException;
import com.practica.excecption.EmsInvalidNumberOfDataException;
import com.practica.excecption.EmsInvalidTypeException;
import com.practica.excecption.EmsLocalizationNotFoundException;
import com.practica.excecption.EmsPersonNotFoundException;
import com.practica.genericas.Constantes;
import com.practica.genericas.Coordenada;
import com.practica.genericas.FechaHora;
import com.practica.genericas.Persona;
import com.practica.genericas.PosicionPersona;
import com.practica.lista.ListaContactos;

public class ContactosCovid {
	private Poblacion poblacion;
	private Localizacion localizacion;
	private ListaContactos listaContactos;

	public ContactosCovid() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	public Poblacion getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(Poblacion poblacion) {
		this.poblacion = poblacion;
	}

	public Localizacion getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(Localizacion localizacion) {
		this.localizacion = localizacion;
	}

	public ListaContactos getListaContactos() {
		return listaContactos;
	}

	public void setListaContactos(ListaContactos listaContactos) {
		this.listaContactos = listaContactos;
	}

	public void loadData(String data, boolean reset) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException,
			EmsDuplicatePersonException, EmsDuplicateLocationException {
		if (reset) {
			resetData();
		}
		String[] datas = dividirEntrada(data);
		for (String linea : datas) {
			processLine(linea);
		}
	}

	private void resetData() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	private void processLine(String linea) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException, EmsDuplicatePersonException, EmsDuplicateLocationException {
		String[] datos = dividirLineaData(linea);
		switch (datos[0]) {
			case "PERSONA":
				if (datos.length != Constantes.MAX_DATOS_PERSONA) {
					throw new EmsInvalidNumberOfDataException("El número de datos para PERSONA es menor de 8");
				}
				this.poblacion.addPersona(crearPersona(datos));
				break;
			case "LOCALIZACION":
				if (datos.length != Constantes.MAX_DATOS_LOCALIZACION) {
					throw new EmsInvalidNumberOfDataException("El número de datos para LOCALIZACION es menor de 6");
				}
				PosicionPersona pp = crearPosicionPersona(datos);
				this.localizacion.addLocalizacion(pp);
				this.listaContactos.insertarNodoTemporal(pp);
				break;
			default:
				throw new EmsInvalidTypeException();
		}
	}

	public void loadDataFile(String fichero, boolean reset) {
		if (reset) {
			resetData();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(new File(fichero)))) {
			String data;
			while ((data = br.readLine()) != null) {
				processLine(data.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int findPersona(String documento) throws EmsPersonNotFoundException, EmsLocalizationNotFoundException {
		return findElement(() -> this.poblacion.findPersona(documento));
	}

	public int findLocalizacion(String documento, String fecha, String hora) throws EmsLocalizationNotFoundException, EmsPersonNotFoundException {
		return findElement(() -> this.localizacion.findLocalizacion(documento, fecha, hora));
	}

	private int findElement(FindElementFunction findFunction) throws EmsPersonNotFoundException, EmsLocalizationNotFoundException {
		try {
			return findFunction.find();
		} catch (EmsPersonNotFoundException | EmsLocalizationNotFoundException e) {
			throw e;
		}
	}

	public List<PosicionPersona> localizacionPersona(String documento) throws EmsPersonNotFoundException {
		List<PosicionPersona> lista = new ArrayList<>();
		for (PosicionPersona pp : this.localizacion.getLista()) {
			if (pp.getDocumento().equals(documento)) {
				lista.add(pp);
			}
		}
		if (lista.isEmpty()) {
			throw new EmsPersonNotFoundException();
		}
		return lista;
	}

	public boolean delPersona(String documento) throws EmsPersonNotFoundException {
		for (int i = 0; i < this.poblacion.getLista().size(); i++) {
			if (this.poblacion.getLista().get(i).getDocumento().equals(documento)) {
				this.poblacion.getLista().remove(i);
				return true;
			}
		}
		throw new EmsPersonNotFoundException();
	}

	private String[] dividirEntrada(String input) {
		return input.split("\\n");
	}

	private String[] dividirLineaData(String data) {
		return data.split(";");
	}

	private Persona crearPersona(String[] data) {
		Persona persona = new Persona();
		for (int i = 1; i < Constantes.MAX_DATOS_PERSONA; i++) {
			setPersonaData(persona, data[i], i);
		}
		return persona;
	}

	private void setPersonaData(Persona persona, String value, int index) {
		switch (index) {
			case 1:
				persona.setDocumento(value);
				break;
			case 2:
				persona.setNombre(value);
				break;
			case 3:
				persona.setApellidos(value);
				break;
			case 4:
				persona.setEmail(value);
				break;
			case 5:
				persona.setDireccion(value);
				break;
			case 6:
				persona.setCp(value);
				break;
			case 7:
				persona.setFechaNacimiento(parsearFecha(value));
				break;
		}
	}

	private PosicionPersona crearPosicionPersona(String[] data) {
		PosicionPersona posicionPersona = new PosicionPersona();
		float latitud = 0;
		for (int i = 1; i < Constantes.MAX_DATOS_LOCALIZACION; i++) {
			latitud = setPosicionPersonaData(posicionPersona, data, latitud, i);
		}
		return posicionPersona;
	}

	private float setPosicionPersonaData(PosicionPersona posicionPersona, String[] data, float latitud, int index) {
		String value = data[index];
		switch (index) {
			case 1:
				posicionPersona.setDocumento(value);
				break;
			case 2:
				posicionPersona.setFechaPosicion(parsearFecha(value, data[3]));
				break;
			case 4:
				latitud = Float.parseFloat(value);
				break;
			case 5:
				posicionPersona.setCoordenada(new Coordenada(latitud, Float.parseFloat(value)));
				break;
		}
		return latitud;
	}

	private FechaHora parsearFecha(String fecha) {
		String[] valores = fecha.split("\\/");
		int dia = Integer.parseInt(valores[0]);
		int mes = Integer.parseInt(valores[1]);
		int anio = Integer.parseInt(valores[2]);
		return new FechaHora(dia, mes, anio, 0, 0);
	}

	private FechaHora parsearFecha(String fecha, String hora) {
		String[] fechaValores = fecha.split("\\/");
		String[] horaValores = hora.split("\\:");
		int dia = Integer.parseInt(fechaValores[0]);
		int mes = Integer.parseInt(fechaValores[1]);
		int anio = Integer.parseInt(fechaValores[2]);
		int minuto = Integer.parseInt(horaValores[0]);
		int segundo = Integer.parseInt(horaValores[1]);
		return new FechaHora(dia, mes, anio, minuto, segundo);
	}

	@FunctionalInterface
	private interface FindElementFunction {
		int find() throws EmsPersonNotFoundException, EmsLocalizationNotFoundException;
	}
}