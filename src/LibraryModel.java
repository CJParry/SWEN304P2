/*
 * LibraryModel.java
 * Author: Chris Parry
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

    public LibraryModel(JFrame parent, String userid, String password) {
        dialogParent = parent;
        try {
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql:" + "//db.ecs.vuw.ac.nz/" + "parrychri1" + "_jdbc";
            con= DriverManager.getConnection(
                    "jdbc:postgresql://db.ecs.vuw.ac.nz/parrychri1_jdbc","parrychri1","dbpassword");

            stmt=con.createStatement();

        }catch(Exception e){ System.out.println(e);}
    }

    /**
     * Working - format, book, no book, no author.
     * Don't need to lock as read only transactions
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
            con.setAutoCommit(false);
            stmt = con.createStatement();

            //check book exists
            rs = stmt.executeQuery("SELECT isbn " +
                    "FROM book " +
                    "WHERE isbn = " + isbn + ";");
            if (!rs.isBeforeFirst() ) {
                con.rollback();
                return s + "      No such ISBN: " + isbn + "\n";
            }

            //get book details
            rs = stmt.executeQuery("SELECT title, edition_no, numOfCop, numLeft " +
                    "FROM book " +
                    "WHERE isbn =  " + isbn + ";");

            while (rs.next()) {
                title = rs.getString(1);
                edition_Num = rs.getInt(2);
                numOfCopy = rs.getInt(3);
                numLeft = rs.getInt(4);
            }
            s += "    " + isbn + ": " + title.trim() + "\n       Edition: " + edition_Num + " - Number of copies: " + numOfCopy + " - Copies left: " + numLeft + "\n";

            //get authors
            rs = stmt.executeQuery("SELECT surname " +
                    "FROM book_author " +
                    "NATURAL JOIN author " +
                    "NATURAL JOIN book " +
                    "WHERE isbn =  " + isbn + " " +
                    "ORDER BY authorseqno;");
            if (!rs.isBeforeFirst() ) {
                s += "       No authors";
            }
            else{
                authors = "       Authors: ";
                while(rs.next()){
                    authors += rs.getString(1).trim() + ", ";
                }
                authors = authors.substring(0, authors.length()-2);
                s+= authors;
            }

            con.commit();
            con.setAutoCommit(true);
        }catch(Exception e){
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            System.out.println("Error in booklookup");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     * Works
     * @return
     */
    public String showCatalogue() {
        String s = "Show Catalogue:\n";
        ArrayList<Integer> bookISBNs = new ArrayList<>();
        try {
            stmt = con.createStatement();

            //get all book isbns
            rs = stmt.executeQuery("SELECT isbn FROM book ORDER BY isbn;");
            while (rs.next())
                bookISBNs.add(rs.getInt(1));

            //use isbns to call booklookup method which returns details about the book
            for(int i = 0; i < bookISBNs.size(); i++){
                s+= bookLookup(bookISBNs.get(i));
            }
        }catch(Exception e){
            System.out.println("Error in showCatalogue");
            System.out.println(e.toString());
        }
        s = s.replace("Book Lookup:", "");
        return s;
    }

    /**
     *  WORKING - need to fix format
     * @return
     */
    public String showLoanedBooks() {
        String s = "Show Loaned Books\n\n";
        ArrayList<Integer> bookISBN = new ArrayList();
        try{
            stmt = con.createStatement();

            //get all loaned book isbns
            rs = stmt.executeQuery("SELECT isbn FROM cust_book");
            while(rs.next()){
                bookISBN.add(rs.getInt(1));
            }
            if(bookISBN.isEmpty()){
                s += "      (No loaned books)\n";
            }else{

                //get details about each book and corresponding customer
                for(int i = 0; i < bookISBN.size(); i++) {
                    s += bookLookup(bookISBN.get(i)) + "\nBorrowers:";
                    rs = stmt.executeQuery("SELECT * FROM cust_book NATURAL JOIN customer WHERE isbn = " + bookISBN.get(i) + ";");
                    while (rs.next()) {
                        int customerId = rs.getInt(1);
                        Date dueDate = rs.getDate(3);
                        String lastName = rs.getString(4).trim();
                        String firstName = rs.getString(5).trim();
                        String city = rs.getString(6);

                        s += "\n        Name = " + firstName + " " + lastName + " CustomerId = " + customerId + " Due date = " + dueDate.toString() + " City = " + city;
                    }

                }
            }
        }catch(Exception e){
            System.out.println("Error in showLoanedBooks");
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     *  WORKS - format, author, no author. Doesnt display author 0?
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
     *  working
     * @param customerID
     * @return
     */
    public String showCustomer(int customerID) {
        String s = "Show Customer:\n";
        String surname = "";
        String name = "";
        String city = "";
        int isbn;
        String title ;
        try {
            stmt = con.createStatement();

            //check customer exists
            rs = stmt.executeQuery("SELECT l_name, f_name, city " +
                    "FROM customer " +
                    "WHERE customerID = " + customerID + ";");
            if (!rs.isBeforeFirst() ) {
                return s + "    No such customer ID: " + customerID;
            }
            System.out.println("Customer exists");
            while (rs.next()) {
                surname = rs.getString(1).trim();
                name = rs.getString(2).trim();
                city = rs.getString(3).trim();
            }
            s = "Show Customer:\n" + "      " + customerID + ": " + surname + ", " + name + " - " + city + "\n";

            //check if customer has books borrowed
            rs = stmt.executeQuery("SELECT isbn, title " +
                    "FROM cust_book " +
                    "NATURAL JOIN book " +
                    "WHERE customerid = " + customerID + ";");

            if (!rs.isBeforeFirst() ) {
                System.out.println("No books borrowed");
                s += "      No books borrowed ";
            }else {
                s+= "      Book(s) Borrowed:\n";
                while (rs.next()) {
                    isbn = rs.getInt(1);
                    title = rs.getString(2);
                    s +=  "      " + isbn + " - " + title + "\n";
                }

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
        int customerId;
        String surname;
        String name;
        String city;
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

    /**
     *  Working - need to test update anomilies
     *
     */
    public String borrowBook(int isbn, int customerID,
                             int day, int month, int year) {
        int numLeft = 0;
        String s = "";
        try {
            //start transaction
            stmt.execute("START TRANSACTION READ WRITE ");

            stmt = con.createStatement();
            con.setAutoCommit(false);

            //check cust_book tuple doesnt already exist
            rs = stmt.executeQuery("SELECT * FROM cust_book WHERE isbn = " + isbn + " AND customerID = " + customerID + ";");//" FOR UPDATE;");
            if (rs.isBeforeFirst()) {
                con.rollback();
                return "Customer already has that book borrowed";
            }

            //check customer exists
            rs = stmt.executeQuery("SELECT l_name FROM customer WHERE customerid = " + customerID + " FOR UPDATE;");
            if (!rs.isBeforeFirst()) {
                con.rollback();
                return "No such customer";
            }

            //check book exists
            rs = stmt.executeQuery("SELECT numleft FROM book WHERE isbn = " + isbn + " FOR UPDATE;");
            if (!rs.isBeforeFirst()) {
                con.rollback();
                return "No such book";
            }
            //check copies available
            while(rs.next()) {
                numLeft = rs.getInt(1);
            }
            if(numLeft <= 0) {
                con.rollback();
                return "No copies left sorry";
            }

            //book and customer exist, available and locked

            //update numleft before inserting new numLeft into database
            numLeft--;

            //create new cust_book tuple
            String date = year + "-" + month + "-" + day;
            stmt.execute("INSERT INTO cust_book VALUES (" + isbn + ", '" + date + "'," + customerID + ");");

            //interaction command to pause program to allow for testing
            askToContinue();

            //update book tuple with new number of copies left
            stmt.executeUpdate("UPDATE book SET numLeft = " + numLeft + " WHERE isbn = " + isbn + ";");

            //commit transaction
            stmt.execute("COMMIT");
            con.commit();
            s = "Book borrowed";
            con.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return s;
    }


    /**
     *  Working - need to test update anomalies
     *
     */
    public String returnBook(int isbn, int customerID) {
        int numLeft = 0;
        String s = "";
        try {
            stmt.execute("START TRANSACTION READ WRITE ");

            stmt = con.createStatement();
            con.setAutoCommit(false);
            System.out.println();

            //check cust_book tuple exist
            rs = stmt.executeQuery("SELECT * FROM cust_book WHERE isbn = " + isbn + " AND customerID = " + customerID + " FOR UPDATE;");
            if (!rs.isBeforeFirst()) {
                con.rollback();
                return "No such book borrowed by that customer";
            }else{
                System.out.println("Tuple exists");
            }

            //check copies available and lock book tuple
            rs = stmt.executeQuery("SELECT numleft FROM book WHERE isbn = " + isbn + " FOR UPDATE;");
            while(rs.next()) {
                numLeft = rs.getInt(1);
            }

            //cust_book exists, available and locked

            //update numleft before inserting new numLeft into database
            numLeft++;

            //delete cust_book tuple
            stmt.execute("DELETE FROM cust_book WHERE isbn = " + isbn + " AND customerID = " + customerID + ";");

            //interaction command to pause program to allow for testing
            askToContinue();

            //update book tuple with new number of copies left
            stmt.executeUpdate("UPDATE book SET numLeft = " + numLeft + " WHERE isbn = " + isbn + ";");

            //commit transaction
            stmt.execute("COMMIT");
            con.commit();
            s = "Book returned";
            con.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return s;
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
            System.out.println(e.toString());
        }
        return s;
    }


    /**
     *  Working - deletes author.
     *  If author has books, it sets the books author to default author
     * @param authorID
     * @return
     */
    public String deleteAuthor(int authorID) {
        try {
            stmt = con.createStatement();
            stmt.execute("DELETE FROM author WHERE authorId = " + authorID + ";");
        }catch(Exception e){
            System.out.println("Error in deleteAuthor");
            System.out.println(e.toString());
        }
        return "Deleted Author " + authorID;
    }

    /**
     *  WORKING - deletes book tuple if there are none on loan.
     *  Sets book_author isbn to default due to constraint
     * @param isbn
     * @return
     */
    public String deleteBook(int isbn) {
        String s = "";
        try {
            PreparedStatement st = con.prepareStatement("DELETE FROM book WHERE isbn = " + isbn + ";");
            int i = st.executeUpdate();
            if(i == 0){
                s = "No book to delete";
            }else {
                s = "Deleted book " + isbn;
            }
        }catch(Exception e){
            s = " Cannot delete book " + isbn + " as there are copies out on loan";
            System.out.println(e.toString());
        }
        return s;
    }

    /**
     *    Interaction command to pause program to allow for testing
     **/
    private void askToContinue() {
        JOptionPane.showMessageDialog(dialogParent, "Program paused for concurrency testing. Press 'Yes' to continue");
    }
}