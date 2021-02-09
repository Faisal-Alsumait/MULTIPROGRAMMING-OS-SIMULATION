import java.io.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class main {

	public static void main(String[] args) {
		OS a = new OS();
		a.generate("projectcsc", 1000);
		a.loadjobs();
		a.long_term_scheduler();

		a.short_term_scheduler();

		a.write("result_Abosmt");

//		int killed = 0;
//		int ter = 0;
//
//		for (int i = 0; i < a.finished_queue.length(); i++) {
//			if (a.finished_queue.find_at_i(i).state.equals("terminated"))
//				ter++;
//
//			else if (a.finished_queue.find_at_i(i).state.equals("killed"))
//				killed++;
//
//		}
//
//		System.out.println(a.clock);
//		System.out.println(ter);
//		System.out.println(killed);

	}

}
