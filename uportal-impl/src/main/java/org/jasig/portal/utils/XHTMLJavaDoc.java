/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */
package org.jasig.portal.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.Text;


/**
 * <p>
 * This class converts standard Sun JavaDoc to well-formed
 * XHTML. It requires the use of John Cowan's TagSoup.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @author George Lindholm (modified for uPortal)
 * @version 1.0
 *
 */
class XHTMLJavaDoc {

    private static Builder builder
      = new Builder(new org.ccil.cowan.tagsoup.Parser(),
         false, new HTMLFixFactory());


    private static class HTMLFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".html")) return true;
            if (pathname.isDirectory()) return true;
            return false;
        }

    }


    public static void main(String[] args) {

        try {
            File indir = new File(args[0]);
            process(indir);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private static void process(File indir) {

        FileFilter htmlfilter = new HTMLFilter();
        if (indir.exists() && indir.isDirectory()) {
            File[] files = indir.listFiles(htmlfilter);
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory()) {
                    process(f);
                }
                else {
                    try {
                        Document doc = builder.build(f);
                        DocType doctype = new DocType("html",
                          "-//W3C//DTD XHTML 1.0 Frameset//EN",
                          "http://www.w3.org/TR/2000/REC-xhtml1-20000126/DTD/xhtml1-frameset.dtd");
                        doc.setDocType(doctype);
                        Attribute en = new Attribute("lang", "en-US");
                        Attribute xmlen = new Attribute("xml:lang",
                          "http://www.w3.org/XML/1998/namespace", "en-US");
                        Element root = doc.getRootElement();
                        root.addAttribute(en);
                        root.addAttribute(xmlen);
                        Attribute version = root.getAttribute("version");
                        if (version != null) root.removeAttribute(version);
                        Element body = root.getFirstChildElement("body", "http://www.w3.org/1999/xhtml");
                        Element frameset = root.getFirstChildElement("frameset", "http://www.w3.org/1999/xhtml");
                        if (frameset != null && body != null) {
                            root.removeChild(body);
                        }
                        Serializer serializer = new HTMLSerializer(new FileOutputStream(f));
                        if (false) {
                         	serializer.setIndent(1);
                         	serializer.setMaxLength(512);
                        }
                        serializer.write(doc);
                        serializer.flush();
                    }
                    catch (ParsingException ex) {
                        ex.printStackTrace();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        else {
            System.err.println("Could not locate source directory: " + indir);
        }

    }


    private static class HTMLFixFactory extends NodeFactory {

        public Nodes finishMakingElement(Element element) {

            if (element.getLocalName().equals("i")) {
                element.setLocalName("span");
                element.addAttribute(new Attribute("style", "font-style: italic"));
            }
            else if (element.getLocalName().equals("b")) {
                element.setLocalName("span");
                element.addAttribute(new Attribute("style", "font-weight: bold"));
            }

            return new Nodes(element);

        }

    }


    private static class HTMLSerializer extends Serializer {

        HTMLSerializer(OutputStream out) throws UnsupportedEncodingException {
            super(out, "ISO-8859-1");
        }

        protected void writeXMLDeclaration() {
        }

        protected void writeEmptyElementTag(Element element)
          throws IOException {
             super.writeStartTag(element);
             super.writeEndTag(element);
        }
    }


}