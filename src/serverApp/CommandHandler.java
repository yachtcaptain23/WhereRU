package serverApp;

import java.util.regex.*;
import java.sql.*;


public class CommandHandler {
    
    static String DATABASE_URL = "jdbc:mysql://localhost/whereru";
    
    public CommandHandler(){}
    
    /**
     * Handles the input.
     * The prefix "ssql:" sends the suffix to do SQL processing
     * @param inputLine command
     * @return Command to web-interface
     */
    public static String processInput(String inputLine){
        if(inputLine == null) return "";
        System.out.println("Received" + inputLine);
        
        // Regex search on the word
        Pattern pattern2 = Pattern.compile("^Qsql:");
        Matcher matcher2 = pattern2.matcher(inputLine);
        Pattern pattern1 = Pattern.compile("^Esql:");
        Matcher matcher1 = pattern1.matcher(inputLine);
        
        if(matcher2.find()){
            return querySQLCommand(inputLine.substring(5, inputLine.length()));
        }
        
        if(matcher1.find()){
            return executeSQLCommand(inputLine.substring(5, inputLine.length()));
        }
            
        return "";
    }
    
    /**
     * Performs specified SQL command. 
     * @param command is the SQL command
     * @return SQL's return value
     */
    public static String executeSQLCommand(String command){
        Connection connection = null;
        Statement statement = null;

        
        System.out.println("RECEIVED @ ESQL = " + command);
        String [] commands = command.split(";");

        for(int i=0; i<commands.length; i++){
            try{
                System.out.println(commands[i]);
                connection = DriverManager.getConnection(DATABASE_URL,"root", "891221");
                statement = connection.createStatement();
    
                statement.executeUpdate(commands[i]);
                
            }
            catch(SQLException sqlexception){
                sqlexception.printStackTrace();
            }
        }
        
        return "";
    }
    
    /**
     * Performs specified SQL command. 
     * @param command is the SQL command
     * @return SQL's return value
     */
    public static String querySQLCommand(String command){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;
        String retval = "";
        if(command.contains("FROM CONTACT")){
            retval = "CONTACT:";
        }
        if(command.contains("FROM MESSAGE")){
        	retval = "MESSAGE:";
        }

        try{
            System.out.println(command);
            connection = DriverManager.getConnection(DATABASE_URL,"root", "891221");
            statement = connection.createStatement();

            resultset=statement.executeQuery(command);

            ResultSetMetaData metadata = resultset.getMetaData();


            for(int i=1;i<=metadata.getColumnCount();i++){
                retval = retval + metadata.getColumnName(i) + ",";
            }
            
            retval = retval + "\n";
            while(resultset.next()){
                for(int e = 1; e <= metadata.getColumnCount(); e++)
                {
                    retval = retval + resultset.getString(e) + ",";
                }
                retval = retval + "\n";
            }
        }
        catch(SQLException sqlexception){
            sqlexception.printStackTrace();
        }
        
        retval = retval + "END_PULL";
        
        System.out.println(retval);
        
        return retval;
    }

}
