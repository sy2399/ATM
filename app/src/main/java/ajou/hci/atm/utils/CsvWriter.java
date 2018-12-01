package ajou.hci.atm.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class CsvWriter {

    private PrintWriter pw;
    private char separator;
    private char escapeChar;
    private String lineEnd;
    private char quoteChar;

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char NO_QUOTE_CHARACTER = '\u0000';
    private static final char NO_ESCAPE_CHARACTER = '\u0000';
    private static final String DEFAULT_LINE_END = "\n";
    private static final char DEFAULT_QUOTE_CHARACTER = '"';
    private static final char DEFAULT_ESCAPE_CHARACTER = '"';

    public CsvWriter(Writer writer) {
        this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    private CsvWriter(Writer writer, char separator, char quoteChar, char escapeChar, String lineEnd) {
        this.pw = new PrintWriter(writer);
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
        this.lineEnd = lineEnd;
    }

    public void writeNext(String[] nextLine) {

        if (nextLine == null)
            return;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(separator);
            }

            String nextElement = nextLine[i];
            if (nextElement == null)
                continue;
            if (quoteChar != NO_QUOTE_CHARACTER)
                sb.append(quoteChar);
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (escapeChar != NO_ESCAPE_CHARACTER && nextChar == quoteChar) {
                    sb.append(escapeChar).append(nextChar);
                } else if (escapeChar != NO_ESCAPE_CHARACTER && nextChar == escapeChar) {
                    sb.append(escapeChar).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }
            if (quoteChar != NO_QUOTE_CHARACTER)
                sb.append(quoteChar);
        }

        sb.append(lineEnd);
        pw.write(sb.toString());

    }

    public void close() throws IOException {
        pw.flush();
        pw.close();
    }

    public void flush() throws IOException {

        pw.flush();

    }

}
