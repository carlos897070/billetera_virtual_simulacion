package billeteraVirtual;

public class CuentaCorporativa extends Cuenta {
	
	private String cuitEmpresa;

	public CuentaCorporativa(String alias, String dniTitular, String cuitEmpresa) {
		super(alias, dniTitular);
		this.cuitEmpresa = cuitEmpresa;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Tipo: " + "[CuentaCorporativa]: " + "[" + alias + "]" + "[" + cvu + "]";
	}
	
	

}
