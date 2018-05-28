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
        String s = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT name, surname FROM book_author NATURAL JOIN author WHERE isbn =  " + isbn + " ORDER BY authorseqno;");
            s+= "Name           Surname\n";
            s+= "=========================================\n";
            while (rs.next())
                s+= (rs.getString(1) + "  " + rs.getString(2) + "\n");

        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return s;
    }

    public String showCatalogue() {
        return "Show Catalogue Stub";
    }

    public String showLoanedBooks() {
        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from cust_book;");
            while (rs.next())
                System.out.println(rs.getInt(1) + "  " + rs.getDate(2) + "  " + rs.getInt(3));
        }catch(Exception e){
            System.out.println("Error in showLoanedBooks");
            System.out.println(e.toString());
        }
        return "Show Loaned Books Stub";
    }

    public String showAuthor(int authorID) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from author where authorid = " + authorID);
            while (rs.next())
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
        }catch(Exception e){
            System.out.println("Error in showAuthor");
            System.out.println(e.toString());
        }
        return "Show Author Stub";
    }

    public String showAllAuthors() {
        return "Show All Authors Stub";
    }

    public String showCustomer(int customerID) {
        return "Show Customer Stub";
    }

    public String showAllCustomers() {
        return "Show All Customers Stub";
    }

    public String borrowBook(int isbn, int customerID,
                             int day, int month, int year) {
        return "Borrow Book Stub";
    }

    public String returnBook(int isbn, int customerid) {
        return "Return Book Stub";
    }

    public void closeDBConnection() {
    }

    public String deleteCus(int customerID) {
        return "Delete Customer";
    }

    public String deleteAuthor(int authorID) {
        return "Delete Author";
    }

    public String deleteBook(int isbn) {
        return "Delete Book";
    }
}