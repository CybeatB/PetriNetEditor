import java.io.*;
import java.lang.Math;
import java.util.regex.*;

public class PetrinetReaderWriter implements PetrinetIo {
	public PetrinetReaderWriter() {
		//
	}

	public void read(Reader r, Petrinet p) throws IOException, PetrinetFormatException {
		PetrinetReadMachine m = new PetrinetReadMachine(p);
		int buf = r.read();
		while (buf != -1) {
			m.sendChar((char)buf);
			buf = r.read();
		}
		m.sendChar('\n');
	}

	public void write(Writer w, Petrinet p) throws IOException {
			String in = p.toString();
			// places
			Pattern pat = Pattern.compile("PLACE");
			Matcher mat = pat.matcher(in);
			in = mat.replaceAll("plc");
			// transitions
			pat = Pattern.compile("TRANSITION");
			mat = pat.matcher(in);
			in = mat.replaceAll("trn");
			// arcpt
			pat = Pattern.compile("ARCPT");
			mat = pat.matcher(in);
			in = mat.replaceAll("apt");
			// arctp
			pat = Pattern.compile("ARCTP");
			mat = pat.matcher(in);
			in = mat.replaceAll("atp");
			// initial
			pat = Pattern.compile("INITIAL");
			mat = pat.matcher(in);
			in = mat.replaceAll("ini");
			// juggle arc fields
			pat = Pattern.compile("(\\([0-9]+\\))(.+)\n");
			mat = pat.matcher(in);
			in = mat.replaceAll(" $2$1\n");
			// commas & parens to spaces
			pat = Pattern.compile("\\(|,|\\)");
			mat = pat.matcher(in);
			in = mat.replaceAll(" ");
			// tidy line endings
			pat = Pattern.compile(" +\n");
			mat = pat.matcher(in);
			in = mat.replaceAll("\n");
			// write to file
			w.write(in);
	}


/* INDENTATION IS INTENTIONAL */

private class PetrinetReadMachine {
	/* 'Generic' States */
	private static final int STARTLN = 0;
	private static final int IGNORELN = 1;
	/* 'Place' States */
	private static final int PLCNAME = 2;
	private static final int PLCXPOS = 3;
	private static final int PLCYPOS = 4;
	/* 'Transition' States */
	private static final int TRNNAME = 5;
	private static final int TRNXPOS = 6;
	private static final int TRNYPOS = 7;
	/* 'Place-Transition Arc' States */
	private static final int APTPLC = 8;
	private static final int APTTRN = 9;
	private static final int APTWGT = 10;
	/* 'Transition-Place Arc' States */
	private static final int ATPTRN = 11;
	private static final int ATPPLC = 12;
	private static final int ATPWGT = 13;
	/* 'Initial Arc' States */
	private static final int INIPLC = 14;
	private static final int INIWGT = 15;

	/* Class Variables */
	private int linenr;
	private int state;
	private Petrinet pnet;
	private String line;
	private String name;
	private String place;
	private String trans;
	private String xpos;
	private String ypos;
	private String weight;

	public PetrinetReadMachine(Petrinet p) {
		this.linenr = 0;
		this.state = 0;
		this.pnet = p;
		this.line = new String();
		this.name = new String();
		this.place = new String();
		this.trans = new String();
		this.xpos = new String();
		this.ypos = new String();
		this.weight = new String();
		next(STARTLN);
	}

	public void sendChar(char c) throws PetrinetFormatException {
		switch (this.state) {
			case STARTLN:
				if (c == ' ') {
					if (this.line.length() == 0) {
						// arbitrary whitespace?
						return;
					}
					// line type in buffer, change state
					if (this.line.equals("plc")) {
						// line is a place
						next(PLCNAME);
						return;
					}
					if (this.line.equals("trn")) {
						// line is a transition
						next(TRNNAME);
						return;
					}
					if (this.line.equals("apt")) {
						// line is a place-transition arc
						next(APTPLC);
						return;
					}
					if (this.line.equals("atp")) {
						// line is a transition-place arc
						next(ATPTRN);
						return;
					}
					if (this.line.equals("ini")) {
						// line is an initial arc
						next(INIPLC);
						return;
					}
					// invalid type, fall through to error
				}
				if (this.line.length() == 0 && (c == '\n' || c == '\r')) {
					// ignore newline characters
					return;
				}
				if (c >= 'a' && c <= 'z') {
					// append character to buffer
					this.line += c;
					return;
				}
				if (this.line.length() == 0 && c == '#') {
					// line is a comment, ignore it
					next(IGNORELN);
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Object Type");
//				return;
			case IGNORELN:
				if (c == '\n' || c == '\r') {
					// line has ended
					next(STARTLN);
					return;
				}
				// ignore the given character
				return;
			case PLCNAME:
				if (c == ' ') {
					if (this.name.length() == 0) {
						// arbitrary whitespace
						return;
					}
					// name is in buffer, change state
					next(PLCXPOS);
					return;
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append character to buffer
					this.name += c;
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Place Name");
//				return;
			case PLCXPOS:
				if (c == ' ') {
					if (this.xpos.length() == 0) {
						// arbitrary whitespace
						return;
					}
					// value is in buffer, change state
					next(PLCYPOS);
					return;
				}
				if (c >= '0' && c <= '9') {
					// append character to buffer
					this.xpos += c;
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Place X-Coordinate "+this.line+" "+c);
//				return;
			case PLCYPOS:
				if (this.ypos.length() == 0 && c == ' ') {
					// arbitrary whitespace
					return;
				}
				if (this.ypos.length() > 0 && (c == '\r' || c == '\n')) {
					// line has ended
					int x = 0;
					for (int i = 0; i < this.xpos.length(); i++) {
						x += (this.xpos.charAt(i) - '0') * Math.pow(10, (this.xpos.length() - (i+1)));
					}
					int y = 0;
					for (int i = 0; i < this.ypos.length(); i++) {
						y += (this.ypos.charAt(i) - '0') * Math.pow(10, (this.ypos.length() - (i+1)));
					}
					this.pnet.newPlace(this.name, x, y);
					next(STARTLN);
					return;
				}
				if (c >= '0' && c <= '9') {
					// append character
					this.ypos += c;
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Place Y-Coordinate");
//				return;
			case TRNNAME:
				if (c == ' ') {
					if (this.name.length() == 0) {
						// arbitrary whitespace
						return;
					}
					// name is in buffer, change state
					next(TRNXPOS);
					return;
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append character
					this.name += c;
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Transition Name");
//				return;
			case TRNXPOS:
				if (c == ' ') {
					if (this.xpos.length() == 0) {
						// arbitrary whitespace
						return;
					}
					// position is in buffer, change state
					next(TRNYPOS);
					return;
				}
				if (c >= '0' && c <= '9') {
					// append character
					this.xpos += c;
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Transition X-Coordinate");
//				return;
			case TRNYPOS:
				if (this.ypos.length() == 0 && c == ' ') {
					// arbitrary whitespace
					return;
				}
				if (this.ypos.length() > 0 && (c == '\r' || c == '\n')) {
					// line end
					int x = 0;
					for (int i = 0; i < xpos.length(); i++) {
						x += (this.xpos.charAt(i) - '0') * Math.pow(10, (this.xpos.length() - (i+1)));
					}
					int y = 0;
					for (int i = 0; i < ypos.length(); i++) {
						y += (this.ypos.charAt(i) - '0') * Math.pow(10, (this.xpos.length() - (i+1)));
					}
					this.pnet.newTransition(this.name, x, y);
					next(STARTLN);
					return;
				}
				if (c >= '0' && c <= '9') {
					// append character
					this.ypos += c;
					return;
				}
				// handle bad characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Transition Y-Coordinate");
//				return;
			case APTPLC:
				if (c == ' ') {
					if (this.place.length() == 0) {
						// arbitrary whitespace
						return;
					}
					// name in buffer, check for the place in pnet
					if (this.pnet.findPlace(this.place) != null) {
						next(APTTRN);
						return;
					}
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append character
					this.place += c;
					return;
				}
				// handle invalid characters
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Arc Place");
//				return;
			case APTTRN:
				if (c == ' ') {
					if (this.trans.length() == 0) {
						// arbitrary whitespace
						return;
					}
					if (this.pnet.findTransition(this.trans) != null) {
						// in buffer, change state
						next(APTWGT);
						return;
					}
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append to buffer
					this.trans += c;
					return;
				}
				// handle invlid input
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Arc Transition");
//				return;
			case APTWGT:
				if (this.weight.length() == 0 && c == ' ') {
					// arbitrary whitespace
					return;
				}
				if (this.weight.length() > 0 && (c == '\r' || c == '\n')) {
					// end of line
					int w = 0;
					for (int i = 0; i < this.weight.length(); i++) {
						w += (this.weight.charAt(i) - '0') * Math.pow(10, (this.weight.length() - (i+1)));
					}
					this.pnet.addArc(this.pnet.findPlace(this.place), this.pnet.findTransition(this.trans), w);
					next(STARTLN);
					return;
				}
				if (c >= '0' && c <= '9') {
					// append to buffer
					this.weight += c;
					return;
				}
				// handle invalid input
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Arc Weight");
//				return;
			case ATPTRN:
				if (c == ' ') {
					if (this.trans.length() == 0) {
						// arbitrary whitespace
						return;
					}
					if (this.pnet.findTransition(this.trans) != null) {
						// change state
						next(ATPPLC);
						return;
					}
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append to buffer
					this.trans += c;
					return;
				}
				// handle invalid input
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Arc Transition");
//				return;
			case ATPPLC:
				if (c == ' ') {
					if (this.place.length() == 0) {
						// arbitrary whitespace
						return;
					}
					if (this.pnet.findPlace(this.place) != null) {
						// change state
						next(ATPWGT);
						return;
					}
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append character
					this.place += c;
					return;
				}
				// handle invalid
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Arc Place");
//				return;
			case ATPWGT:
				if (this.weight.length() == 0 && c == ' ') {
					// arbitrary whitespace
					return;
				}
				if (this.weight.length() > 0 && (c == '\r' || c == '\n')) {
					// end of line
					int w = 0;
					for (int i = 0; i < this.weight.length(); i++) {
						w += (this.weight.charAt(i) - '0') * Math.pow(10, (this.weight.length() - (i+1)));
					}
					this.pnet.addArc(this.pnet.findTransition(this.trans), this.pnet.findPlace(this.place), w);
					next(STARTLN);
					return;
				}
				if (c >= '0' && c <= '9') {
					// apend character
					this.weight += c;
					return;
				}
				// append character
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Arc Weight");
//				return;
			case INIPLC:
				if (c == ' ') {
					if (this.place.length() == 0) {
						// arbitrary whitespace
						return;
					}
					if (this.pnet.findPlace(this.place) != null) {
						// change state
						next(INIWGT);
						return;
					}
				}
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
					// append character
					this.place += c;
					return;
				}
				// bad character
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Initial-Arc Place");
//				return;
			case INIWGT:
				if (this.weight.length() == 0 && c == ' ') {
					// arbitrary whitespace
					return;
				}
				if (this.weight.length() > 0 && (c == '\r' || c == '\n')) {
					// end of line
					int w = 0;
					for (int i = 0; i < this.weight.length(); i++) {
						w += (this.weight.charAt(i) - '0') * Math.pow(10, (this.weight.length() - (i+1)));
					}
					this.pnet.addInitialArc(this.pnet.findPlace(this.place), w);
					next(STARTLN);
					return;
				}
				if (c >= '0' && c <= '9') {
					// append character
					this.weight += c;
					return;
				}
				// bad character
				next(STARTLN);
				throw new PetrinetFormatException(this.linenr, "Invalid Initial-Arc Weight");
//				return;
		}
	}

	private void next(int newstate) {
		this.state = newstate;
		switch (this.state) {
			case STARTLN:
				// next line
				this.linenr += 1;
				// reset the globals
				this.line = "";
				this.name = "";
				this.place = "";
				this.trans = "";
				this.xpos = "";
				this.ypos = "";
				this.weight = "";
				return;
			case IGNORELN:
				return;
			case PLCNAME:
				return;
			case PLCXPOS:
				return;
			case PLCYPOS:
				return;
			case TRNNAME:
				return;
			case TRNXPOS:
				return;
			case TRNYPOS:
				return;
			case APTPLC:
				return;
			case APTTRN:
				return;
			case APTWGT:
				return;
			case ATPPLC:
				return;
			case ATPTRN:
				return;
			case ATPWGT:
				return;
			case INIPLC:
				return;
			case INIWGT:
				return;
		}
	}
}
} // leave this here, it's supposed to be here
