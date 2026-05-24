package billeteraVirtual;

import java.util.List;

import java.util.*;

public class Billetera implements IBilletera {
	
	// dni, usuario
	private Map<String, Usuario> usuarios;
	// cuit, empresa
    private Map<String, Empresa> empresas;
    // Acceso a cuentas por cvu
    private Map<String, Cuenta> cuentas;
    
    // Acceso a cuentas por alias
    private Map<String, Cuenta> cuentasAlias;
    
    private Map<Integer, Inversion> inversiones;
    
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
	public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email,
			String nombreContacto) {
		
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
		
		for(Cuenta c: usuarios.get(dniUsuario).getCuentas() )
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
		
		// Validar las que existan las cuentas
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
		
		// crear inverson y agregar a las estructuras de Billetera, Cuenta y Usuario
		Inversion i = new InversionRentaFija(c, monto, plazoDias);
		inversiones.put(i.getId(), i);
		c.agregarInversion(i);
		obtenerUsuarioTitularCuenta(cvu).agregarInversion(i);
		
		// agregar el movimiento a el historial global, al del usuario y cuenta
		historial.add(i);
		c.agregarMovimiento(i);
		obtenerUsuarioTitularCuenta(cvu).agregarMovimiento(i);
		
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
		
		// crear inverson y agregar a las estructuras de Billetera, Cuenta y Usuario
		Inversion i = new InversionDivisa(c, monto, plazoDias, divisa, tasa);
		inversiones.put(i.getId(), i);
		c.agregarInversion(i);
		obtenerUsuarioTitularCuenta(cvu).agregarInversion(i);
		
		// agregar el movimiento a el historial global, al del usuario y cuenta
		historial.add(i);
		c.agregarMovimiento(i);
		obtenerUsuarioTitularCuenta(cvu).agregarMovimiento(i);
		
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
		
		// crear inverson y agregar a las estructuras de Billetera, Cuenta y Usuario
		Inversion i = new InversionLiquidez(c, monto, plazoDias);
		inversiones.put(i.getId(), i);
		c.agregarInversion(i);
		obtenerUsuarioTitularCuenta(cvu).agregarInversion(i);
		
		// agregar el movimiento a el historial global, al del usuario y cuenta
		historial.add(i);
		c.agregarMovimiento(i);
		obtenerUsuarioTitularCuenta(cvu).agregarMovimiento(i);
		
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
	    i.cancelar();
	    
	 // devolver dinero
	    c.depositar(i.calcularRetorno());
	    i.modificarMonto(0);
		
	}

	@Override
	public String consultarCvu(String alias) {

		if(!cuentasAlias.containsKey(alias)) throw new IllegalArgumentException("El alias no existe en el sistema");
		
		return cuentasAlias.get(alias).getCvu();
	}

	@Override
	public List<String> consultarHistorialGlobal() {
		
		List<String> resultado = new ArrayList<>();

	    for(Movimiento m : historial)
	    {
	        resultado.add(m.toString());
	    }

	    return resultado;
	}

	@Override
	public List<String> consultarHistorialCuenta(String cvu) {
		
		Cuenta c = validarCuenta(cvu);
		
		List<String> resultado = new ArrayList<>();
		
		for(Movimiento m : c.getHistorial())
	    {
	        resultado.add(m.toString());
	    }

	    return resultado;
	}

	@Override
	public List<String> consultarHistorialUsuario(String dniUsuario) {
		
		if(!usuarios.containsKey(dniUsuario)) throw new IllegalArgumentException("El usuario no se encuentra registrado en el sistema");
		
		List<String> resultado = new ArrayList<>();
		
		for(Movimiento m :  usuarios.get(dniUsuario).getHistorial())
	    {
	        resultado.add(m.toString());
	    }

	    return resultado;
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

	    List<Cuenta> lista = new ArrayList<>(cuentas.values());

	    // ordenar de mayor a menor cantidad de movimientos
	    lista.sort((c1, c2) -> c2.volumenDeCuenta() - c1.volumenDeCuenta());

	    List<String> resultado = new ArrayList<>();

	    int limite = Math.min(cantidadTop, lista.size());

	    for(int i = 0; i < limite; i++)
	    {
	        resultado.add(lista.get(i).toString());
	    }

	    return resultado;
	}

	@Override
	public String toString() {
		return "Billetera: \n\nUsuarios : " + usuarios + "\nEmpresas : " + empresas + "\nCuentas : " + imprimirCuentas() + "\nHistorial : "
				+ historial;
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
	    StringBuilder sb = new StringBuilder();

	    for(Cuenta cuenta : cuentas.values())
	    {
	        sb.append(cuenta).append("\n");
	    }

	    return sb.toString();
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
	

}
