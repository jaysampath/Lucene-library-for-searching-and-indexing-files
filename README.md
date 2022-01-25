<h1> Lucene-library-for-searching-and-indexing-files </h1>

<p> Lucene is a full-text search library in Java which makes it easy to add search functionality to an application or website. It does so by adding content to a full-text index. It then allows you to perform queries on this index, returning results ranked by either the relevance to the query or sorted by an arbitrary field such as a document's last modified date. </p>

<p> The content you add to Lucene can be from various sources, like a SQL/NoSQL database, a filesystem, or even from websites. This project's source is file system </p>

<p> Find more about lucene <a href="http://www.lucenetutorial.com/basic-concepts.html" target="_blank" > here. </a> </p>

<h2> Features </h2>

<ul>
  
  <li> Optimized File Indexing and Searching system. </li>
  
  <li> Creating the new index file when a new file is added to the folder without re-indexing all files. </li>
  
  <li> Deleting index file when its corresponding file is deleted from folder</li>
  
  <li> Updating the index file when a content of the existing file is modified.  </li>
  
</ul>

<h2> Update </h2>
  
  <ul>
  
  <li> folderlevel.java makes the project's use case to folder level. </li>
  <li> The user need not remember which file he modified or deleted or added. Simply he can update the folder index using cindex update '<path>' command </li>
  <li> Implemented CRUD operations on files </li>
  
  </ul>
  
  
