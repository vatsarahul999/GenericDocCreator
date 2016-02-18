package com.dm.GDC.Engine;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.dm.GDC.File.FileDetails;
import com.dm.GDC.File.FileInfo;
import com.dm.GDC.FileParser.HiveParser;
import com.dm.GDC.FileParser.PigParser;
import com.dm.GDC.FileParser.ShellParser;
import com.dm.GDC.Writer.FileCreator;
import com.dm.GDC.Writer.FileMaker;

public class GDCEngine {
	List<FileInfo> filelst;
	String inputPath;
	String outputPath;

	StringBuffer psb;
	StringBuffer hsb;
	StringBuffer ssb;

	GDCEngine() {
		this.inputPath = System.getProperty("user.dir");
		this.outputPath = inputPath + "/docs/";
		this.filelst = new ArrayList<FileInfo>();
		this.psb = new StringBuffer();
		this.hsb = new StringBuffer();
		this.ssb = new StringBuffer();
	}

	public static void main(String[] args) throws Exception {
		GDCEngine eng = new GDCEngine();
		eng.checkParameters();
		eng.ScanInputPath();
		eng.IndentifyFilesandComments();
		eng.WriteComments();
		System.out.println("The process has been completed");

	}

	private void WriteComments() throws Exception {
		// TODO Auto-generated method stub
		FileMaker fm = new FileCreator();
		fm.addContents(ssb.toString());
		fm.addContents(psb.toString());		
		fm.addContents(hsb.toString());
		fm.writeToDisk(this.outputPath);

	}

	private void IndentifyFilesandComments() {
		// TODO Auto-generated method stub
		String type = null;
		psb.append(MakeHeader("Pig Script"));
		hsb.append(MakeHeader("Hive Script"));
		ssb.append(MakeHeader("Shell Script"));
		for (FileInfo fi : filelst) {
			type = fi.getFileType();
			switch (type) {
			case "pig":
				psb.append("\n").append("------------------------------------------------------------------------\n")
						.append(fi.getRelativeFilePath()).append(fi.getFileName())
						.append("\n------------------------------------------------------------------------\n")
						.append(PigParser.getInstance().getComments(fi.getFileContents()));
				break;
			case "hql":
				hsb.append("\n").append("------------------------------------------------------------------------\n")
						.append(fi.getRelativeFilePath())
						.append("\n------------------------------------------------------------------------\n")
						.append(HiveParser.getInstance().getComments(fi.getFileContents()));
				break;
			case "sh":
				ssb.append("\n").append("------------------------------------------------------------------------\n")
						.append(fi.getRelativeFilePath())
						.append("\n------------------------------------------------------------------------\n")
						.append(ShellParser.getInstance().getComments(fi.getFileContents()));
				break;
			default:
				System.out.print("...");
				break;

			}
		}
		System.out.println();

	}

	private String MakeHeader(String string) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= 99; i++) {
			sb.append("#");
		}
		sb.append("\n");
		for (int i = 0; i <= 50-string.length() / 2; i++) {
			sb.append(" ");
		}
		sb.append(string);
		for (int i = 0; i <= 50-string.length() / 2; i++) {
			sb.append(" ");
		}
		sb.append("\n");
		for (int i = 0; i <= 99; i++) {
			sb.append("#");
		}
		sb.append("\n");
		
		return sb.toString();
	}

	private void ScanInputPath() throws Exception {
		Queue<File> dirs = new LinkedList<File>();
		File rootDir = new File(this.inputPath);
		dirs.add(new File(this.inputPath));
		FileInfo fd = null;
		while (!dirs.isEmpty()) {
			for (File f : dirs.poll().listFiles()) {

				if (f.isDirectory()) {
					dirs.add(f);
				} else if (f.isFile()) {

					fd = new FileDetails(rootDir.getPath(), f);
					this.filelst.add(fd);

				}
			}
		}

	}

	private void checkParameters() {
		if (System.getProperty("inputPath") != null)
			this.inputPath = System.getProperty("inputPath");
		this.outputPath = inputPath + "/docs/";
		if ((System.getProperty("outputPath") != null))
			this.outputPath = System.getProperty("outputPath");
		System.out.println("The Directory that will be scaned is " + this.inputPath);
		System.out.println("The Document will be created in " + this.outputPath);
	}

}
