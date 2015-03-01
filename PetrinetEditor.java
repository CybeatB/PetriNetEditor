import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class PetrinetEditor extends JFrame implements ActionListener {
	public static void main(String[] args) {
		PetrinetEditor pe = new PetrinetEditor();
	}

	// menus
	private JMenu filemenu;
	private JMenuItem loaditem;
	private JMenuItem storeitem;
	private JMenuItem quititem;
	private JMenu editmenu;
	private JMenuItem placeitem;
	private JMenuItem transitem;
	private JMenuItem arcitem;
	private JMenuItem iniadditem;
	private JMenuItem iniremitem;
	private JMenuItem delitem;
	// buttons
	private JButton resetbut;
	private JButton firebut;
	// other
	private JTextField searchfield;
	private JTextArea statusarea;
	private Petrinet petrinet;
	private EditorPanel editor;
	private File currentfile;

	public PetrinetEditor() {
		// set up frame
		this.setTitle("Petrinet Editor");
		this.setSize(800,600);
		this.setPreferredSize(new Dimension(800,600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// status area
		this.statusarea = new JTextArea();
		this.statusarea.setRows(5);
		this.statusarea.setLineWrap(true);
		this.statusarea.setWrapStyleWord(true);
		this.statusarea.setEditable(false);
		((DefaultCaret)this.statusarea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.statusarea.append("=== BEGIN EVENT LOG ===");
		this.petrinet = new PetrinetImpl();
		this.currentfile = new File(System.getProperty("user.dir"));

		// buttons
		this.resetbut = new JButton("Reset");
		this.resetbut.addActionListener(this);
		this.firebut = new JButton("Fire");
		this.firebut.addActionListener(this);
		this.searchfield = new JTextField("Search...", 12);

		// editor
		this.editor = new EditorPanel(this.petrinet);
		this.editor.setBackground(Color.WHITE);
		this.petrinet.addListener(this.editor);

		// menus
		JMenuBar menubar = new JMenuBar();
		this.filemenu = new JMenu("File");
		this.loaditem = new JMenuItem("Load Petri-net...");
		this.loaditem.addActionListener(this);
		this.storeitem = new JMenuItem("Store Petri-net...");
		this.storeitem.addActionListener(this);
		this.quititem = new JMenuItem("Quit");
		this.quititem.addActionListener(this);
		this.editmenu = new JMenu("Edit");
		this.placeitem = new JMenuItem("Add place");
		this.placeitem.addActionListener(this);
		this.transitem = new JMenuItem("Add transition");
		this.transitem.addActionListener(this);
		this.arcitem = new JMenuItem("Add arc");
		this.arcitem.addActionListener(this);
		this.iniadditem = new JMenuItem("Add initial arc");
		this.iniadditem.addActionListener(this);
		this.iniremitem = new JMenuItem("Remove initial arc");
		this.iniremitem.addActionListener(this);
		this.delitem = new JMenuItem("Delete");
		this.delitem.addActionListener(this);

		// menu bar
		this.setJMenuBar(menubar);
		menubar.add(filemenu);
		this.filemenu.add(loaditem);
		this.filemenu.add(storeitem);
		this.filemenu.add(quititem);
		menubar.add(editmenu);
		this.editmenu.add(placeitem);
		this.editmenu.add(transitem);
		this.editmenu.add(arcitem);
		this.editmenu.add(iniadditem);
		this.editmenu.add(iniremitem);
		this.editmenu.add(delitem);

		// control panel
		JPanel ctrlPanel = new JPanel();
		ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS));
		ctrlPanel.add(this.firebut);
		ctrlPanel.add(this.resetbut);
		ctrlPanel.add(this.searchfield);

		// status panel
		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(this.statusarea);
		statPanel.add(scrollPane, BorderLayout.CENTER);

		// gather the status and control panels together
		JPanel notnetPanel = new JPanel();
		notnetPanel.setLayout(new BorderLayout());
		notnetPanel.add(ctrlPanel, BorderLayout.WEST);
		notnetPanel.add(statPanel, BorderLayout.CENTER);

		// main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(editor, BorderLayout.CENTER);
		mainPanel.add(notnetPanel, BorderLayout.SOUTH);
		this.setContentPane(mainPanel);

		this.setVisible(true);
	}

	public void reset() {
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String act = e.getActionCommand();
		statusLine(act);
		if (act.equals("Load Petri-net...")) {
			JFileChooser fc = new JFileChooser(this.currentfile);
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.currentfile = fc.getSelectedFile();
				try {
					BufferedReader readin = new BufferedReader(new FileReader(this.currentfile));
					Petrinet tempnet = new PetrinetImpl();
					tempnet.addListener(this.editor);
					PetrinetReaderWriter pnetrw = new PetrinetReaderWriter();
					pnetrw.read(readin, tempnet);
					statusLine("Opened "+this.currentfile.getName());
					this.petrinet = tempnet;
					this.editor.setPetrinet(this.petrinet);
				} catch (FileNotFoundException x) {
					statusLine(x.toString());
				} catch (IOException x) {
					statusLine(x.toString());
				} catch (PetrinetFormatException x) {
					statusLine(x.toString());
					Pattern pat = Pattern.compile("^([0-9]+):0:PetrinetFormatException: (.*)$");
					Matcher mat = pat.matcher(x.toString());
					JOptionPane.showMessageDialog(this, mat.replaceAll("$2 on line $1"), "Error Loading File", JOptionPane.ERROR_MESSAGE);
				}
			}
			System.out.println(this.petrinet.toString());
			return;
		}
		if (act.equals("Store Petri-net...")) {
			JFileChooser fc = new JFileChooser(this.currentfile);
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					BufferedWriter writeout = new BufferedWriter(new FileWriter(file));
					PetrinetReaderWriter pnetrw = new PetrinetReaderWriter();
					pnetrw.write(writeout, this.petrinet);
					this.currentfile = file;
					statusLine("Saved "+this.currentfile.getName());
				} catch (IOException x) {
					statusLine(x.toString());
				}
			}
			return;
		}
		if (act.equals("Quit")) {
			this.setVisible(false);
			this.dispose();
			return;
		}
		if (act.equals("Add place")) {
			String name = (String)JOptionPane.showInputDialog(this,
				"Input the Name of the New Place:",
				"Add Place",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"Name"
			);
			if (name == null) { return; }
			try {
				Place newplace = this.petrinet.newPlace(name, this.editor.getWidth()/2, this.editor.getHeight()/2);
				statusLine("New Place '"+newplace.toString()+"' Added");
			} catch (IllegalArgumentException x) {
				statusLine(x.toString());
				JOptionPane.showMessageDialog(this, x.toString(), "Error Adding Place", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}
		if (act.equals("Add transition")) {
			String name = (String)JOptionPane.showInputDialog(this,
				"Input the Name of the New Transition",
				"Add Transition",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"Name"
			);
			if (name == null) { return; }
			try {
				Transition newtrans = this.petrinet.newTransition(name, this.editor.getWidth()/2, this.editor.getHeight()/2);
				statusLine("New Transition '"+newtrans.toString()+"' Added");
			} catch (IllegalArgumentException x) {
				statusLine(x.toString());
				JOptionPane.showMessageDialog(this, x.toString(), "Error Adding Transition", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}
		if (act.equals("Add arc")) {
			this.editor.addArc();
			return;
		}
		if (act.equals("Add initial arc")) {
			String weight = (String)JOptionPane.showInputDialog(this,
				"Input the Weight of the New Initial Arc:",
				"Add Initial Arc",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"Weight"
			);
			if (weight == null) { return; }
			int w = Integer.parseInt(weight);
			for (PlaceIcon pi : this.editor.selectedPlaces()) {
				this.petrinet.addInitialArc(pi.getPlace(), w);
				statusLine("INI("+w+") Added To "+ pi.getPlace().toString());
			}
			return;
		}
		if (act.equals("Remove initial arc")) {
			for (PlaceIcon pi : this.editor.selectedPlaces()) {
				this.petrinet.removeInitialArc(pi.getPlace());
				statusLine("INI Removed From "+pi.getPlace().toString());
			}
			return;
		}
		if (act.equals("Fire")) {
			Transition t = this.petrinet.findTransition(this.searchfield.getText());
			try {
				this.petrinet.simFire(t);
			} catch (IllegalArgumentException x) {
				statusLine(x.toString());
				JOptionPane.showMessageDialog(this, x.toString(), "Could Not Fire Transition", JOptionPane.ERROR_MESSAGE);
			} catch (NullPointerException x) {
				String err = "Transition '"+this.searchfield.getText()+"' Does Not Exist";
				statusLine(err);
				JOptionPane.showMessageDialog(this, err, "Transition Does Not Exist", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}
		if (act.equals("Reset")) {
			this.petrinet.simInitialise();
			return;
		}
		if (act.equals("Delete")) {
			Set<PlaceIcon> rmplc = this.editor.selectedPlaces();
			Set<TransitionIcon> rmtrn = this.editor.selectedTransitions();
			for (PlaceIcon pi : rmplc) {
				this.petrinet.removePlace(pi.getPlace());
				statusLine("Removed Place: "+pi.getPlace().toString());
			}
			for (TransitionIcon ti : rmtrn) {
				this.petrinet.removeTransition(ti.getTransition());
				statusLine("Removed Transition: "+ti.getTransition().toString());
			}
			return;
		}
	}

	private void statusLine(String s) {
		System.out.println(s);
		this.statusarea.append("\n"+s);
	}
}
