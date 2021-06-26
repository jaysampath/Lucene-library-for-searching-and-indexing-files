package lucena_practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import org.apache.lucene.analysis.Analyzer;


import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory; 


public class update {
	public static void main(String[] args){
		LuceneTester obj = new LuceneTester();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter parameter 'f' for new file creation :"
				                    + "'t' for modifying existing file");
		String para = sc.nextLine();
		if(para.startsWith("t")) {
			System.out.println("Enter text to be added : ");
			String update = sc.nextLine();
			System.out.println("Enter filename which is to be modified : ");
			String filename = sc.nextLine();
			File res = file_search(filename);
			updatefilecontent(res,update);
			
		}
		else if(para.startsWith("f")) {
			System.out.println("Enter filename to be created : ");
			String fname = sc.nextLine();
			file_create(fname);
			
		}
		else if(para.startsWith("d")) {
			System.out.println("Enter filename to be deleted : ");
			String fname = sc.nextLine();
			fileandindex_delete(fname);
			
		}
   }
	private static void updatefilecontent(File f, String update) {
		// TODO Auto-generated method stub
		
		String str="";
		try {
			 FileReader fr = new FileReader(f.getAbsolutePath());
             int i;    
             while((i=fr.read())!=-1)    
             str += String.valueOf((char)i);    
             fr.close();
             FileWriter fw=new FileWriter(f.getAbsolutePath());    
             str += " " + update;
             fw.write(str);    
             fw.close();
             System.out.println("File Content updated");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error while updating content: "+e);
		}
		
		updateIndex(f);
		
		
	}
	private static void updateIndex(File f) {
		// TODO Auto-generated method stub
		
		try {
			Directory directory = FSDirectory.open(new File("D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\index\\"));
			
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
                    new StandardAnalyzer(Version.LUCENE_36));
            conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(directory,conf);
			
			int total = writer.numDocs();
			System.out.println("Total Index Files before update index: "+total);
			Term term = new Term(LuceneConstants.FILE_NAME,f.getName());
			
			Document newdoc = new Document();
			
			newdoc = Indexer.getDocument(f);
			
			writer.updateDocument(term, newdoc);
			
			 writer.commit();
			 
			 total = writer.numDocs();
			 System.out.println("Total Index Files after update index: "+total);
			 
			 System.out.println("Index updated for filename: "+f.getName());
			 
			//LuceneTester tester = new LuceneTester();
			//tester.search("ldpc");
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error in deleting index File: "+e);
		}
		
		
	}
	private static void fileandindex_delete(String fname) {
		
		File file = new File("D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\data\\"+fname);
		File res = file_search(fname);
			try {
				Directory directory = FSDirectory.open(new File("D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\index\\"));
				
				IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
	                    new StandardAnalyzer(Version.LUCENE_36));
	            conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
				IndexWriter writer = new IndexWriter(directory,conf);
				
				int total = writer.numDocs();
				System.out.println("Total Index Files before deleting a file: "+total);
				writer.deleteDocuments(new Term(LuceneConstants.FILE_NAME,res.getName()));
				
				 writer.commit();
				 
				 total = writer.numDocs();
				 System.out.println("Total Index Files after deleting a file: "+total);
				 
				LuceneTester tester = new LuceneTester();
				tester.search("ldpc");
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error in deleting index File: "+e);
			}
            
			if(file.delete()) {
				System.out.println("File and its index deleted!");
			}
		
	}

	static File file_search(String fname){
		String path = "D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\data\\";
		File d = new File(path);
		File[] arr = d.listFiles(); 
		File res = null;
		for(File f:arr) {
			if(f.getName().toLowerCase().equals(fname.toLowerCase())){
			res=f;
			break;
			}
		}
	  return res;  
	}
	static void file_create(String fname) {
		try {
			File f = new File("D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\data\\"+fname);
			if (f.createNewFile()) {
                System.out.println("File created");
                
                addnewindex(fname);
			}
            else
                System.out.println("File already exists");
        }
        catch (Exception e) {
            System.err.println(e);
        }
	}
	private static void addnewindex(String fname) {
		// TODO Auto-generated method stub
		File res = file_search(fname);
		try {
			Directory directory = FSDirectory.open(new File("D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\index\\"));
			
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
                    new StandardAnalyzer(Version.LUCENE_36));
            conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(directory,conf);
			
			int total = writer.numDocs();
			System.out.println("Total Index Files before Adding new File : "+total);
			
			Document doc = new Document();
			
			doc = Indexer.getDocument(res);
			
			writer.addDocument(doc);
			
			 writer.commit();
			 
			 total = writer.numDocs();
			 System.out.println("Total Index Files After adding new file "+total);
			 System.out.println("New File indexed!");
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error in creating new index File: "+e);
		}
		
	}
}