import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class Parser {

    private static SQLiteC sqliteC;

    public static LinkedList<String> names;
    public static LinkedList<String> hrefs;
    public static LinkedList<String> languages;

    public static void main(String[] args){

        names = takeNames();
        hrefs = takeHref();
        languages = takeLang();

        sqliteC = new SQLiteC("src/main/resources/archive.db");
        sqliteC.connect();
        sqliteC.clean("Projects");

        addToSql();
        print();

        sqliteC.disconnect();
    }

    private static void addToSql(){
        for (int i = 0; i < names.size(); i++) {
            sqliteC.addRow("Projects", names.get(i), languages.get(i), hrefs.get(i));
        }
    }

    private static void print(){
        sqliteC.queryRows("Projects").forEach(project -> {
            System.out.println("Project name - " + project.get(0) +
                    "| language -  " + project.get(1) +
                    "| href - " + project.get(2)
            );
        });
    }

    private static Document takePage() throws IOException {
        String url = "https://github.com/WrapAndKit";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    private static Element takeRepOl() throws IOException {
        Document page = takePage();
        Element repTable = page.select("ol[class=d-flex flex-wrap list-style-none gutter-condensed mb-4]").first();
        return  repTable;
    }

    private static Elements takeNamesStruct(){
        Elements listOfNames = null;
        try {
            Element repOl = takeRepOl();
            listOfNames = repOl.select("a[class=text-bold flex-auto min-width-0]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfNames;
    }

    private static LinkedList<String> takeNames(){
        Elements namesStruct = takeNamesStruct();
        Elements namesSpan = namesStruct.select("span[class=repo]");
        LinkedList<String> names = new LinkedList<>();
        namesSpan.forEach(name -> {
            names.add(name.text());
        });
        return names;
    }

    private static LinkedList<String> takeHref(){
        Elements namesStruct = takeNamesStruct();
        LinkedList<String> hrefs = new LinkedList<>();
        namesStruct.forEach(name ->{
            hrefs.add(name.attr("href").toString());
        });
        return hrefs;
    }

    private static LinkedList<String> takeLang(){
        Element repOl;
        Elements langStruct = null;
        try{
            repOl = takeRepOl();
            langStruct = repOl.select("span[itemprop=programmingLanguage]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedList<String> list = new LinkedList<>();
        langStruct.forEach(struct ->{
            list.add(struct.text());
        });
        return list;
    }

}
