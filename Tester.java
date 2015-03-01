import java.io.*;

public class Tester {
	public static void main(String[] args) {
		System.out.println("Reader Tests\n");
		rtest("One Valid Place", "plc P0 1 2");
		rtest("One Valid Transition", "trn t0 1 2");
		rtest("One Valid Place & One Valid Transition", "plc P0 1 2\n trn T0 2 3");
		rtest("Empty Lines", "\n\n \n");
		rtest("Comments", "# comment\n#another one\n#");
		rtest("Arc By Itself", "apt P0 T0 1");
		rtest("Arc By Itself", "atp T0 P0 1");
		rtest("Place, Transition, Arc", "plc P0 1 2\n trn T0 2 3\n apt P0 T0 1");
		rtest("Transition, Place, Arc", "plc P0 1 2\n trn T0 2 3\n atp T0 P0 1");
		rtest("Initial Arc By Itself", "ini P0 1");
		rtest("Place with Initial Arc", "plc P0 1 2\n ini P0 1");
		rtest("Invalid Line Type", "die");
		rtest("Incomplete Line", "plc P0");

		System.out.println("\n===================================================\n");
		System.out.println("Writer Tests\n");

		PetrinetImpl pnet = new PetrinetImpl();
		Place p0 = pnet.newPlace("P0", 1, 2);
		wtest("One Place", pnet);
		Transition t0 = pnet.newTransition("T0", 2, 3);
		wtest("Place & Transition", pnet);
		pnet.addArc(p0, t0, 1);
		wtest("One Arc", pnet);
		pnet.addArc(t0, p0, 2);
		wtest("Two Arcs", pnet);
		pnet.addInitialArc(p0, 3);
		wtest("Initial Arc", pnet);
	}

	private static void rtest(String msg, String input) {
		PetrinetImpl pnet = new PetrinetImpl();
		PetrinetReaderWriter rw = new PetrinetReaderWriter();
		StringReader sr = new StringReader(input);
		try {
			System.out.println(msg);
			System.out.println(input);
			rw.read(sr, pnet);
		} catch (PetrinetFormatException e) {
			System.err.println(e.toString());
		} catch (IOException e) {
			System.err.println(e.toString());
		}
		System.out.println(pnet.toString());
		System.out.println("-------------------------------------");
	}

	private static void wtest(String msg, PetrinetImpl pnet) {
		PetrinetReaderWriter rw = new PetrinetReaderWriter();
		StringWriter sw = new StringWriter();
		try {
			rw.write(sw, pnet);
		} catch (IOException e) {
			System.err.println(e.toString());
		}
		System.out.println(msg);
		System.out.println(pnet.toString());
		System.out.println(sw.toString());
		System.out.println("-------------------------------------");
	}
}
