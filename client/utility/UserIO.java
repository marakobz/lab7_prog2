package utility;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.Stack;


/**
 * Provides normal work of user's input and output
 */
public class UserIO {
    private Scanner scanner;
    private Stack<ArrayDeque<String>> fileContents;
    private Stack<String> fileNames;
    private BufferedWriter writer;
    private boolean scriptMode;
    private boolean fileMode;
    private boolean isEnded;

    public UserIO(Scanner scanner, Writer writer) {
        this.scanner = scanner;
        this.writer = new BufferedWriter(writer);
        scriptMode = false;
        fileMode = false;
        isEnded = false;
    }

    public void setFileMode(){
        fileMode = true;
    }

    /**
     * reads the line entered by the user from the console.
     *
     * @return next line of scanned data
     */
    public String readline() {
        if (scriptMode) {
            if (!fileContents.empty() && !fileContents.peek().isEmpty()) {
                return fileContents.peek().pop();
            } else {
                finishReadScript();
                return readline();
            }
        }
        if (!scanner.hasNextLine() && !isEnded) {
            isEnded = true;
            return "";
        }
        return scanner.nextLine();
    }

    /**
     * writes the `s` object to the output stream and moves the input to the next line.
     */
    public void writeln(Object s) {
        try {
            writer.write(s.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            writeln("Exception while writing to output stream: \n" + e);
        }
    }


    /**
     * manages the work with script files and recursion from its work
     */
    public void startReadScript(String fileName) {
        if (fileContents == null) {
            fileContents = new Stack<>();
            fileNames = new Stack<>();
        }
        if (fileNames.stream().anyMatch(v -> v.equals(fileName))) {
            writeln("File '" + fileName + "' has already been opened");
            return;
        }
        fileContents.push(new ArrayDeque<>());
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                fileContents.peek().add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        fileNames.push(fileName);

        writeln("Start reading from file " + fileName + " ...");
        scriptMode = true;


    }
    /**
     * finishes reading script
     */
    public void finishReadScript() {
        if (!fileContents.empty()) {
            fileContents.pop();
            fileNames.pop();
        }
        if (fileContents.empty()) {
            scriptMode = false;
        }
        writeln("Reading from file was finished");
    }

}
