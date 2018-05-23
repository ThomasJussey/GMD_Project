package core;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.util.Date;


import org.apache.lucene.queryparser.classic.QueryParser;



public class DrugBankMatching {

    static final File INDEX_DIR = new File("dbmIndex");

    public DrugBankMatching(String path) {

        boolean create = true;

        if (INDEX_DIR.exists()) {
            System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
            System.exit(1);
        }

        final File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            System.out.println("File '" +path+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {
            Directory directory = FSDirectory.open(INDEX_DIR);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                config.setOpenMode(OpenMode.CREATE);
            } else {
                config.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            IndexWriter writer = new IndexWriter(directory, config);

            System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
            indexDoc(writer, file);
            writer.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    private static void indexDoc(IndexWriter writer, File file) throws IOException {
        int eltCount = 0;
        if (file.canRead() && !file.isDirectory()) {
            // each line of the file is a new document
            try{
                InputStream 	  ips  = new FileInputStream(file);
                InputStreamReader ipsr = new InputStreamReader(ips);
                BufferedReader    br   = new BufferedReader(ipsr);
                String line;
                //initialization
                String genericName      = "";
                ArrayList<String> synonyms         = new ArrayList<>();
                String indication       = "";
                String toxicity         = "";

                while ((line=br.readLine())!=null){

                    // new drug
                    if(line.startsWith("  <name>")){
                        //System.out.println(line+" NAME LOOP");
                        String[] fields = line.split("<name>");
                        //System.out.println(fields[1]);
                        fields = fields[1].split("</name>");
                        //System.out.println(fields[0]);
                        genericName = fields[0];
                        //System.out.println("Adding of : "+genericName);
                        //break;
                    }
                    if(line.startsWith("  <indication>")){
                        //System.out.println(line + " INDICATION LOOP");
                        String[] fields = line.split("<indication>");
                        //System.out.println(fields[1]);
                        fields = fields[1].split("</indication>");
                        //System.out.println(fields[0]);
                        indication = fields[0];
                        //System.out.println("Adding of : "+indication);
                        //break;
                    }
                    if(line.startsWith("  <toxicity>")){
                        //System.out.println(line +" TOXICITY LOOP");
                        String[] fields = line.split("<toxicity>");
                        //System.out.println(fields[1]);
                        fields = fields[1].split("</toxicity>");
                        //System.out.println(fields[0]);
                        toxicity = fields[0];
                        //System.out.println("Adding of : "+toxicity);
                        //break;
                    }
                    if(line.equals("  <synonyms>")){
                        //System.out.println(line + " SYNONYMS LOOP");
                        line = br.readLine();
                        //System.out.println(line + " SYNONYM LOOP");
                        while(line.startsWith("    <synonym ")|| line.startsWith("    <synonym>")){
                            String[] fields = line.split(">");
                            //System.out.println(fields[1]);
                            fields = fields[1].split("<");
                            //System.out.println(fields[0]);
                            synonyms.add(fields[0]);
                            //System.out.println("Adding of: "+ fields[0]);
                            line = br.readLine();
                            //System.out.println(line + " SYNONYM LOOP Following");
                        }
                        //break;
                    }

                    if(line.startsWith("</drug>")){
                        //write the index
                        // make a new, empty document
                        Document doc = new Document();
                        doc.add(new TextField("Name",   genericName,    Field.Store.YES));// indexed and stored
                        doc.add(new TextField("Indication",   indication, 		Field.Store.YES)); // indexed and stored
                        doc.add(new TextField("Toxicity", toxicity, 	Field.Store.YES)); // indexed and stored
                        int n = synonyms.size();
                        int i = 0;
                        while (i<n){
                            String name = "Synonym";
                            doc.add(new TextField(name,     synonyms.get(i), 		Field.Store.YES));  // indexed and stored
                            i=i+1;
                        }
                        //System.out.println(id+" "+genericName);
                        if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                            //System.out.println("adding " + file);
                            writer.addDocument(doc);
                        }else{
                            //System.out.println("updating " + file);
                            writer.updateDocument(new Term("path", file.getPath()), doc);
                        }

                        eltCount++;
                        //clean values
                        genericName      = "";
                        synonyms         = new ArrayList<>();
                        indication       = "";
                        toxicity         = "";
                    }
                }

                br.close();
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
        System.out.println(eltCount+" elts have been added to the index " + System.getProperty("user.dir")+ "/" + INDEX_DIR);
    }

    /** Simple command-line based search demo. */
    public static ArrayList<String> search(String query1, String whatfor) throws Exception {

        String index = "dbmIndex";
        String field = "Name";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        String queryString = query1;
        int hitsPerPage = 100;
        ArrayList<String> results = new ArrayList<>();


        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);

        BufferedReader in = null;
        if (queries != null) {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(queries), "UTF-8"));
            } else {
            in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            }
        QueryParser parser = new QueryParser(Version.LUCENE_40, field, analyzer);
        while (true) {
            if (queries == null && queryString == null) {                        // prompt the user
                System.out.println("Enter query (Press enter to leave): ");
            }

            String line = queryString != null ? queryString : in.readLine();

            if (line == null || line.length() == -1) {
                break;
            }

            line = line.trim();
            if (line.length() == 0) {
                break;
            }

            Query query = /*New TermQuery(new Term("Name","Lepirudin"));*/parser.parse(line);
            System.out.println(line);
            System.out.println("Searching for: " + query.toString(field));

            if (repeat > 0) {                           // repeat & time as benchmark
                Date start = new Date();
                for (int i = 0; i < repeat; i++) {
                    searcher.search(query, null, 100);
                }
                Date end = new Date();
                System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
            }

            results = doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null, whatfor);

            if (queryString != null) {
                break;
            }
        }
        reader.close();
        return results;
    }
            /**
         139   * This demonstrates a typical paging search scenario, where the search engine presents
         140   * pages of size n to the user. The user can then go to the next page if interested in
         141   * the next hits.
         142   *
         143   * When the query is executed for the first time, then only enough results are collected
         144   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
         145   * is executed another time and all hits are collected.
         146   *
         147   */

    public static ArrayList<String> doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage, boolean raw, boolean interactive, String whatfor) throws IOException {
        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
        ArrayList<String> r = new ArrayList<>();

        int numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");
        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        while (true) {
            if (end > hits.length) {
                System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
                System.out.println("Collect more (y/n) ?");
                String line = in.readLine();
                if (line.length() == 0 || line.charAt(0) == 'n') {
                    break;
                }
                hits = searcher.search(query, numTotalHits).scoreDocs;
            }

            end = Math.min(hits.length, start + hitsPerPage);

            for (int i = start; i < end; i++) {
                if (raw) {                              // output raw format
                    System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
                    continue;
                }

                Document doc = searcher.doc(hits[i].doc);

                if (whatfor == "Synonym"){
                    IndexableField[] field = doc.getFields("Synonym");
                    //System.out.println(doc.getFields());
                    int n = field.length;
                    for (int k =0; k<n;k++){
                        //System.out.println(field[k].stringValue());
                        r.add(field[k].stringValue());
                    }
                }
                else {
                    String name = doc.get(whatfor);

                    if (name != null) {
                        System.out.println((i + 1) + ". " + name);
                        r.add(name);
                    } else {
                        System.out.println((i + 1) + ". " + "No "+ whatfor+" for this document");
                    }
                }
            }

            if (!interactive || end == 0) {
                break;
            }

            if (numTotalHits >= end) {
                boolean quit = false;
                while (true) {
                    System.out.print("Press ");
                    if (start - hitsPerPage >= 0) {
                        System.out.print("(p)revious page, ");
                    }
                    if (start + hitsPerPage < numTotalHits) {
                        System.out.print("(n)ext page, ");
                    }
                    System.out.println("(q)uit or enter number to jump to a page.");

                    String line = in.readLine();
                    if (line.length() == 0 || line.charAt(0)=='q') {
                        quit = true;
                        break;
                    }
                    if (line.charAt(0) == 'p') {
                        start = Math.max(0, start - hitsPerPage);
                        break;
                    } else if (line.charAt(0) == 'n') {
                        if (start + hitsPerPage < numTotalHits) {
                            start+=hitsPerPage;
                        }
                        break;
                    } else {
                        int page = Integer.parseInt(line);
                        if ((page - 1) * hitsPerPage < numTotalHits) {
                            start = (page - 1) * hitsPerPage;
                            break;
                        } else {
                            System.out.println("No such page");
                        }
                    }
                }
                if (quit) break;
                end = Math.min(numTotalHits, start + hitsPerPage);
            }
        }
        return r;
    }

  public static void main (String[] args){
        ArrayList<String> results = new ArrayList<>();
        //DrugBankMatching dbm = new DrugBankMatching(".sensibleData/drugbank.xml");
        try {
            results = search("Name:\"Heparin\"", "Synonym");
        }catch (Exception e){
            e.printStackTrace();
        }
      System.out.println(results.get(0));
  }

}