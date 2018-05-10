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
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexWriterConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.util.Date;


import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
                    if(line.startsWith("<name>")){
                        String[] fields = line.split("<name>");
                        System.out.println(fields[1]);
                        fields = fields[0].split("</name>");
                        genericName = fields[0];
                    }
                    if(line.startsWith("<indication>")){
                        String[] fields = line.split("<indication>");
                        fields = fields[0].split("</indication>");
                        indication = fields[0];
                    }
                    if(line.startsWith("<toxicity>")){
                        String[] fields = line.split("<toxicity>");
                        fields = fields[0].split("</toxicity>");
                        toxicity = fields[0];
                    }
                    if(line.equals("<synonyms>")){
                        line = br.readLine();
                        while(line.startsWith("<synonym ")|| line.startsWith("<synonym>")){
                            String[] fields = line.split(">");
                            fields = fields[1].split("<");
                            synonyms.add(fields[0]);
                            line = br.readLine();
                        }
                    }

                    if(line.startsWith("</drug>")){
                        System.out.println("Bonjour");
                        //write the index
                        // make a new, empty document
                        Document doc = new Document();
                        doc.add(new TextField("Name",   genericName,    Field.Store.YES));
                        System.out.println(genericName);// indexed and stored
                        doc.add(new TextField("Indication",   indication, 		Field.Store.YES)); // indexed and stored
                        doc.add(new TextField("Toxicity", toxicity, 	Field.Store.YES)); // indexed and stored
                        int n = synonyms.size();
                        int i = 0;
                        while (i<n){
                            String name = "Synonyms " + i;
                            doc.add(new TextField(name,     synonyms.get(i), 		Field.Store.YES));  // indexed and stored
                        }
                        //System.out.println(id+" "+genericName);
                        if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                            System.out.println("adding " + file);
                            writer.addDocument(doc);
                        }else{
                            System.out.println("updating " + file);
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
    public static void main(String[] args) throws Exception {
        String usage = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/java/4_0/demo.html for details.";
        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }
        String index = "dbmIndex";
        String field = "Name";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        String queryString = null;
        int hitsPerPage = 10;


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

            Query query = new TermQuery(new Term("Name","Lepirudin"));//parser.parse(line);
            System.out.println("Searching for: " + query.toString(field));

            if (repeat > 0) {                           // repeat & time as benchmark
                Date start = new Date();
                for (int i = 0; i < repeat; i++) {
                    searcher.search(query, null, 100);
                }
                Date end = new Date();
                System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
            }

            doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);

            if (queryString != null) {
                break;
            }
        }
        reader.close();
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

    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage, boolean raw, boolean interactive) throws IOException {
        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

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
                String path = doc.get("path");
                if (path != null) {
                    System.out.println((i+1) + ". " + path);
                    String title = doc.get("title");
                    if (title != null) {
                        System.out.println("   Title: " + doc.get("title"));
                    }
                } else {
                    System.out.println((i+1) + ". " + "No path for this document");
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
    }

}