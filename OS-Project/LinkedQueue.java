
public class LinkedQueue<T>{

	private Node<T> head;
	private Node<T> tail;
	private int size;
	
	public LinkedQueue() {
		head = tail = null;
		size = 0;
		
	}
	
	public void update(T e) {
		
		head.data=e;
		
	}
	public T find_at_i(int i) {
		T e=null;
	    Node<T> temp=head;
     for(int j=0;j<size;j++) {
    	 if(j==i) {e=temp.data;return e;}
    	 temp=temp.next;
     }
     return e;
	}
	
	public T serve() {
		
		
		T e = head.data;
		head = head.next;size--;
		if(size==0)tail=null;
		return e;
		
		
	}


	public void enqueue(T e) {
	
		Node<T> temp = new Node<T>(e);
		if(tail==null)head =tail=temp;
		
		else {
			tail.next=temp;
			tail=temp;
			
			
		}
		size++;
	}

	
	public int length() {
		
		return size;
	}

	
	public boolean full() {
		
		return false;
	}

}
