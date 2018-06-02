/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import javax.swing.*;
import java.sql.*;
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


    /**
     * Working - format, book, no book
     * @param isbn
     * @return
     */
    public String bookLookup(int isbn) {
        String s = "Book Lookup:\n";
        String authors = "";
        String title = "";
        int edition_Num = -1;
        int numOfCopy = -1;
        int numLeft = -1;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT title, edition_no, numOfCop, numLeft, surname FROM book_author NATURAL JOIN author NATURAL JOIN book WHERE isbn =  " + isbn + " ORDER BY authorseqno;");
            if (!rs.isBeforeFirst() ) {
                s += "      No such ISBN: " + isbn + "\n";
            }else {
                while (rs.next()) {
                    title = "   " + rs.getString(1);
                    edition_Num = rs.getInt(2);
                    numOfCopy = rs.getInt(3);
                    numLeft = rs.getInt(4);
                    String authorRaw = rs.getString(5);
                    authors += authorRaw.trim() + ", ";
                }
                authors = authors.substring(0, authors.length()-2);
                s += "      " + isbn + ": " + title.trim() + "\n      Edition: " + edition_Num + " - Number of copies: " + numOfCopy + " - Copies left: " + numLeft + "\n      Authors: " + authors;
            }
        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     * Works
     * @return
     */
    public String showCatalogue() {
        String s = "Show Catalogue:\n\n";
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
        s = s.replace("Book Lookup", "");
        return s;
    }

    /**
     * NOT WORKING
     * @return
     */
    public String showLoanedBooks() {
        String s = "Show Loaned Books\n\n";
        int ISBN = 0;
        String title = "";
        String edition = "Edition: ";
        int numOfCopy = -1;
        int numLeft = -1;
        String borrowers = "Borrowwes:\n    ";
        String author = "Author: ";
        String city = "";

        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT isbn, title, edition_no, numOfCop, numLeft, surname, l_name, f_name, city FROM book NATURAL JOIN book_author  NATURAL JOIN author NATURAL JOIN cust_book NATURAL JOIN customer;");
            if (!rs.isBeforeFirst() ) {
                s += "      (No loaned books)\n";
            }else {
                while (rs.next()) {
                    ISBN = rs.getInt(1);
                    title = rs.getString(2);
                    edition += rs.getInt(3);
                    numOfCopy = rs.getInt(4);
                    numLeft = rs.getInt(5);
                    author += rs.getString(6);
                    borrowers += rs.getString(7) + ", " + rs.getString(8) + " - " + rs.getString(9);
                    s += "   " + ISBN + ": " + title + "\n   Edition: " + edition + " - Number of copies: " + numOfCopy + " - Copies left: " + numLeft + "\n     Author: ";
                }
            }
        }catch(Exception e){
            System.out.println("Error in showLoanedBooks");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     *  WORKS - format, author, no author
     * @param authorID
     * @return
     */
    public String showAuthor(int authorID) {
        String books = "     Book(s) Written:\n";
        String s = "Show Author:\n";
        String name = "";
        String surname = "";
        int isbn = -1;
        String title = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT name, surname, isbn, title FROM author NATURAL JOIN book_author NATURAL JOIN book WHERE authorId = " + authorID + ";");
            if (!rs.isBeforeFirst() ) {
                s += "      No such author ID: " + authorID + "\n";
            }else {
                while (rs.next()) {
                    name = rs.getString(1).trim();
                    surname = rs.getString(2).trim();
                    isbn = rs.getInt(3);
                    title = rs.getString(4);
                    books += "          " + isbn + " - " + title + "\n";
                }
                s = "Show Author:\n     " + authorID + " - " + name + " " + surname + "\n" + books + "\n";
            }}catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     *  WORKING
     * @return
     */
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
                surname = rs.getString(3).trim();
                s += "      " + authorNum + ": " + surname + ", " + name + "\n";
            }
        }catch(Exception e){
            System.out.println("Error in showAllAuthors");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     *  invalid customer working, everything else NOT WORKING
     * @param customerID
     * @return
     */
    public String showCustomer(int customerID) {
        String s = "Show Customer:\n";
        String surname = "";
        String name = "";
        String city = "";
        int isbn = -1;
        String title = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT l_name, f_name, city, isbn, title FROM customer NATURAL JOIN cust_book NATURAL JOIN book where customerid = " + customerID + ";");
            if (!rs.isBeforeFirst() ) {
                s += "    No such customer ID: " + customerID;
            }else {

                while (rs.next()) {
                    surname = rs.getString(1).trim();
                    name = rs.getString(2).trim();
                    city = rs.getString(3);
                    isbn = rs.getInt(4);
                    title = rs.getString(5);
                }
                s = "Show Customer:\n" + "      " + customerID + ": " + surname + ", " + name + " - " + city + "\n" + "      Book Borrowed:\n          " + isbn + " - " + title + "\n";

            }
        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     * WORKING
     * @return
     */
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
                surname = rs.getString(2).trim();
                name = rs.getString(3).trim();
                city = rs.getString(4);
                if(city == null) city = "(no city)";
                s+= "       " + customerId + ": " + surname + ", " + name + " - " + city.trim() + "\n";
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
            rs = stmt.executeQuery("INSERT INTO cust_book VALUES(" + isbn + ", '" + date + "'," + customerID + ");" );
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

    /**
     *  WORKING - deletes customer tuple if they have no books loaned
     * @param customerID
     * @return
     */
    public String deleteCus(int customerID) {
        String s = "";
        try {
            PreparedStatement st = con.prepareStatement("DELETE FROM customer WHERE customerId = " + customerID + ";");
            int i = st.executeUpdate();
            if(i == 0){
                s = "No customer to delete";
            }else {
                s = "Deleted customer " + customerID;
            }
        }catch(Exception e){
            s = " Cannot delete customer " + customerID + " as they still have books borrowed on their account";
            System.out.println("Error in deleteCus");
            System.out.println(e.toString());
        }
        return s;
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