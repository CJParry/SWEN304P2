/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class LibraryModel {
    Connection con;
    Statement stmt;
    ResultSet rs;
    // For use in creating dialogs and making them modal
    private JFrame dialogParent;


    //should i open new connection every query or??

    public LibraryModel(JFrame parent, String userid, String password) {
        dialogParent = parent;

        try {
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql:" + "//db.ecs.vuw.ac.nz/" + "parrychri1" + "_jdbc";
            con= DriverManager.getConnection(
                    "jdbc:postgresql://db.ecs.vuw.ac.nz/parrychri1_jdbc","parrychri1","dbpassword");

            stmt=con.createStatement();

            //con.close();
        }catch(Exception e){ System.out.println(e);}
    }




    public String bookLookup(int isbn) {
        boolean empty = true;
        String s = "";//"Book Lookup:\n";
        String authors = "";
        String title = "";
        int edition_Num = -1;
        int numOfCopy = -1;
        int numLeft = -1;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT title, edition_no, numOfCop, numLeft, surname FROM book_author NATURAL JOIN author NATURAL JOIN book WHERE isbn =  " + isbn + " ORDER BY authorseqno;");
            while (rs.next()) {
                empty = false;
                title = "   " + rs.getString(1);
                edition_Num = rs.getInt(2);
                numOfCopy = rs.getInt(3);
                numLeft = rs.getInt(4);
                authors += "," + rs.getString(5);
            }

            s += isbn + ": " + title + "\n      Edition: " + edition_Num + " - Number of copies: " + numOfCopy + " - Copies left: " + numLeft + "\n     Authors: " + authors;

        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        if(empty){
            s = "Book Lookup:\n     No such ISBN: " + isbn;
        }
        return s;
    }

    public String showCatalogue() {
        String s = "Show Catalogue\n\n";
        ArrayList<Integer> bookISBNs = new ArrayList<>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT isbn FROM book ORDER BY isbn;");
            while (rs.next())
                bookISBNs.add(rs.getInt(1));

            for(int i = 0; i < bookISBNs.size(); i++){
                s+= bookLookup(bookISBNs.get(i));
            }
        }catch(Exception e){
            System.out.println("Error in showCatalogue");
            System.out.println(e.toString());
        }
        return s;
    }

    public String showLoanedBooks() {
        String s = "Show Loaned Books\n";
        int ISBN = 0;
        String title = "";
        String edition = "Edition: ";
        int numOfCopy = -1;
        int numLeft = -1;
        String borrowers = "Borrowwes:\n    ";
        String authors = "Author: ";
        String city = "";

        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT isbn, title, edition_no, numOfCop, numLeft, surname, l_name, f_name, city FROM book NATURAL JOIN book_author  NATURAL JOIN author NATURAL JOIN cust_book NATURAL JOIN customer;");
            while (rs.next()) {
                ISBN = rs.getInt(1);
                title = rs.getString(2);
                edition += rs.getInt(3);
                numOfCopy = rs.getInt(4);
                numLeft = rs.getInt(5);
                authors += rs.getString(6);
                borrowers += rs.getString(7) + ", " + rs.getString(8) + " - " + rs.getString(9);
            }
        }catch(Exception e){
            System.out.println("Error in showLoanedBooks");
            System.out.println(e.toString());
        }
        if(s.equals("")){
            s = "(No Loaned Books)";
        }
        return s;
    }

    public String showAuthor(int authorID) {
        String books = "     Book(s) Written:\n";
        String s = "";
        String name = "";
        String surname = "";
        int isbn = -1;
        String title = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT name, surname, isbn, title FROM author NATURAL JOIN book_author NATURAL JOIN book WHERE authorId = " + authorID + ";");
            while (rs.next()) {
                name = rs.getString(1);
                surname = rs.getString(2);
                isbn = rs.getInt(3);
                title = rs.getString(4);
                books += "          " + isbn + " - " + title + "\n";
            }
            s = "Show Author:\n     " + authorID + " - " + name + " " + surname + "\n" + books + "\n";
        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return s;
    }

    public String showAllAuthors() {
        String s = "Show All Authors:\n";
        String name = "";
        String surname = "";
        int authorNum = -1;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from author order by authorid;");
            while (rs.next()){
                authorNum = rs.getInt(1);
                name = rs.getString(2);
                surname = rs.getString(3);
                s += "      " + authorNum + ": " + surname + ", " + name + "\n";
            }
        }catch(Exception e){
            System.out.println("Error in showAllAuthors");
            System.out.println(e.toString());
        }
        return s;
    }

    public String showCustomer(int customerID) {
        String s = "";
        String surname = "";
        String name = "";
        String city = "";
        int isbn = -1;
        String title = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT l_name, f_name, city, isbn, title FROM customer NATURAL JOIN cust_book NATURAL JOIN book where customerid = " + customerID + ";");
            while (rs.next()){
                surname = rs.getString(1);
                name = rs.getString(2);
                city = rs.getString(3);
                isbn = rs.getInt(4);
                title = rs.getString(5);
                      }
                               }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        s = "Show Customer:\n" + "  " + customerID + ": " + surname + ", " + name + " - " + city + "\n" + "  Book Borrowed:\n       " + isbn + " - " + title + "\n";
        return s;
    }

    public String showAllCustomers() {
        String s = "Show All Customers:\n";
        int customerId = -1;
        String surname = "";
        String name = "";
        String city = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from customer");
            while (rs.next()){
                customerId = rs.getInt(1);
                surname = rs.getString(2);
                name = rs.getString(3);
                city = rs.getString(4);
                if(city == null) city = "(no city)";
                s+= "   " + customerId + ": " + surname + ", " + name + " - " + city + "\n";
            }
        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return s;
    }

    public String borrowBook(int isbn, int customerID,
                             int day, int month, int year) {
        String date = year + "-" + month + "-" + day;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("INSERT INTO cust_book VALUES(" + isbn + ", " + date + "," + customerID + ");" );
        }catch(Exception e){
            System.out.println("Error in borrowBook");
            System.out.println(e.toString());
        }
        return "Borrowed Book Stub";
    }

    public String returnBook(int isbn, int customerid) {
        return "Return Book Stub";
    }

    public void closeDBConnection() {
        try{
            con.close();
        }
        catch(Exception e) {
            System.out.println("Error in closeDBConnection");
            System.out.println(e.toString());
        }
    }

    public String deleteCus(int customerID) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("DELETE FROM customer WHERE customerId = " + customerID + ";");
        }catch(Exception e){
            System.out.println("Error in deleteCus");
            System.out.println(e.toString());
        }
        return "Deleted Customer " + customerID;
    }

    public String deleteAuthor(int authorID) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("DELETE FROM author WHERE authorId = " + authorID + ";");
        }catch(Exception e){
            System.out.println("Error in deleteAuthor");
            System.out.println(e.toString());
        }
        return "Deleted Author " + authorID;
    }

    public String deleteBook(int isbn) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("DELETE FROM book WHERE isbn = " + isbn + ";");
        }catch(Exception e){
            System.out.println("Error in deleteBook");
            System.out.println(e.toString());
        }

        return "Deleted Book " + isbn;
    }
}