package core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class StitchMatching {
    static final File INDEX_DIR = new File("stitchIndex");

    public StitchMatching(String path) {

        boolean create = true;

        if (INDEX_DIR.exists()) {
            System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
            System.exit(1);
        }

        try {
            CSVReader file = new CSVReader(new FileReader(path), '\t');
            System.out.println("atc -> index");
            Date start = new Date();

            Directory directory = FSDirectory.open(INDEX_DIR);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            } else {
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
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

    private static void indexDoc(IndexWriter writer, CSVReader file) throws IOException {
        int eltCount = 0;

        try{  //initialization
            String Name      = "";
            String CUI               = "";

            String[] line;
            file.readNext();
            for (int k =0; k<8;k++){
                file.readNext();
            }
            while ((line = file.readNext()) != null){
                //System.out.println(line.length);
                //System.out.println(line[0]);
                Name = line[0];
                //System.out.println("Name : " +Name);
                CUI = "ATC " +line[3];
                //System.out.println("CUI : " +CUI);


                //System.out.println("---------------------------\n");


                Document doc = new Document();
                doc.add(new TextField("Name",  Name,    Field.Store.YES));// indexed and stored
                doc.add(new TextField("ATC",     CUI, 		Field.Store.YES));  // indexed and stored

                if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                    //System.out.println("adding " + file);
                    writer.addDocument(doc);
                }


                eltCount++;
                //clean values
                Name      = "";
                CUI       = "";


            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(eltCount+" elts have been added to the index " + System.getProperty("user.dir")+ "/" + INDEX_DIR);
    }

    public static ArrayList<String> search(String query1, String whatfor) throws Exception {

        String index = "stitchIndex";
        String field = "Synonym";
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
                    System.out.println(doc.getFields());
                    int n = field.length;
                    for (int k =0; k<n;k++){
                        //System.out.println(field[k].stringValue());
                        r.add(field[k].stringValue());
                    }
                }
                else {
                    String name = doc.get(whatfor);

                    if (name != null) {
                        //System.out.println((i + 1) + ". " + name);
                        r.add(name);
                    } else {
                        //System.out.println((i + 1) + ". " + "No "+ whatfor+" for this document");
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
        StitchMatching stm = new StitchMatching(".sensibleData/stitch.tsv");
        ArrayList<String> results = new ArrayList<>();
        try {
            results = search("Name:\"CIDm00002369\"","ATC");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(results);
    }
}

