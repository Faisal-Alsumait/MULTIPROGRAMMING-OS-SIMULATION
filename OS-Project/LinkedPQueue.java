
public class LinkedPQueue<T> {
	private PQNode<T> head;
	private int size;
	private int maxsize;

	public int length() {

		return size;
	}

	public T find_at_i(int i) {
		T e = null;
		PQNode<T> temp = head;
		for (int j = 0; j < size; j++) {
			if (j == i) {
				e = temp.data;
				return e;
			}

			temp = temp.next;

		}
		return e;
	}

	public void update(T e) {

		head.data = e;

	}

	public void enqueue(int pr, process e) {
		PQNode tmp = new PQNode(e, pr);

		if ((size == 0) || (pr < head.pr)) {

			tmp.next = head;
			head = tmp;
		} else {
			PQNode p = head;
			PQNode q = null;
			while ((p != null) && (pr >= p.pr)) {
				q = p;
				p = p.next;
			}

			tmp.next = p;
			q.next = tmp;
		}
		size++;

	}

	public T serve() {

		if (size == 0)
			return null;
		T node = head.data;

		head = head.next;
		size--;
		return node;

	}

}
