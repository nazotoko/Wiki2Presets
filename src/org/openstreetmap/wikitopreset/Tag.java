/*
 * This file is public domain.
 */

package org.openstreetmap.wikitopreset;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tag 
 */
public class Tag {
    private String key=null;
    private String subkey=null;
    private String value=null;
    private String kl=null;
    private String vl=null;
    private String comment=null;
    private boolean primary=false;
    private boolean linkKey = false;

    final static private Pattern p=Pattern.compile("\\{\\{[Tt]?ag\\|([^}]+)\\}\\}\\s*(.*)$");
    final static private Pattern p2=Pattern.compile("\\{\\{[Tt]?ag\\|([^}]+)\\}\\}\\s*(.*)$");
    public Tag(String line,boolean primary) {
        this.primary=primary;
        Matcher m=p.matcher(line);
        if(m.find()){
            String [] sa=m.group(1).split("\\|");
            int order=1;
            int templateParameter=-1;
            for(String s:sa){
                if((templateParameter = s.indexOf("=")) > 0){
                    if(s.startsWith("kl=")){
                        kl = s.substring(templateParameter+1);
                    } else if(s.startsWith("vl=")){
                        vl = s.substring(templateParameter+1);
                    } else if(s.startsWith("subkey=") || s.startsWith(":=")){
                        subkey = s.substring(templateParameter+1);
                    }
                } else if(order==1){
                    key=s;
                    order++;
                } else {
                    if(order>2){
                        linkKey = true;
                    }
                    order++;
                    value=s;
                }
            }
            comment = m.group(2);
//            System.out.println("kl:"+kl+", key:"+key+", vl="+vl+", value:"+value+", comment:"+comment);
        }
    }
    public void toXML(PrintWriter pw,boolean optional){
        if(key == null){
            return;
        }
        if(primary){
            if(!linkKey && value != null && !(value.contains("/") || value.contains(";"))){
                pw.print("<link href=\"http://wiki.openstreetmap.org/wiki/");
                if(vl!=null){pw.print(vl + ":");}
                pw.println("Tag:" + key + "=" + value + "\" />");
            } else {
                pw.print("<link href=\"http://wiki.openstreetmap.org/wiki/");
                if(kl!=null){pw.print(kl + ":");}
                pw.println("Key:" + key + "\" />");
            }
        }
        String tagKey;
        if(subkey != null){
            tagKey = key + ":" + subkey;
        } else {
            tagKey = key;
        }
        if(key.startsWith("name") || key.startsWith("operator") || key.startsWith("ref")){
            pw.print("<text key=\""+tagKey+"\" default=\"");
            if(value != null){
                pw.print(value);
            }
            pw.print("\" text=\"");
            if(comment!=null){
                pw.print(comment+" ");
            }
            pw.print(tagKey);
            pw.println("\" />");
        } else if(value != null && value.contains("/")){
            String[] combo = value.split("/");
            pw.print("<combo key=\"" + tagKey + "\" values=\"");
            pw.print(combo[0]);
            for(int i = 1; i < combo.length; i++){
                pw.print("," + combo[i]);
            }
            pw.print("\" ");
            pw.print("text=\"");
            if(comment!=null){
                pw.print(comment+" ");
            }
            pw.print(tagKey);
            pw.print("\" delete_if_empty=\"");
            if(optional){
                pw.print("true");
            } else {
                pw.print("false");
            }
            pw.println("\" />");
        } else if(key.startsWith("internet_access")){
            pw.print("<combo key=\"" + tagKey + "\" values=\"wlan,wired,terminal,public,service\" ");
            if(value!=null){
                pw.print("default=\""+value+"\" ");
            }
            pw.print("text=\"");
            if(comment!=null){
                pw.print(comment+" ");
            }
            pw.print(tagKey);
            pw.print("\" delete_if_empty=\"");
            if(optional){
                pw.print("true");
            } else {
                pw.print("false");
            }
            pw.println("\" />");
        } else if(key.startsWith("religion")){
            pw.print("<combo key=\"" + tagKey + "\" values=\"bahai,buddhist,christian,hindu,jain,jewish,masonic,multifaith,muslim.pastafarian,scientologist,shinto,sikh,spiritualist,taoist,unitarian,zoroastrian\" ");
            if(value!=null){
                pw.print("default=\""+value+"\" ");
            }
            pw.print("text=\"");
            if(comment!=null){
                pw.print(comment+" ");
            }
            pw.print(tagKey);
            pw.print("\" delete_if_empty=\"");
            if(optional){
                pw.print("true");
            } else {
                pw.print("false");
            }
            pw.println("\" />");
        } else {
            if(value==null){
                pw.print("<text key=\"" + tagKey + "\" value=\"\" text=\"");
                if(comment != null){
                    pw.print(comment + " ");
                }
                pw.print(tagKey);
                pw.println("\" />");
            } else {
                if(optional){
                    pw.print("<combo key=\"" + tagKey + "\" values=\"" + value + "\" text=\"");
                    if(comment != null){
                        pw.print(comment + " ");
                    }
                    pw.print(tagKey);
                    pw.println("\" delete_if_empty=\"true\" />");
                } else {
                    pw.println("<key key=\"" + tagKey + "\" value=\"" + value + "\" />");
                    pw.println("<label text=\"" + tagKey + "=" + value + "\" />");
                }
            }
        }

    }
}
