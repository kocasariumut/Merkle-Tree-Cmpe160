package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import project.MerkleTree;
import project.Node;


public class Main {

	public static void main(String[] args) {


		MerkleTree m0 = null;
		try {
			m0 = new MerkleTree("sample/white_walker.txt");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		String hash = m0.getRoot().getLeft().getRight().getData();		
		System.out.println(hash);

		boolean valid = false;
		try {
			valid = m0.checkAuthenticity("sample/white_walkermeta.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(valid);



		// The following just is an example for you to see the usage. 
		// Although there is none in reality, assume that there are two corrupt chunks in this example.
		ArrayList<Stack<String>> corrupts = null;
		try {
			corrupts = m0.findCorruptChunks("data/0meta.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Corrupt hash of first corrupt chunk is: " + corrupts.get(0).pop());
		System.out.println("Corrupt hash of second corrupt chunk is: " + corrupts.get(1).pop());

		try {
			download("secondaryPart/data/download_from_trusted.txt");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}

	/**
	 * 
	 * Taken from https://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
	 * 
	 */
	private static void downloadFileFromURL(String urlString, File destination) {    
		try {
			URL website = new URL(urlString);
			ReadableByteChannel rbc;
			rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(destination);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void download(String path) throws NoSuchAlgorithmException, IOException {
		// Entry point for the secondary part

		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		Scanner input = new Scanner(new File(path));

		File e = new File("secondaryPart/data/split");
		e.mkdir();
		int i =0;
		while(input.hasNextLine()) {

			list.add(new ArrayList<String>());
			list.get(i).add(input.nextLine());
			list.get(i).add(input.nextLine());
			list.get(i).add(input.nextLine());			
			if(input.hasNextLine()) {
				input.nextLine();
			}
			i++;
		}
		input.close();
		for(int j =0;j<list.size();j++) {
			String meta = list.get(j).get(0);
			String alt1 = list.get(j).get(1);
			String alt2 = list.get(j).get(2);
			String fileName = findFileName(alt1);
			String fileNameWithoutTxt = findFileNameWithoutTxt(alt1);			
			File w = new File("secondaryPart/data/split/"+fileNameWithoutTxt);
			w.mkdir();

			File f = new File("secondaryPart/data/real"+fileName);
			f.createNewFile();

			File g = new File("secondaryPart/data/"+fileName);
			g.createNewFile();

			PrintStream output = new PrintStream(new File("secondaryPart/data/"+fileName));

			downloadFileFromURL(alt1,f);

			Scanner input2 = new Scanner(new File("secondaryPart/data/real"+fileName));

			int count=0;
			while(input2.hasNextLine()) {
				input2.nextLine();
				count++;
			}
			input2.close();

			Scanner input3 = new Scanner(new File("secondaryPart/data/real"+fileName));

			for(int a=0;a<count;a++) {

				String nameOfFile = input3.nextLine();
				String chunk="";
				if(a<10) {
					chunk = "secondaryPart/data/split/"+fileNameWithoutTxt+"/0"+a; 
				}
				else {
					chunk = "secondaryPart/data/split/"+fileNameWithoutTxt+"/"+a; 
				}

				File h = new File(chunk);
				output.println(chunk);

				h.createNewFile();

				downloadFileFromURL(nameOfFile,h);

			}
			input3.close();

			MerkleTree m0 = new MerkleTree("secondaryPart/data/"+fileName);

			File d = new File("secondaryPart/data/meta"+fileName);
			d.createNewFile();

			downloadFileFromURL(meta,d);


			if(!m0.checkAuthenticity("secondaryPart/data/meta"+fileName)) {

				ArrayList<Stack<String>> corrupts = m0.findCorruptChunks("secondaryPart/data/meta"+fileName);

				ArrayList<Node> corruptNodes = new ArrayList<Node>();

				Queue<Node> nodes = new LinkedList<Node>();
				nodes.add(m0.getRoot());

				int u =0;
				while(nodes.size()!=0) {

					Node current = nodes.poll();

					if(u!=corrupts.size()) {		
						String temp1 = corrupts.get(u).pop();
						if(temp1.equals(current.getData())){

							corruptNodes.add(current);
							u++;
						}
						else {
							corrupts.get(u).push(temp1);
						}
					}

					if(current.getLeft()!=null) {
						nodes.add(current.getLeft());
					}
					if(current.getRight()!=null) {
						nodes.add(current.getRight());
					}
				}

				ArrayList<Integer> nodesInteger = new ArrayList<Integer>();
				for(int y =0;y<corruptNodes.size();y++) {
					int t =0;

					Node a = corruptNodes.get(y);
					String aPath = a.getPath();

					for(int o =0;o<aPath.length();o++) {
						char v = aPath.charAt(o);						
						int b =0;
						if(v=='0') {
							b=0;
						}
						else {
							b=1;
						}

						t+=Math.pow(2, aPath.length()-1-o)*b;

					}

					nodesInteger.add(t);
				}
			
				File t = new File("secondaryPart/data/real"+fileName);
				t.createNewFile();

				File y = new File("secondaryPart/data/"+fileName);
				y.createNewFile();

				PrintStream output2 = new PrintStream(new File("secondaryPart/data/"+fileName));

				downloadFileFromURL(alt2,t);

				Scanner input4 = new Scanner(new File("secondaryPart/data/real"+fileName));

				int count2=0;
				while(input4.hasNextLine()) {
					input4.nextLine();
					count2++;
				}
				input4.close();

				Scanner input5 = new Scanner(new File("secondaryPart/data/real"+fileName));

				int temp =0;
				for(int a=0;a<count2;a++) {

					String chunk ="";
					if(a<10) {
						chunk = "secondaryPart/data/split/"+fileNameWithoutTxt+"/0"+a; 
					}
					else {
						chunk = "secondaryPart/data/split/"+fileNameWithoutTxt+"/"+a; 
					}
					output2.println(chunk);
					String nameOfFile = input5.nextLine();
					if(a ==nodesInteger.get(temp)) {

						File h = new File(chunk);
						h.createNewFile();
						downloadFileFromURL(nameOfFile,h);
						if(nodesInteger.size()-1!=temp) {
							temp++;
						}

					}

				}
				input5.close();

			}																																							
		}				
	}

	public static String findFileName(String file) {

		for(int i =0;i<file.length();i++) {
			char a = file.charAt(i);

			if(a =='/') {
				file = file.substring(i+1);
				i=0;

			}
		}

		return file;
	}

	public static String findFileNameWithoutTxt(String file) {

		for(int i =0;i<file.length();i++) {
			char a = file.charAt(i);

			if(a =='/') {
				file = file.substring(i+1);
				i=0;

			}
		}
		file = file.substring(0, file.length()-4);

		return file;
	}

}
