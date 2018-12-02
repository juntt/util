package common.util.tcc.exception;

/**
 * TCC异常
 * 
 * @author jieli
 *
 */
public class TccException extends RuntimeException {
	private static final long serialVersionUID = -2640394334683368376L;

	public TccException(String message) {
		super(message);
	}

	public TccException(String message, Throwable cause) {
		super(message, cause);
	}

	public TccException(Throwable cause) {
		super(cause);
	}
}
