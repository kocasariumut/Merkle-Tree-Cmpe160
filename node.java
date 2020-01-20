package project;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import util.HashGeneration;

public class Node {
	private Node left;
	private Node right;
	private String information;
	private String data;
	private boolean isLeaf;
	private int number;
	private String path;

	public Node() {
		this.left=null;
		this.right=null;
		this.information="";
		this.data="";
		this.isLeaf=false;
		this.path = "";
	}

	public Node getLeft() {
		return this.left;
	}
	public Node getRight() {
		return this.right;
	}
	public String getData(){
		
		return this.data;
	}
	public boolean getisLeaf() {
		return this.isLeaf;
	}
	public String getInformation() {
		return this.information;
	}
	public int getNumber() {
		return this.number;
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public void setLeft(Node newNode) {
		this.left = newNode;
	}
	public void setRight(Node newNode) {
		this.right = newNode;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public void setIsLeaf(boolean ýsLeaf) {
		this.isLeaf=ýsLeaf;
	}
	public void setData(String hash) {
		this.data=hash;
	}
	public String calculateData() throws NoSuchAlgorithmException, IOException{

		this.data = HashGeneration.generateSHA256(new File(this.information));
		return this.data;

	}
}
