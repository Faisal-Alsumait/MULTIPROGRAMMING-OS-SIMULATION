import java.io.*;
import java.util.*;

public class OS {
	public int clock = 1;
	public int finished;
	public int nbOfP;
	private double ARAM;
	private double addRam;
	LinkedPQueue<process> jobs_queue;
	LinkedPQueue<process> Ready_queue;
	LinkedQueue<process> waiting_queue;
	LinkedQueue<process> io_queue;
	LinkedQueue<process> finished_queue;
	LinkedQueue<process> pro;

	public OS() {
		addRam = (1024 - 320) * 0.15;
		ARAM = (1024 - 320) * 0.85;
		nbOfP = 0;
		pro = new LinkedQueue<process>();
		jobs_queue = new LinkedPQueue<process>();
		Ready_queue = new LinkedPQueue<process>();
		waiting_queue = new LinkedQueue<process>();
		io_queue = new LinkedQueue<process>();
		finished_queue = new LinkedQueue<process>();
	}

	public void short_term_scheduler() {
		process pre = null;
		while (finished != finished_queue.length()) {
//			System.out.println("Clock ->" + clock);
			if (deadlock())
				kill_process();
			if (clock % 200 == 0)
				long_term_scheduler();
			process p = findminB();

			CPU(p, pre);

			ioburst();
			// if there is enough memory then move the process from waiting queue to ready
			// queue
			enoughMem();

			clock++;
			pre = p;
			nbOfP = Ready_queue.length() + waiting_queue.length() + io_queue.length();
		}

	}

	public void long_term_scheduler() {
		int n = jobs_queue.length();
		for (int i = 0; i < n; i++) {
			pro.enqueue(jobs_queue.serve());
		}
		n = pro.length();

		for (int i = 0; i < n; i++) {
			process p = pro.serve();
			int memory_r = p.memory_req.find_at_i(0);
			if (ARAM - memory_r >= 0) {
				p.firstreq = memory_r;
				p.loadReadyTime = clock;
				Ready_queue.enqueue(p.AT, p);
				ARAM = ARAM - memory_r;
			} else
				pro.enqueue(p);

		}

	}

	public void CPU(process p, process pre) {
		if (p == null)
			return;

		else if (p.CPU_burst.find_at_i(0).intValue() == -1) {
			ARAM += p.firstreq;
			addRam += p.addmem;
			p.state = "terminated";
			finished_queue.enqueue(p);
		}

		else if (p.CPU_burst.find_at_i(0).intValue() != 0) {
			p.CPU_burst.update(new Integer(p.CPU_burst.find_at_i(0).intValue() - 1));
			p.finishedcpu++;

			if (pre != null && p.id != pre.id && pre.state != "WFIO") {
				preempted(pre.id);
			}
			Ready_queue.enqueue(p.AT, p);
		}

		else if (p.CPU_burst.find_at_i(0).intValue() == 0) {

			p.state = "WFIO";
			p.CPU_burst.serve();
			p.memory_req.serve();
			p.nbOfTimeInCpu++;
			if (p.CPU_burst.find_at_i(0).intValue() == -1) {
				ARAM += p.firstreq;
				addRam += p.addmem;
				p.state = "terminated";
				finished_queue.enqueue(p);
				return;
			}
			io_queue.enqueue(p);
		}
	}

	public void preempted(int id) {
		int n = Ready_queue.length();
		if (n == 0) {
			return;
		}
		process[] ps = new process[n];

		for (int i = 0; i < n; i++) {
			ps[i] = Ready_queue.serve();
		}
		n = ps.length;
		int i = 0;
		while (i < n) {
			process p = ps[i];
			if (id == p.id) {
				p.preempted++;
				p.nbOfTimeInCpu++;
			}
			Ready_queue.enqueue(p.AT, p);

			i++;
		}

	}

	public void ioburst() {
		int n = io_queue.length();
		process[] ps = new process[n];

		for (int i = 0; i < n; i++) {
			ps[i] = io_queue.serve();

		}

		for (int i = 0; i < n; i++) {
			process p = ps[i];
			p.io_burst.update(p.io_burst.find_at_i(0).intValue() - 1);
			if (p.io_burst.find_at_i(0).intValue() == 0) {
				p.nbOfTimeIO++;
				p.finishedio += p.io_burst.find_at_i(0).intValue();
				p.io_burst.serve();
				p.state = "WFM";
				waiting_queue.enqueue(p);
			} else
				io_queue.enqueue(p);
		}

	}

	public process findminB() {
		int n = Ready_queue.length();
		if (n == 0) {
			return null;
		}
		process[] ps = new process[n];

		for (int i = 0; i < n; i++) {
			ps[i] = Ready_queue.serve();
		}

		if (ps[0].AT > clock) {
			for (int i = 0; i < n; i++) {
				Ready_queue.enqueue(ps[i].AT, ps[i]);
			}
			return null;
		}
		process p = ps[0];
		for (int i = 0; i < n; i++)
			if (ps[i].AT <= clock)
				if (ps[i].CPU_burst.find_at_i(0) < p.CPU_burst.find_at_i(0))
					p = ps[i];

		for (int i = 0; i < n; i++)
			if (p.id != ps[i].id)
				Ready_queue.enqueue(ps[i].AT, ps[i]);

//		System.out.println(
//				"Min cpu burst is ->" + +p.id + " " + p.CPU_burst.find_at_i(0).intValue() + " Clock ->" + clock);

		return p;
	}

	public void enoughMem() {
		int n = waiting_queue.length();
		if (n == 0)
			return;
		process[] ps = new process[n];
		for (int i = 0; i < n; i++)
			ps[i] = waiting_queue.serve();

		for (int i = 0; i < n; i++) {
			process p = ps[i];
			int mem = p.memory_req.find_at_i(0).intValue();
			if (addRam - mem >= 0) {
				p.addmem += mem;
				addRam -= mem;
				Ready_queue.enqueue(p.AT, p);
			} else {
				p.nbOfTimeWaitMem++;
				waiting_queue.enqueue(p);
			}

		}

	}

	public boolean deadlock() {

		if (waiting_queue.length() == 0)
			return false;

		return nbOfP == waiting_queue.length();

	}

	public void kill_process() {
		int max = 0;

		int id = -1;
		int n = waiting_queue.length();

		for (int i = 0; i < n; i++) {

			if (waiting_queue.find_at_i(i).memory_req.find_at_i(0).intValue() > max) {
				max = waiting_queue.find_at_i(i).memory_req.find_at_i(0).intValue();
				id = waiting_queue.find_at_i(i).id;

			}

		}

		for (int i = 0; i < n; i++) {

			process p = waiting_queue.serve();

			if (p.id != id) {
				waiting_queue.enqueue(p);
			}

			else {
				p.state = new String("killed");
				addRam += p.addmem;
				ARAM += p.firstreq;
				finished_queue.enqueue(p);

			}

		}
	}

	public void write(String filename) {
		int n = finished_queue.length();
		try {
			File myObj = new File("C:\\Users\\Ahmad\\Desktop\\" + filename + ".txt");
			if (myObj.createNewFile()) {
				double total_burst = 0;
				int kill = 0, ter = 0;
				FileWriter myWriter = new FileWriter("C:\\Users\\Ahmad\\Desktop\\" + filename + ".txt");
				for (int i = 0; i < n; i++) {
					process p = finished_queue.serve();

					int t = 0;

					if (p.state.equalsIgnoreCase("killed")) {

						t = p.timeTerOrKilled;
						kill++;
					} else {

						t = p.timeTerOrKilled;
						ter++;
					}
					total_burst += p.finishedcpu;
					myWriter.write("Process ID -> " + p.id + " \r\n" + "Program name -> " + p.id + " \r\n"
							+ "When it was loaded into the ready queue. -> " + p.loadReadyTime + " \r\n"
							+ "Number of times it was in the CPU. -> " + p.nbOfTimeInCpu + " \r\n"
							+ "Total time spent in the CPU -> " + p.finishedcpu + " \r\n"
							+ "Number of times it performed an IO. -> " + p.nbOfTimeIO + " \r\n"
							+ "Total time spent in performing IO -> " + p.finishedio + "  \r\n"
							+ "Number of times it was waiting for memory. -> " + p.nbOfTimeWaitMem + " \r\n"
							+ "Number of times its preempted -> " + p.preempted + " \r\n"
							+ "Time it terminated or was killed -> " + t + " \r\n"
							+ "Its final state: Killed or Terminated -> " + p.state);

				}
				double q = (total_burst / clock) * 100;
				myWriter.write("CPU Utilization -> " + q + "% \r\n");
				System.out.println("Counter -> " + clock + " Total -> " + total_burst);
				System.out.println("CPU Utilization -> " + q);
				System.out.println("k " + kill + " ter" + ter);
				System.out.println("F lenght -> " + finished_queue.length() + "Ready lenght -> " + Ready_queue.length()
						+ " waiting lenght -> " + waiting_queue.length() + " IO lenght -> " + io_queue.length() + " = "
						+ (Ready_queue.length() + waiting_queue.length() + io_queue.length()));
				System.out.println(addRam + " " + ARAM);
				myWriter.close();

			} else {
				myObj.delete();
				write(filename);
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void generate(String filename, int nbj) {
		try {
			Random r = new Random();
			File file = new File("C:\\Users\\Ahmad\\Desktop\\" + filename + ".txt");
			if (file.createNewFile()) {
				FileWriter fw = new FileWriter(file);
				PrintWriter pw = new PrintWriter(fw);

				for (int i = 0; i < nbj; i++) {
					int nbprocess = 3;
					int ART = 1 + r.nextInt(79);
					pw.print(String.valueOf(ART) + " ");
					while (nbprocess > 0) {
						int cpu_b = (10 + r.nextInt(91));
						int memory = 5 + r.nextInt(196);
						int io = 20 + r.nextInt(41);
						pw.print(String.valueOf(cpu_b) + " ");
						pw.print(String.valueOf(memory) + " ");
						if (nbprocess > 1)
							pw.print(String.valueOf(io) + " ");
						nbprocess--;
					}
					pw.println("-1");

				}

				fw.close();
			} else {
				file.delete();
				generate(filename, nbj);
			}

		} catch (Exception e) {

		}

	}

	public void loadjobs() {

		try {

			FileReader fr = new FileReader("C:\\Users\\Ahmad\\Desktop\\" + "projectcsc" + ".txt");
			BufferedReader r = new BufferedReader(fr);

			String s = r.readLine();

			int id = 0;
			while (s != null) {
				process p = new process();

				p.state = "new";

				p.id = id++;
				String[] arr = s.split(" ");
				p.AT = Integer.valueOf(arr[0]);

				int flag = 1;
				int mem = 0;
				boolean flag2 = true;
				for (int i = 1; i < arr.length - 1; i++) {

					int k = flag % 3 == 0 ? 3 : flag % 2 == 0 ? 2 : 1;

					switch (k) {

					case 1:
						p.CPU_burst.enqueue(Integer.valueOf(arr[i]));
						break;
					case 2:
						mem = Integer.valueOf(arr[i]);
						if (flag2)
							p.firstreq = mem;
						flag2 = false;
						p.memory_req.enqueue(mem);
						break;
					case 3:
						p.io_burst.enqueue(Integer.valueOf(arr[i]));
						flag = 0;
						break;

					default:
						break;

					}
					flag++;

				}
				p.CPU_burst.enqueue(Integer.valueOf(arr[arr.length - 1]));

				jobs_queue.enqueue(p.AT, p);
				s = r.readLine();

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		finished = jobs_queue.length();

	}

}
