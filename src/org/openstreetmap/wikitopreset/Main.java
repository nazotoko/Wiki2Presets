/*
 * This file is public domain.
 */

package org.openstreetmap.wikitopreset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Main (page)
 */
public class Main {
    private BufferedReader br;
    private String title;
    private ArrayList <Section> listSection;
    public Main(String s) {
        title=s;
        listSection=new ArrayList();
    }
    @SuppressWarnings("empty-statement")
    public void start(InputStream is) throws UnsupportedEncodingException {
        String line;
        Section sc;
        Pattern p = Pattern.compile("==\\s*([^\\s]+)\\s*==");
        Matcher m;

        br=new BufferedReader(new InputStreamReader(is,"UTF-8"));

        
        try {
            while((line = br.readLine()) != null && !line.contains("<textarea")){
            }
            while((line = br.readLine()) != null && !line.contains("</textarea>")){
                m = p.matcher(line);
                if(m.find()){
//                    System.out.println("section:" + m.group(1));
                    sc = new Section(m.group(1));
                    sc.read(br);
                    listSection.add(sc);
                }
            }
        } catch(IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void toXML(PrintWriter pw){
        pw.println("<presets version=\"" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\">");
        pw.println("<group name=\""+title+"\">");
        for(Section s:listSection){
            s.toXML(pw);
        }
        pw.println("</group>");
        pw.println("</presets>");
        pw.flush();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Input OpenStreet Map wiki pgae name.");
            System.out.println("ex)");
            System.out.println("# wikitopreset 'Ja:Howto_Map_A'");
            System.exit(1);
        }
        try {
            URL u = new URL("http://wiki.openstreetmap.org/index.php?title=" + args[0] + "&action=edit");
            Main m=new Main("日本語五十音");
            m.start(u.openStream());
            m.toXML(new PrintWriter(new OutputStreamWriter(System.out,"UTF-8")));
        } catch(MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch(UnsupportedEncodingException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
