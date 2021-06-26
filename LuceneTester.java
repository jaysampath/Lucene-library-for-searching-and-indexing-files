package lucena_practice;

import java.io.File;
import java.io.IOException; 
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException; 
import org.apache.lucene.search.ScoreDoc; 
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.Arrays;
import java.util.Collections;

public class LuceneTester 
{ 
	String indexDir = "D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\index"; 
	String dataDir = "D:\\Practice_files\\eclipse_files\\eclipse_ee\\project1\\lucena_practice\\src\\main\\java\\test\\data"; 
	Indexer indexer; 
	Searcher searcher;
	
	public static void main(String[] args) 
	{ 
		LuceneTester tester; 
		try 
		{ 
			tester = new LuceneTester(); 
			tester.createIndex(); 
			tester.search("ldpc"); 
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace(); 
		} 
		catch (ParseException e) 
		{ 
			e.printStackTrace(); 
		}
		
	} 
	public void createIndex() throws IOException
	{ 
		indexer = new Indexer(indexDir); 
		int numIndexed; 
		long startTime = System.currentTimeMillis(); 
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter()); 
		long endTime = System.currentTimeMillis(); 
		indexer.close(); 
		System.out.println(numIndexed+" File indexed, time taken: " +(endTime-startTime)+" ms"); 
	} 
	public void search(String searchQuery) throws IOException, ParseException
	{ 
		searcher = new Searcher(indexDir); 
		long startTime = System.currentTimeMillis(); 
		TopDocs hits = searcher.search(searchQuery); 
		long endTime = System.currentTimeMillis(); 
		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime)); 
		
		ScoreDoc[] temp =  sorthelper(hits.scoreDocs);
		
		System.out.println("Matched results are ordered by Last modified time");
		
		for(ScoreDoc scoreDoc : temp) 
		{ 
			Document doc = searcher.getDocument(scoreDoc); 
			
			
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH) + " ||||  Fields: "+ doc.getFields() ); 
		} 
		searcher.close(); 
		
	}
	
	
	private ScoreDoc[] sorthelper(ScoreDoc[] scoreDocs) throws CorruptIndexException, IOException {
		// TODO Auto-generated method stub
		int k =0;
		String[] arr = new String[scoreDocs.length];
		for(ScoreDoc t : scoreDocs) {
			Document doc = searcher.getDocument(t);	
			arr[k++]= (String) doc.get(LuceneConstants.LAST_MODIFIED);
		}
		Arrays.sort(arr,Collections.reverseOrder());
	    
		/*for(String t: arr) {
			System.out.println(t);
		}*/
		
		ScoreDoc[] res = new ScoreDoc[scoreDocs.length];
		int m = 0;
		for(int i = 0 ; i< arr.length; i++) {
			for(int j = 0 ; j< scoreDocs.length;j++) {
				Document doc = searcher.getDocument(scoreDocs[j]);
				
				if(doc.get(LuceneConstants.LAST_MODIFIED).equals(arr[i])) {
					res[m++] = scoreDocs[j];
				}
				
			}
		}
		return res;
	} 
}
