package billeteraVirtual;

public class Pruebas {
	
	public static void main(String[] args)
	{
		Billetera billetera = new Billetera();
    	Empresa emp = new Empresa("456", "fintech", "445566", "fin", "carlos");
    	Usuario us = new Usuario("34887900", "rafa", "1154657899", "rafa123@gmail.com");
    	
    	billetera.registrarUsuario("34887900", "rafa", "1154657899", "rafa123@gmail.com");
    	billetera.registrarUsuario("456", "pablo", "1166666666", "pablin6@gmail.com");
    	
    	billetera.registrarEmpresa("444444444", "fintech", "1122445566", "fintech@gmail.com", "pepe");
    	
    	billetera.agregarPersonaAutorizada("444444444", "34887900");
    	;
    	
    	billetera.crearCuentaRegular("34887900", "cometa.barco");
    	
    	billetera.crearCuentaPremium("34887900", "motor.16", 500000);
    	
    	billetera.crearCuentaCorporativa("34887900", "corp.3", "444444444");
    	
    	billetera.obtenerCuenta("cometa.barco").depositar(85000);
    	
    	billetera.realizarTransferencia("0000003100000000000001", "0000003100000000000002", 15000);
    	
    	billetera.realizarInversionRentaFija("34887900", "0000003100000000000001", 3000, 30);
    	
    	Utilitarios.actualizarCotizacion("USD", 1350);
    	
    	billetera.realizarInversionDivisa("34887900", "0000003100000000000002", 2000, 30, "USD", 0.1);
    	
    	
    	
    	System.out.println(billetera.obtenerCuenta("motor.16").saldoInvertido);
    	
    	
    	
    	
    	//System.out.println(billetera);
	}

}