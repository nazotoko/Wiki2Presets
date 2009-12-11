/*
 * This file is public domain.
 */

package org.openstreetmap.wikitopreset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Abstract class for <group>
 */
public abstract class Group {

    public Group() {
    }

    public abstract void read(BufferedReader br) throws IOException;

    public abstract void toXML(PrintWriter pw);

}
