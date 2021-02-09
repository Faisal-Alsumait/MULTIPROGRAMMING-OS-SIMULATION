
public class PQNode<T> {
	public T data;
	public int pr;
	public PQNode<T> next;

	public PQNode () {
		data = null;
		
		next = null;
	}

	public PQNode (T val,int p) {
		data = val;
		pr=p;
		next = null;
	}
}
