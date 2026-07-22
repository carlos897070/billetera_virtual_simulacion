package billeteraVirtual;

import java.util.*;

public class Billetera implements IBilletera {
	
	// Acceso a usuarios por dni
	private Map<String, Usuario> usuarios;
	
	//Acceso a empresas por cuit
    private Map<String, Empresa> empresas;
    
    // Acceso a cuentas por cvu
    private Map<String, Cuenta> cuentas;
    
    // Acceso a cuentas por alias
    private Map<String, Cuenta> cuentasAlias;
    
    // Acceso a inversiones por id
    private Map<Integer, Inversion> inversiones;
    
    // Lista que guarda el historial de actividades
    private List<Movimiento> historial;
	
	public Billetera() {
		
		 this.usuarios = new HashMap<>();
		 this.empresas = new HashMap<>();
		 this.cuentas = new HashMap<>();
		 this.historial = new ArrayList<>();
		 this.cuentasAlias = new HashMap<>();
		 this.inversiones = new HashMap<>();
	}

	
	@Override
	public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
		
		if(empresas.containsKey(cuit)) throw new IllegalArgumentException("La empresa ya se encuentra registrada en el sistema");
		
		Empresa empresa = new Empresa(cuit, nombreFantasia, telefono, email, nombreContacto);
		
		empresas.put(empresa.getCuit(), empresa);
		
	}
	
	@Override
	public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
		
		if(!empresas.containsKey(cuitEmpresa)) throw new IllegalArgumentException("La empresa no se encuentra registrada en el sistema");
		
		Set<String> autorizados = empresas.get(cuitEmpresa).getAutorizados();
		
		if(autorizados.contains(dniAutorizado)) throw new IllegalArgumentException("Esta persona ya se encuentra"
				+ "autorizada para operar");
		
		autorizados.add(dniAutorizado);
		
	}

	
	
	@Override
	public void registrarUsuario(String dni, String nombre, String telefono, String email) {
		
		if(usuarios.containsKey(dni)) throw new IllegalArgumentException("El usuario ya se encuentra registrado en el sistema");
		
		Usuario us = new Usuario(dni, nombre, telefono, email);
		
		usuarios.put(us.getDni(), us);
		
	}
	

	@Override
	public String crearCuentaRegular(String dniUsuario, String alias) {
		
		validarUsuarioYAlias(dniUsuario, alias);
		
		// creo la cuenta
		Cuenta nueva = new CuentaRegular(alias, dniUsuario);
		
		// agrego la cuenta al map de billetera, al map de usuarios y al map de cuentaAlias
		agregarCuentas(dniUsuario, nueva);
		
		return nueva.getCvu();
	}
	

	@Override
	public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {
		
		validarUsuarioYAlias(dniUsuario, alias);
		
		// pregunto si el saldo a depositar es menor al minimo requerido para crear esta cuenta
		if(depositoInicial < CuentaPremium.SALDO_MINIMO) throw new IllegalArgumentException("El saldo para crear esta cuenta es insuficiente");
		
		Cuenta nueva = new CuentaPremium(alias, dniUsuario);
		
		// se hace el deposito inicial
		nueva.depositar(depositoInicial);
		
		// agrego la cuenta al map de billetera, al map de usuarios y al map de cuentaAlias
		agregarCuentas(dniUsuario, nueva);
		
		return nueva.getCvu();
	}
	
	// Metodo que crea una cuenta premium automaticamente con el saldo minimo
	
	public String crearCuentaPremium(String dniUsuario, String alias)
	{
		return crearCuentaPremium(dniUsuario, alias, CuentaPremium.SALDO_MINIMO);
	}
	

	@Override
	public String crearCuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {

		validarUsuarioYAlias(dniUsuario, alias);
		
		// verifico que la empresa se encuentre registrada
		if(!empresas.containsKey(cuitEmpresa)) throw new IllegalArgumentException("La empresa no se encuentra registrada en el sistema");
		
		// lista de personas autorizadas para operar en nombre de la empresa
		Set<String> autorizados = empresas.get(cuitEmpresa).getAutorizados();
		
		// compruebo si el dni ingresado se encuentra autorizado para operar
		if(!autorizados.contains(dniUsuario)) throw new 
		IllegalArgumentException("El dni ingresado no se encuentra autorizado por esta empresa para operar");
		
		Cuenta nueva = new CuentaCorporativa(alias, dniUsuario, cuitEmpresa);
		
		// agrego la cuenta al map de billetera, al map de usuarios y al map de cuentaAlias
		agregarCuentas(dniUsuario, nueva);
		
		return nueva.getCvu();
	}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																									

	@Override
	public List<String> obtenerCuentas(String dniUsuario) {
		
		if(!usuarios.containsKey(dniUsuario)) throw new IllegalArgumentException("El usuario no se encuentra registrado en el sistema");
		
		List<String> cuentas = new ArrayList<>();
		
		for(Cuenta c : usuarios.get(dniUsuario).getCuentas())
		{
			cuentas.add(c.toString());
		}
		
		return cuentas;
		
	}

	@Override
	public double obtenerSaldoDisponible(String cvu) {
		// validar cuenta
		Cuenta c = validarCuenta(cvu);
		return c.getSaldo();
	}

	@Override
	public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {
		
		// Validar que existan las cuentas
		Cuenta origen = validarCuenta(cvuOrigen);
		Cuenta destino = validarCuenta(cvuDestino);
		
		// validar el monto a transferir
		if(monto <= 0) throw new IllegalArgumentException("El monto ingresado es invalido");
		
		// proceso de transfeencia
		origen.retirarDinero(monto);
		destino.depositar(monto);
		
		// Crear transferencia
		Transferencia t = new Transferencia(origen, destino, monto);
		
		// Agregar el movimiento los historiales
		historial.add(t);
		obtenerUsuarioTitularCuenta(cvuOrigen).agregarMovimiento(t);
		obtenerUsuarioTitularCuenta(cvuDestino).agregarMovimiento(t);
		origen.agregarMovimiento(t);
		destino.agregarMovimiento(t);
		t.aprobar();
	}

	@Override
	public int realizarInversionRentaFija(String dni, String cvu, double monto, int plazoDias) {
		
		// Validar todos los parametros
		if(!usuarios.containsKey(dni)) throw new IllegalArgumentException("El dni ingresado no corresponde a un usuario registrado");
		Cuenta c = validarCuenta(cvu);
		if(monto <= 0) throw new IllegalArgumentException("El monto ingresado es invalido");
		if(plazoDias <= 0) throw new IllegalArgumentException("El plazo ingresado es invalido");
		
		// Validar que la cuenta le corresponda al usuario
		if(!c.getDniTitular().equals(dni)) throw new IllegalArgumentException("La cuenta no pertenece al usuario");
		
		// Extraer dinero para la inversion
		c.retirarDinero(monto);
		c.depositarEnSaldoDeInversiones(monto);
		
		// crear inverson y agregar a las estructuras de Billetera, Cuenta y Usuario
		Inversion i = new InversionRentaFija(c, monto, plazoDias);
		
		// agrega la inversion a las estructuras de Billetera, Cuenta y Usuario, y agrega la inversion a los historiales
		registrarInversion(i, c);
		i.aprobar();
		
		return i.getId();
	}

	@Override
	public int realizarInversionDivisa(String dni, String cvu, double monto, int plazoDias, String divisa, double tasa) {
		
		// Validar todos los parametros
		if(!usuarios.containsKey(dni)) throw new IllegalArgumentException("El dni ingresado no corresponde a un usuario registrado");
		Cuenta c = validarCuenta(cvu);
		if(monto <= 0) throw new IllegalArgumentException("El monto ingresado es invalido");
		if(plazoDias <= 0) throw new IllegalArgumentException("El plazo ingresado es invalido");
		
		// Validar que la cuenta le corresponda al usuario
		if(!c.getDniTitular().equals(dni)) throw new IllegalArgumentException("La cuenta no pertenece al usuario");
		
		// Validar tasa
		if(tasa < 0) throw new IllegalArgumentException("Tasa invalida");
		
		// Extraer dinero para la inversion
		c.retirarDinero(monto);
		c.depositarEnSaldoDeInversiones(monto);
		
		// crear inverson y agregar a las estructuras de Billetera, Cuenta y Usuario
		Inversion i = new InversionDivisa(c, monto, plazoDias, divisa, tasa);
		
		// agrega la inversion las estructuras de Billetera, Cuenta y Usuario, y agrega la inversion a los historiales
		registrarInversion(i, c);
		i.aprobar();
		
		return i.getId();
	}

	@Override
	public int realizarInversionLiquidez(String dni, String cvu, double monto, int plazoDias) {
		
		// Validar todos los parametros
		if(!usuarios.containsKey(dni)) throw new IllegalArgumentException("El dni ingresado no corresponde a un usuario registrado");
		Cuenta c = validarCuenta(cvu);
		if(monto < 20000000) throw new IllegalArgumentException("El monto es insuficiente para realizar esta inversion");
		if(plazoDias <= 0) throw new IllegalArgumentException("El plazo ingresado es invalido");
		
		// Validar que la cuenta le corresponda al usuario
		if(!c.getDniTitular().equals(dni)) throw new IllegalArgumentException("La cuenta no pertenece al usuario");
		
		// Validar que la Cuenta sea corporativa
		if(!(c instanceof CuentaCorporativa)) throw new IllegalArgumentException("Solo las cuentas corporativas pueden operar FLE");
		
		// Extraer dinero para la inversion
		c.retirarDinero(monto);
		c.depositarEnSaldoDeInversiones(monto);
		
		// crear inverson
		Inversion i = new InversionLiquidez(c, monto, plazoDias);
		
		// agrega la inversion las estructuras de Billetera, Cuenta y Usuario, y agrega la inversion a los historiales
		registrarInversion(i, c);
		i.aprobar();
		
		return i.getId();
	}


	@Override
	public void precancelarInversion(String dni, String cvu, int idInversion) {
		
		// Validar parametros
		if(!usuarios.containsKey(dni)) throw new IllegalArgumentException("El usuario no se encuentra registrado en el sistem");
		Cuenta c = validarCuenta(cvu);
		if(!inversiones.containsKey(idInversion)) throw new IllegalArgumentException("El ID de la inversion no se encuentra en el sistema");
		
		Inversion i = inversiones.get(idInversion);
		
		// validar titularidad
	    if(!c.getDniTitular().equals(dni)) throw new IllegalArgumentException("La cuenta no pertenece al usuario");
	    // validar que la inversion pertenezca a la cuenta
	    if(!i.getCuenta().getCvu().equals(c.getCvu())) throw new IllegalArgumentException("La inversion no pertenece a la cuenta");
	    // validar estado
	    if(!i.estaActiva()) throw new IllegalArgumentException("La inversion ya fue cancelada");
	    // Validar si la inversion se puede cancelar
	    if(!i.esPrecancelable()) throw new IllegalArgumentException("La inversion no se puede cancelar");

	    // cancelar inversion
	    i.desactivar();
	    
	    // devolver dinero
	    c.depositar(i.calcularRetorno());
	    c.retirarDineroDeInversion(i.getMonto());
	    //i.modificarMonto(0);
	    i.cancelar();
		
	}

	@Override
	public String consultarCvu(String alias) {

		if(!cuentasAlias.containsKey(alias)) throw new IllegalArgumentException("El alias no existe en el sistema");
		
		return cuentasAlias.get(alias).getCvu();
	}
	
	public String consultarCvu(Cuenta c)
	{
	    return consultarCvu(c.getAlias());
	}

	@Override
	public List<String> consultarHistorialGlobal() {
		
		List<String> historialGlobal = new ArrayList<>();

	    for(Movimiento m : historial)
	    {
	    	historialGlobal.add(m.toString());
	    }

	    return historialGlobal;
	}

	@Override
	public List<String> consultarHistorialCuenta(String cvu) {
		
		Cuenta c = validarCuenta(cvu);
		
		List<String> historialCuenta = new ArrayList<>();
		
		for(Movimiento m : c.getHistorial())
	    {
			historialCuenta.add(m.toString());
	    }

	    return historialCuenta;
	}

	@Override
	public List<String> consultarHistorialUsuario(String dniUsuario) {
		
		if(!usuarios.containsKey(dniUsuario)) throw new IllegalArgumentException("El usuario no se encuentra registrado en el sistema");
		
		List<String> historialUsuario = new ArrayList<>();
		
		for(Movimiento m :  usuarios.get(dniUsuario).getHistorial())
	    {
			historialUsuario.add(m.toString());
	    }

	    return historialUsuario;
	}

	@Override
	public double obtenerTotalInvertido(String dniUsuario) {
		
		if(!usuarios.containsKey(dniUsuario)) throw new IllegalArgumentException("El usuario no se encuentra registrado en el sistema");
		double total = 0;
		
		for(Inversion i : usuarios.get(dniUsuario).getInversiones())
		{
			if(i.estaActiva()) total += i.getMonto();
		}
		
		return total;
	}

	@Override
	public List<String> cuentasConMayorVolumen(int cantidadTop) {

	    if(cantidadTop <= 0) throw new IllegalArgumentException("Cantidad invalida");

	    List<Cuenta> cuntasExistentes = new ArrayList<>(cuentas.values());

	    // ordenar de mayor a menor cantidad de movimientos
	    cuntasExistentes.sort((c1, c2) -> c2.volumenDeCuenta() - c1.volumenDeCuenta());

	    List<String> top = new ArrayList<>();

	    /* Este limite se establece en caso que las cuentas existentes sean menor al top ingresado, en ese caso
	     * solo se imprimiran las existentes*/
	    
	    int limite = Math.min(cantidadTop, cuntasExistentes.size());

	    for(int i = 0; i < limite; i++)
	    {
	        top.add(cuntasExistentes.get(i).toString());
	    }

	    return top;
	}
	
	public void procesarInversionesQueVencenHoy()
	{
		for(Inversion i : inversiones.values())
		{
			if(i.estaActiva() && i.getFechaVencimiento().equals(Utilitarios.hoy()))
			{
				i.getCuenta().retirarDineroDeInversion(i.getMonto());
				double dineroGenerado = i.calcularRetorno();
				i.getCuenta().depositar(dineroGenerado);
				i.desactivar();
			}
		}
	}

	@Override
	public String toString() {
		return "\nBilletera: \n\nUsuarios: " + imprimirUsuarios() + "\nEmpresas: " + imprimirEmpresas() + 
				"\nCuentas:\n" + imprimirCuentas() + "\nHistorial : " + historial;
	}
	
	
	// Metodos auxiliares
	
	private void validarUsuarioYAlias(String dni, String alias)
	{
		// Pregunto si no esta registrado el usuario ingresado
		if(!usuarios.containsKey(dni)) throw new IllegalArgumentException("El usuario no se encuentra registrado en el sistema");
				
		// pregunto si existe el alias ingresado
		if(cuentasAlias.containsKey(alias)) throw new IllegalArgumentException("Este alias ya se encuentra asociado a una cuenta");
	}
	
	private void agregarCuentas(String dni, Cuenta cuenta)
	{
		cuentas.put(cuenta.getCvu(), cuenta);
		cuentasAlias.put(cuenta.getAlias(), cuenta);
		usuarios.get(dni).getCuentas().add(cuenta);
	}
	
	private String imprimirCuentas()
	{
	    StringBuilder nuevo = new StringBuilder();
	    
	    Iterator<Map.Entry<String, Cuenta>> it = cuentas.entrySet().iterator();
	    
	    while(it.hasNext())
	    {
	    	Map.Entry<String, Cuenta> e = it.next();
	    	nuevo.append(e.getValue()).append("\n");
	    }
	    
	    return nuevo.toString();
	}
	
	private String imprimirUsuarios()
	{
		StringBuilder nuevo = new StringBuilder();
		
		Iterator<Map.Entry<String, Usuario>> it = usuarios.entrySet().iterator();
	    
	    while(it.hasNext())
	    {
	    	Map.Entry<String, Usuario> e = it.next();
	    	nuevo.append(e.getValue()).append("\n");
	    }
		
		return nuevo.toString();
	}
	
	private String imprimirEmpresas()
	{
		StringBuilder nuevo = new StringBuilder();
		
		Iterator<Map.Entry<String, Empresa>> it = empresas.entrySet().iterator();
	    
	    while(it.hasNext())
	    {
	    	Map.Entry<String, Empresa> e = it.next();
	    	nuevo.append(e.getValue()).append("\n");
	    }
		
		return nuevo.toString();
	}
	
	
	public Cuenta obtenerCuenta(String alias)
	{
		if(!cuentasAlias.containsKey(alias)) throw new IllegalArgumentException("El alias ingresado no existe");
		
		return cuentasAlias.get(alias);
	}
	
	private Usuario obtenerUsuarioTitularCuenta(String cvu)
	{
		if(!cuentas.containsKey(cvu)) throw new IllegalArgumentException("El cvu ingresado es invalido");
		Cuenta c = cuentas.get(cvu);
		
		return usuarios.get(c.getDniTitular());
	}
	
	private Cuenta validarCuenta(String cvu)
	{
	    if(!cuentas.containsKey(cvu))
	        throw new IllegalArgumentException("El cvu ingresado es invalido");

	    return cuentas.get(cvu);
	}
	
	private void registrarInversion(Inversion i, Cuenta c)
	{
		inversiones.put(i.getId(), i);
		c.agregarInversion(i);
		
		Usuario u = obtenerUsuarioTitularCuenta(c.getCvu());
		
		u.agregarInversion(i);
		
		historial.add(i);
		c.agregarMovimiento(i);
		u.agregarMovimiento(i);
		
	}
	
}