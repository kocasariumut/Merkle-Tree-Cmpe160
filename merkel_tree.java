package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import util.HashGeneration;

public class MerkleTree {

	private Node root;
	private int numberOfProcesses;
	private int numberOfNonLeafNode;
	private ArrayList<String> processes = new ArrayList<String>();


	public MerkleTree(String path) throws NoSuchAlgorithmException, IOException {

		Scanner input = new Scanner(new File(path));
		while(input.hasNextLine()) {
			processes.add(input.nextLine());
		}
		input.close();
		numberOfProcesses = processes.size();
		numberOfNonLeafNode = (int)(Math.pow(2, Math.ceil(log2((double)numberOfProcesses)))-1);
		Queue<Node> nodes = new LinkedList<Node>();
		root = new Node();
		nodes.add(root);
		int countOfNonLeafNodes =1;
		while(nodes.size()!=0&&countOfNonLeafNodes!=numberOfNonLeafNode) {
			Node node = nodes.poll();

			if(countOfNonLeafNodes!=numberOfNonLeafNode) {
				Node leftNode = new Node();
				node.setLeft(leftNode);
				nodes.add(leftNode);
				countOfNonLeafNodes++;

			}
			if(countOfNonLeafNodes!=numberOfNonLeafNode) {
				Node rightNode = new Node();
				node.setRight(rightNode);
				nodes.add(rightNode);
				countOfNonLeafNodes++;

			}
		}
		if(numberOfProcesses%2==0) {
			for(int i=0;i<numberOfProcesses;i=i+2) {
				Node node = nodes.poll();
				Node leftNode = new Node();
				leftNode.setIsLeaf(true);
				leftNode.setInformation(processes.get(i));
				node.setLeft(leftNode);
				Node rightNode = new Node();
				rightNode.setIsLeaf(true);
				rightNode.setInformation(processes.get(i+1));
				node.setRight(rightNode);					
			}
		}
		else {
			for(int i=0;i<numberOfProcesses;i=i+2) {
				Node node = nodes.poll();
				Node leftNode = new Node();
				leftNode.setIsLeaf(true);
				leftNode.setInformation(processes.get(i));
				node.setLeft(leftNode);
				if(i==numberOfProcesses-1) {

				}
				else 
				{
					Node rightNode = new Node();
					rightNode.setIsLeaf(true);
					rightNode.setInformation(processes.get(i+1));
					node.setRight(rightNode);	
				}
			}
		}

		Queue<Node> queue = new LinkedList<Node>();
		queue.add(root);
		while(queue.size()!=0) {
			Node node = queue.poll();
			if(checkHaveaChild(node)==false) {
				node.setRight(null);
			}
			if(node.getLeft()!=null) {
				queue.add(node.getLeft());
			}
			if(node.getRight()!=null) {
				queue.add(node.getRight());
			}			
		}

		Queue<Node> queue1 = new LinkedList<Node>();
		queue1.add(root);
		int i =0;

		while(queue.size()!=0) {
			Node node = queue.poll();
			node.setNumber(i);
			if(node.getLeft()!=null) {

				queue.add(node.getLeft());
			}
			if(node.getRight()!=null) {

				queue.add(node.getRight());
			}			
			i++;
		}

		Queue<Node> queue2 = new LinkedList<Node>();
		queue2.add(root);

		while(queue2.size()!=0) {
			Node node = queue2.poll();

			if(node.getLeft()!=null) {
				node.getLeft().setPath(node.getPath()+"0");
				queue2.add(node.getLeft());
			}
			if(node.getRight()!=null) {
				node.getRight().setPath(node.getPath()+"1");
				queue2.add(node.getRight());
			}			

		}
		constructHash(root);
	}

	public Node getRoot() {
		return this.root;
	}
	private static void constructHash(Node root) throws NoSuchAlgorithmException, IOException {
		if(root!=null&&root.getLeft()!=null&&root.getRight()!=null)
			root.setData(constructHash(root.getLeft(),root.getRight()));

	}
	private static String constructHash(Node left,Node right) throws NoSuchAlgorithmException, IOException {
		String a ="";
		String b ="";


		if(left.getisLeaf()==false) {
			if(left.getLeft()==null&&left.getRight()==null) {
				left.setData("");
			}
			else {
				a = constructHash(left.getLeft(),left.getRight());
				left.setData(a);
			}
		}
		else {
			a = left.calculateData();
		}
		if(right!=null){
			if(right.getisLeaf()==false) {
				if(right.getLeft()==null&&right.getRight()==null) {
					right.setData("");					
				}
				else {
					b = constructHash(right.getLeft(),right.getRight());
					right.setData(b);
				}
			}
			else {
				b = right.calculateData();
			}
		}
		return HashGeneration.generateSHA256(a+b);

	}
	private static double log2(double a) {
		return Math.log10(a)/Math.log10(2);
	}

	public boolean checkAuthenticity(String string) throws FileNotFoundException {

		Queue<Node> nodesInTree= new LinkedList<Node>();		
		nodesInTree.add(root);
		Scanner input = new Scanner(new File(string));		
		ArrayList<String> control = new ArrayList<String>();
		while(input.hasNextLine()) {
			control.add(input.nextLine());
		}
		input.close();
		int i =0;
		while(nodesInTree.size()!=0) {

			Node node = nodesInTree.poll();
			if(!(node.getData().equals(control.get(i)))) {
				return false;
			}
			if(node.getLeft()!=null) {
				nodesInTree.add(node.getLeft());
			}
			if(node.getRight()!=null) {
				nodesInTree.add(node.getRight());
			}
			i++;
		}

		return true;
	}

	private static boolean checkHaveaChild(Node root) {

		int count =0;

		Node a = root.getRight();

		while(a!=null) {
			if(a.getisLeaf()==true) {
				count++;
			}
			a = a.getLeft();
		}

		if(count==0) {
			return false;
		}
		return true;
	}

	public ArrayList<Stack<String>> findCorruptChunks(String string) throws FileNotFoundException {

		ArrayList<Stack<String>> corrupts = new ArrayList <Stack<String>>();

		Scanner input = new Scanner(new File(string));		
		ArrayList<String> control = new ArrayList<String>();
		while(input.hasNextLine()) {
			control.add(input.nextLine());
		}
		input.close();

		int i =0;
		
		Queue<Node> corruptNodes = new LinkedList<Node>();
		
		Queue<Node> nodes = new LinkedList<Node>();

		nodes.add(root);

		while(nodes.size()!=0) {

			Node node = nodes.poll();
			
			if(!(node.getData().equals(control.get(i)))&&node.getisLeaf()==true) {
				corruptNodes.add(node);
				
			}
						
			if(node.getLeft()!=null) {
				
				nodes.add(node.getLeft());
			}
			if(node.getRight()!=null) {
				nodes.add(node.getRight());
			}
			i++;
		}
		int corruptNodesSýze = corruptNodes.size();
		for(int j=0;j<corruptNodesSýze;j++) {
			
			Stack<String> corruptPath = new Stack<String>();
			
			corruptPath.push(root.getData());
			
			Node currentNode = root;
			
			Node node = corruptNodes.poll();
			
			String path = node.getPath();
			
			for(int h =0;h<path.length();h++) {
				char a = path.charAt(h);
				
				if(a =='0') {
					corruptPath.push(currentNode.getLeft().getData());
					currentNode = currentNode.getLeft();
				}
				
				if(a =='1') {
					corruptPath.push(currentNode.getRight().getData());
					currentNode = currentNode.getRight();
				}				
			}			
			corrupts.add(corruptPath);
		}
		return corrupts;
	}
}
