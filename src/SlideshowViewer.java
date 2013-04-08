import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;


@SuppressWarnings("serial")
public class SlideshowViewer extends JFrame 
 implements ActionListener, ListSelectionListener, WindowListener {

	// spara bilderna i en lista av File-objekt
	protected LinkedList<File> images = new LinkedList<File>();
	
	// GUI-komponenter
	private JLabel img = new JLabel(); // för bildvisning
	private JList<String> list = new JList<String>(); // filnamnslista
	private JButton up = new JButton("Up");
	private JButton down = new JButton("Down");
	private JButton add = new JButton("Add");
	private JButton remove = new JButton("Remove");
	
	
	public SlideshowViewer() {
		setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
		
		// vänster panel
		Box box = Box.createVerticalBox();
		
		// lägg till listan, möjliggör scrollning 
		box.add(new JScrollPane(list));

		// lägg till knapp-panel med fyra knappar (up, down, add remove) under listan
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2,2));
		box.add(p);
		p.add(up);
		p.add(down);
		p.add(add);
		p.add(remove);
		box.add(p);
		add(box);

		// lägg till bildkomponenten
		add(new JScrollPane(img));
		
		// pynja maxstorlek för bättre utseende
		p.setMaximumSize(new Dimension(10000,100));
		box.setMaximumSize(new Dimension(300,100000));
		setPreferredSize(new Dimension(1024,768));
		
		// lyssna på varje knapps aktion
		up.addActionListener(this);
		down.addActionListener(this);
		add.addActionListener(this);
		remove.addActionListener(this);
		
		// lyssna på list-selection-förändring
		list.addListSelectionListener(this);
		
		addWindowListener(this);
				
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	

	// öppna JFileChooser-dialog, och lägg till vald fil om användaren tryckt OK (dvs. APPROVE_OPTION returneras)
	protected void addFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Image files","jpg","png","gif"));
		int r = chooser.showOpenDialog(this);
		if (r==JFileChooser.APPROVE_OPTION){
			File file = chooser.getSelectedFile();
			images.addLast(file);
			updateList();
			list.setSelectedIndex(images.size()-1);
		}
	}
	
	
	// stryk valt element i images, uppdatera list
	protected void removeSelected() {
		int idx = list.getSelectedIndex();
		if (idx!=-1) {
			images.remove(idx);
			updateList();
			if (idx<images.size())
				list.setSelectedIndex(idx);
		}
	}
	
	
	// flytta den valda filen ett steg uppåt i images (om möjligt); uppdatera list
	protected void moveUp() {
		int idx = list.getSelectedIndex();
		if (idx>0) {
			File file = images.get(idx);
			images.remove(idx);
			images.add(idx-1,file);
			updateList();
			list.setSelectedIndex(idx-1);
		}
	}
	
	
	// flytta den valda filen ett steg uppåt i images (om möjligt); uppdatera list
	protected void moveDown() {
		int idx = list.getSelectedIndex();
		if (idx!=-1 && idx<images.size()-1) {
			File file = images.get(idx);
			images.remove(idx);
			images.add(idx+1,file);
			updateList();
			list.setSelectedIndex(idx+1);
		}
	}
	
	
	// uppdatera JList-komponenten list med innehållet i images
	protected void updateList() {
		DefaultListModel<String> m = new DefaultListModel<String>();
		for (File file: images) {
			// visa enbart filnamnet, dvs. utan sökväg (path)
			m.addElement(file.getName());
		}
		list.setModel(m);
	}


	// ladda och visa bildfil i paramterna; om null, visa ingen bild
	protected void showImage( String filename ) {
		if (filename==null)
			img.setIcon(null);
		else 
			img.setIcon(new ImageIcon(filename));
	}
	
	
	////////// LYSSNARMETODER //////////
	
	// en knapp har tryckts, exekvera metod beroende på händelsekällan
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==add) {
			addFile();
		}
		else if (e.getSource()==remove) {
			removeSelected();
		}
		else if (e.getSource()==up) {
			moveUp();
		}
		else if (e.getSource()==down) {
			moveDown();
		}
	}
	
	
	// det valda elementet i listan har ändrats, ladda och visa denna bild 
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int idx = list.getSelectedIndex();
		String show = null;
		if (idx>=0)
			show = images.get(list.getSelectedIndex()).getAbsolutePath();
		showImage(show);
	}


	// fönstrat har öppnats, läs in ett filnamn för varje rad i filen "imagelist.txt"
	@Override
	public void windowOpened(WindowEvent e) {
		try {
			FileReader fr = new FileReader("imagelist.txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line=br.readLine())!=null) {
				images.add(new File(line));
			}
			br.close();
			
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}
		updateList();
	}


	// fönstret stängs, spara varje filnamn på en ny rad i filen "imagelist.txt"
	@Override
	public void windowClosing(WindowEvent e) {
		try {
			FileWriter fw = new FileWriter("imagelist.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			for (File s : images) {
				bw.write(s+"\n");
			}
			bw.close();
			
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}
		
	}


	@Override
	public void windowClosed(WindowEvent e) {}


	@Override
	public void windowIconified(WindowEvent e) {}


	@Override
	public void windowDeiconified(WindowEvent e) {}


	@Override
	public void windowActivated(WindowEvent e) {}


	@Override
	public void windowDeactivated(WindowEvent e) {}

	
	////////// MAIN //////////

	public static void main(String[] args) {
		new SlideshowViewer();
	}

}
