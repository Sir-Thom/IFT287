package SeauS;

/**
 * L'exception BiblioException est levée lorsqu'une transaction est inadéquate.
 * Par exemple -- livre inexistant
 */
public final class SeauSException extends Exception
{
	private static final long serialVersionUID = 1L;

	public SeauSException(String message)
	{
		super(message);
	}
}