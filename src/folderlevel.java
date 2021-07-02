package lucena_practice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class folderlevel {
	static String indexDir = "D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\index\\" ;
	static ArrayList<File> filesinpath;
	static Searcher searcher1;

	public static void main(String[] args) throws Exception{
		Scanner sc = new Scanner(System.in);
		System.out.println("'cindex'  --->  new index or update index\n"
				          +"'list'    --->  list of indexed files\n"
						  +"'csearch' --->  searching\n"
				          +"'delete'  --->  delete a folder from index");
		String para = sc.nextLine();
		if(para.equals("cindex")) {
			System.out.println("'index'  --> new index\n"+ 
							   "'update' --> update index of a folder");
			String para1 = sc.nextLine();
			if(para1.equals("index")) {
				System.out.println("enter path of folder to be indexed : ");
				String path = sc.nextLine();
				LuceneTester tester = new LuceneTester();
				tester.createIndex(path);
				
				}else if(para1.equals("update")) {
					System.out.println("enter path of the folder to be updated : ");
					String path = sc.nextLine();
					filesinpath = new ArrayList<File>();
					getfiles(path);
					updatefolderIndex(path);
				}else {
					System.out.println("invalid command");
				}
		
		}else if(para.equals("list")) {
			Directory directory = FSDirectory.open(new File(indexDir));
			IndexReader reader = IndexReader.open(directory, true);
			IndexSearcher searcher =  new IndexSearcher(reader); 
			Query query = new MatchAllDocsQuery();
			TopDocs hits = searcher.search(query,LuceneConstants.MAX_SEARCH);
			ScoreDoc[] temp = hits.scoreDocs;
			System.out.println("indexed file count : " + temp.length);
			for(ScoreDoc sdoc : temp) {
				Document doc = searcher.doc(sdoc.doc); 
				System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH)); 
			}
			
		}else if(para.equals("csearch")) {
			System.out.println("enter content to be searched from index : ");
			String content = sc.nextLine();
			LuceneTester searcher = new LuceneTester();
			searcher.search(content);
		}else if(para.equals("delete")) {
			System.out.println("enter path of the folder to be deleted : ");
			String path = sc.nextLine();
			deletefolderindex(path);
			}else {
				System.out.println("invalid command");
			}
	}
	private static void updatefolderIndex(String path) {
		try {
			Directory directory = FSDirectory.open(new File(indexDir));
			 IndexReader reader = IndexReader.open(directory, true);
			IndexSearcher searcher =  new IndexSearcher(reader); 
			QueryParser queryParser = new QueryParser(Version.LUCENE_36,LuceneConstants.FILE_PATH, new StandardAnalyzer(Version.LUCENE_36)); 
			String[] name = path.split("\\\\");
			String querystr = name[name.length-2] + " AND " + name[name.length-1];
			Query query = queryParser.parse(querystr);
			
			System.out.println(querystr);
			
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,new StandardAnalyzer(Version.LUCENE_36));
            conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(directory,conf);
			int total = writer.numDocs();
			System.out.println("Total Index Files before updating folder "+path+" :  "+total);
			TopDocs hits = searcher.search(query, LuceneConstants.MAX_SEARCH);
			ScoreDoc[] temp =  hits.scoreDocs;
			System.out.println("No of files already indexed in  path: "+path+" is: "+temp.length);
			System.out.println("No of files currently in path: "+path+" is: "+filesinpath.size());
			int diff = temp.length-filesinpath.size();
			ArrayList<Document> indexcontains = new ArrayList<Document>();
			for(ScoreDoc scoreDoc : temp) 
			{ 
				Document doc = searcher.doc(scoreDoc.doc); 
				int ind =-1;
				indexcontains.add(doc);
			    for(int i =0;i<filesinpath.size();i++) {
			    	if(filesinpath.get(i).getCanonicalPath().equals(doc.get(LuceneConstants.FILE_PATH))) {
			    		ind = i;
			    		break;
			    	}
			    }
			    if(ind!=-1) {
			    	filesinpath.remove(ind);
			    	indexcontains.remove(indexcontains.size()-1);
			    }
			}
			if(filesinpath.size()==0 && indexcontains.size()==0) {
				System.out.println("Already updated!");
			}else {
				System.out.println(diff + "  file(s) difference detected. Updating them");
				if(diff<0) {
				for(int i =0; i< filesinpath.size();i++) {
					Document doc = Indexer.getDocument(filesinpath.get(i));
					writer.addDocument(doc);
					writer.commit();
				     }
			      }
			    else if(diff>0) {
			    	Query[] queryarr = new Query[indexcontains.size()];
				    for(int i =0;i<indexcontains.size();i++) {
				    	String name1 = indexcontains.get(i).get(LuceneConstants.FILE_NAME);
				    	if(name1.indexOf(".")>0) {
				    		name1 = name1.substring(0,name1.lastIndexOf("."));
				    	}
				    	queryarr[i] = queryParser.parse(querystr+ " AND "+name1);
				    	System.out.println("deleting  :  "+indexcontains.get(i).get(LuceneConstants.FILE_PATH));
				        System.out.println("query = "+querystr+" AND "+name1);
				        hits = searcher.search(queryarr[i], LuceneConstants.MAX_SEARCH);
						temp =  hits.scoreDocs;
						System.out.println("hits for the above query : "+temp.length);
				    }
					writer.deleteDocuments(queryarr);
			    	writer.commit();
			   }
		}
			int total1 = writer.numDocs();
			System.out.println("Total Index Files after updating folder "+path+" :  "+total1);
			writer.close();
	     }
		catch (Exception e) {
		e.printStackTrace();
		}
	}
	private static void deletefolderindex(String path) {
		try {
			Directory directory = FSDirectory.open(new File(indexDir));
			@SuppressWarnings("deprecation")
			IndexReader reader = IndexReader.open(directory, true);
			IndexSearcher searcher =  new IndexSearcher(reader); 
			QueryParser queryParser = new QueryParser(Version.LUCENE_36,LuceneConstants.FILE_PATH, new StandardAnalyzer(Version.LUCENE_36)); 
			String[] name = path.split("\\\\");
			String querystr =  name[name.length-2]+ " AND " +name[name.length-1];
			Query query = queryParser.parse(querystr);
			TopDocs hits = searcher.search(query,LuceneConstants.MAX_SEARCH);
			ScoreDoc[] temp = hits.scoreDocs;
			System.out.println(temp.length);
			System.out.println(querystr);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
            new StandardAnalyzer(Version.LUCENE_36));
            conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(directory,conf);
			int total = writer.numDocs();
			System.out.println("Total Index Files before deleting folder "+path+" :  "+total);
			writer.deleteDocuments(query);
			writer.commit();
			int total1 = writer.numDocs();
			System.out.println("Total Index Files after deleting folder "+path+" :  "+total1);
			writer.close();
			searcher.close();
	}catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	public static void getfiles(String Path) throws IOException
	{ 
		File[] files = new File(Path).listFiles(); 
		for (File f : files) { 
			if(f.isFile())
			{ 
				filesinpath.add(f); 
			}
			else{
				getfiles(f.getAbsolutePath());
			}
		} 
	}
}