package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ATCMatching {

    public static ArrayList<String> ATCMatching(String path, ArrayList<String> ATC) {

        ArrayList<String> Drugs = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                Iterator<String> iterator = ATC.iterator();
                while (iterator.hasNext()) {
                    String it = iterator.next();
                    if (it.contains("ATC ")){
                        it = it.replace("ATC ", "");
                    }
                    String line2 = "E        "+it+" ";
                    if (line.contains(line2)) {
                        String drug = line.replace(line2, "");
                        Drugs.add(drug);
                    }
                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println(e);
        }
        catch (IOException e){
            System.out.println(e);
        }

        return Drugs;

    }

    public static void main(String args[]){
        ArrayList<String> a = new ArrayList<>();
        a.add("ATC C05CX01");
        a.add("ATC C05CX02");

        System.out.println(ATCMatching(".sensibleData/br08303.keg", a));
    }
}

