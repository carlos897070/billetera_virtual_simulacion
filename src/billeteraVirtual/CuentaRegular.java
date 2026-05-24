package billeteraVirtual;

public class CuentaRegular extends Cuenta {
	
	private static final double SALDO_MAXIMO = 5000000;
	
	public CuentaRegular(String alias, String dniTitular) {
		super(alias, dniTitular);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Tipo: " + "[CuentaRegular]: " + "[" + alias + "]" + "[" + cvu + "]";
	}
	
	@Override
	public void depositar(double dinero)
	{
		if(dinero <= 0) throw new IllegalArgumentException("Monto inválido");
		
		if(saldo + dinero > SALDO_MAXIMO) throw new IllegalStateException("El monto ingresado supera el limite para esta cuenta");
		
		saldo += dinero;
	}

	
	

	
	
	

}
