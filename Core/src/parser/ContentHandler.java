package parser;


import java.io.IOException;

public interface ContentHandler {
    void startJSON() throws ParseExeption, IOException;

    void endJSON() throws ParseExeption, IOException;

    boolean startObject() throws ParseExeption, IOException;

    boolean endObject() throws ParseExeption, IOException;

    boolean startObjectEntry(String var1) throws ParseExeption, IOException;

    boolean endObjectEntry() throws ParseExeption, IOException;

    boolean startArray() throws ParseExeption, IOException;

    boolean endArray() throws ParseExeption, IOException;

    boolean primitive(Object var1) throws ParseExeption, IOException;
}