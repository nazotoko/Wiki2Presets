/*
 * This file is public domain
 */

package org.openstreetmap.wikitopreset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * a section of page 
 */
public class Section  {
    private String title;
    private ArrayList <Row> rows=new ArrayList<Row>();
    public Section(String s) {
        title=s;
    }
    public void read(BufferedReader br) throws IOException{
        String line;
        Row r;
        while((line = br.readLine()) != null && !line.contains("{|")){
        }
        while((line = br.readLine()) != null && !line.startsWith("|-")){}
        boolean loop=true;
        while(loop){
            r = new Row();
            rows.add(r);
            loop=r.read(br);
        }
    }
    public void toXML(PrintWriter pw){
        if(rows.size() == 0){
            return;
        }
        pw.println("<group name=\""+title+"\">");
        for(Row r: rows){
            r.toXML(pw);
        }
        pw.println("</group>");
        pw.flush();
    }
}
