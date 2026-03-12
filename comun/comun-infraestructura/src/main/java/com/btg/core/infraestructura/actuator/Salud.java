package com.btg.core.infraestructura.actuator;

import com.btg.core.infraestructura.excepcion.ExcepcionTecnica;//a�adir

/**
 * Interface que tiene por objetivo ser implementada por todos los bloques 
 * que quieran utilizar HealthCheck
 * 
 * @author sergio.villamizar
 *
 */

public interface Salud  {//a�adir
	/**
	 * Registra los bloques implementados
	 */
	public void registrarBloque();
	
	/**
	 * Valida la salud del bloque
	 * @throws ExepcionBloqueSinServicio
	 */
	public void verificar() throws ExcepcionTecnica;//a�adir

}
