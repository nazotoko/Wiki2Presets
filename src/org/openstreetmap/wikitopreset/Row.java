/*
 * This file is public domain
 */

package org.openstreetmap.wikitopreset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * A row of the table
 */
public class Row  {
    private String title;
    private ArrayList<Tag> required;
    private ArrayList<Tag> optional;
    private ArrayList<ArrayList<Tag>> group;
    private ArrayList<String> groupT;
    private String text;

    public Row() {
        required = new ArrayList<Tag>();
        optional = new ArrayList<Tag>();
        group = new ArrayList<ArrayList<Tag>>();
        groupT = new ArrayList<String>();
    }

    public boolean read(BufferedReader br) throws IOException{
        String line=null;
        int column=0;
        int a;
        while(column < 5 && ((line = br.readLine()) != null)){
            if(line.startsWith("|")&&!line.startsWith("|}")){
                column++;
                switch(column){
                    case 1:
                        line=line.replaceAll("\\[\\[[^\\]]+\\]\\]", "");
                        if((a = line.indexOf("|", 2)) > 0){
                            title=line.substring(a+1);
                        } else {
                            title=line.substring(1);
                        }
                        break;
                    case 2:
                        break;
                    case 3:
                        boolean primary=true;
                        while((line = br.readLine()) != null && !line.startsWith("|")){
                            if(line.startsWith("#")){
                                if(line.charAt(1)=='*'){
                                    required.add(new Tag(line, primary));
                                    if(primary){
                                        primary = false;
                                    }
                                } else {
                                    required = new ArrayList<Tag>();
                                    group.add(required);
                                    groupT.add(line.substring(1));
                                    primary = true;
                                }
                            } else if(line.startsWith("*")){
                                required.add(new Tag(line, primary));
                                if(primary) {
                                    primary = false;
                                }
                            }
                        }
                        column++;
                    case 4:
                        while((line = br.readLine()) != null && !line.startsWith("|")){
                            if(line.startsWith("*")){
                                optional.add(new Tag(line,false));
                            }
                        }
                        column++;
                    case 5:
                        text=line.substring(1);
                        while((line = br.readLine()) != null && !line.startsWith("|")){
                            text+=line;
                        }
                        text=text.replaceAll("\\{\\{[A-Z]+\\}\\}", "");
                        break;
                }
            }
        }
        if(line == null){
            return false;
        }
        if(line.startsWith("|}")){
            return false;
        }
        return true;
    }
    public void toXML(PrintWriter pw){
        if(required.size() == 0 && group.size() == 0){
            return;
        }
        if(group.size()>0){
            int i;
            pw.println("<group name=\"" + title + "\">");
            for(i = 0; i < group.size(); i++){
                item(pw, groupT.get(i));
                for(Tag t: group.get(i)){
                    t.toXML(pw, false);
                }
                for(Tag t: optional){
                    t.toXML(pw, true);
                }
                if(text != null && text.length() > 0){
                    pw.println("<label text=\"" + text + "\" />");
                }
                pw.println("</item>");
            }
            pw.println("</group>");
        } else {
            item(pw, title);
            for(Tag t: required){
                t.toXML(pw, false);
            }
            for(Tag t: optional){
                t.toXML(pw, true);
            }
            if(text != null && text.length() > 0){
                pw.println("<label text=\"" + text + "\" />");
            }
            pw.println("</item>");
        }
    }

    private void item(PrintWriter pw,String t) {
        pw.println("<item name=\"" + t + "\">");
        pw.println("<label text=\"" + t + "\" />");
    }
}
