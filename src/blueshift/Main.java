package blueshift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import blueshift.util.Deflate;
import blueshift.util.DiskImageWriter;
import blueshift.util.DiskStreamWriter;
import blueshift.util.Fletcher;

public class Main {

	public static void main(String[] args) throws IOException {
		
		System.err.println("WARNING: Solution line counts are not supported!");
		
		// Show help screen
		if (args.length == 0) {
			BufferedReader reader = new BufferedReader(new FileReader("INSTRUCTIONS.txt"));
			while(reader.ready()) {
				System.out.println(reader.readLine());
			}
			reader.close();
			System.exit(0);
		}
		
		// Rebuild project name
		String name = "";
		for (String arg : args) {
			name += " " + arg;
		}
		name = name.substring(1); // Remove leading space
		
		// Identify source files
		System.out.println("Searching for source files...");
		String workDir = Paths.get("").toAbsolutePath().normalize().toString();
		List<File> exaFiles = new ArrayList<File>();
		for (String filename : new File(workDir).list()) {
			String[] chunks = filename.split("\\.");
			String extend = chunks[chunks.length - 1].toUpperCase();
			if (extend.equals("EXA")) {
				exaFiles.add(new File(workDir + "/" + filename));
			}
		}
		
		// Write header
		System.out.println("Writing solution header...");
		DiskStreamWriter dsw = new DiskStreamWriter();
		dsw.writeInt(0); // unknown_01 - Value unknown and may be important, who knows?
		dsw.writeString("PB039"); // level_id
		dsw.writeString(name); // solution_name
		dsw.writeInt(0); // unknown_02 - Value is "usually" 0 (haven't seen one that wasn't 0)
		dsw.writeInt(0); // TODO! solution_length - Value is optional, but recommended
		dsw.writeInt(0); // unknown_03 - Value is "usually" 0
		dsw.writeInt(exaFiles.size()); // exa_count
		
		for (File exa : exaFiles) {
			System.out.println("Processing EXA file: " + exa.getName());
			
			// Index 0-9 : @SP0 - @SP9
			// Index 10  : @CFG
			String[] directives = new String[11];
			String exaName = exa.getName().split("\\.")[0];
			String exaCode = "";
			
			System.out.println("  Reading file...");
			BufferedReader reader = new BufferedReader(new FileReader(exa));
			while (reader.ready()) {
				String line = reader.readLine();
				String prefix = "";
				if (line.length() >= 4) {
					prefix = line.substring(0, 4);
				}
				switch (prefix) {
				case "@SP0": directives[0] = line.substring(5); break;
				case "@SP1": directives[1] = line.substring(5); break;
				case "@SP2": directives[2] = line.substring(5); break;
				case "@SP3": directives[3] = line.substring(5); break;
				case "@SP4": directives[4] = line.substring(5); break;
				case "@SP5": directives[5] = line.substring(5); break;
				case "@SP6": directives[6] = line.substring(5); break;
				case "@SP7": directives[7] = line.substring(5); break;
				case "@SP8": directives[8] = line.substring(5); break;
				case "@SP9": directives[9] = line.substring(5); break;
				case "@CFG": directives[10] = line.substring(5); break;
				default: exaCode += line + "\n"; break;
				}
			}
			reader.close();
			
			System.out.println("  Parsing sprite...");
			// sprite[x 0-9][y 0-9]
			boolean[][] sprite = new boolean[10][10];
			for (int y = 0; y < 10; y++) {
				sprite[y] = new boolean[] {
						false, false, false, false, false,
						false, false, false, false, false
				};
				if (directives[y] == null) continue; // Ignore if line empty!
				String[] foo = directives[y].split(""); // Format: @SP0 -##-##-##-
				for (int x = 0; x < 10; x++) {
					switch (foo[x]) {
					case "-": sprite[x][y] = false; break;
					case "#": sprite[x][y] = true; break;
					default:
						System.err.print("WARNING: Unrecognized sprite char " + foo[x]);
						System.err.println(" (" + exaName + "," + x + "," + y);
						break;
					}
				}
			}
			
			System.out.println("  Parsing EXA configuration...");
			byte codeViewMode = 0;
			byte messageMode = 0;
			if (directives[10] != null) {
				String[] foo = directives[10].split(" ");
				codeViewMode = (byte) Integer.parseInt(foo[0]);
				messageMode = (byte) Integer.parseInt(foo[1]);
			}
			
			System.out.println("  Writing data to solution buffer...");
			// ANNOYINGLY VERBOSE: Why do I need to cast a CONSTANT?
			dsw.writeByte((byte) 10); // unknown_04
			dsw.writeString(exaName); // exa_name
			dsw.writeString(exaCode); // exa_code
			dsw.writeByte(codeViewMode); // exa_code_view_mode
			dsw.writeByte(messageMode); // exa_m_register_mode
			for (int y = 0; y < 10; y++) {
				for (int x = 0; x < 10; x++) {
					dsw.writeBoolean(sprite[x][y]);
				}
			}
			
		}
		
		System.out.println("Compressing solution data...");
		byte[] solutionData = Deflate.compress(dsw.toArray());
		dsw = new DiskStreamWriter();
		dsw.writeInt(solutionData.length);
		dsw.writeInt(Fletcher.fletcher16(solutionData));
		for (byte foo : solutionData) {
			dsw.writeByte(foo);
		}
		
		System.out.println("Writing disk image...");
		DiskImageWriter diw = new DiskImageWriter(new File("Disk.png"));
		diw.writeData(dsw.toArray());
		diw.exportImage("PNG", new File(workDir + "/" + name.toUpperCase() + ".png"));
		
	}

}
