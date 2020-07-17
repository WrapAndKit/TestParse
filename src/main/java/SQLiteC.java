import java.sql.*;
import java.util.LinkedList;

public class SQLiteC {

    private Connection connection;
    private String dataBaseUrl;

    public SQLiteC(String dbFileName){ dataBaseUrl = dbFileName;}

    public void connect(){
        Connection connection = null;
        StringBuilder dbUrl = new StringBuilder("jdbc:sqlite:");
        dbUrl.append(dataBaseUrl);
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                    dbUrl.toString());
        }
        catch(Exception connectionError){
            System.err.println(connectionError.getMessage());
        }
        this.connection = connection;
    }

    public void disconnect(){
        this.connection = null;
    }

    public LinkedList<LinkedList> queryRows(String tableName){
        String statement = "SELECT * " +
                "FROM " + tableName;
        LinkedList<LinkedList> rows = new LinkedList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                LinkedList<String> row = new LinkedList<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                rows.add(row);
            }
            resultSet.close();
        }
        catch(SQLException rowQueryException){
            System.err.println(rowQueryException.toString());
        }
        return rows;
    }

    public void addRow(String tableName, Object ... value){
        StringBuilder statementHelper = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            statementHelper.append((i != value.length - 1) ? "?, " : "?");
        }
        String statement = "INSERT INTO " +  tableName +
                " VALUES (" + statementHelper + ")";
        try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
            for (int i = 1; i <= value.length; i++) {
                preparedStatement.setObject(i,value[i-1]);
            }
            preparedStatement.executeUpdate();
        }
        catch(SQLException addException){
            System.err.println(addException.toString());
        }
    }

    public void clean(String tableName){
        String statement = "DELETE FROM " + tableName;
        try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.executeUpdate();
        }
        catch(SQLException deleteException){
            System.err.println(deleteException.toString());
        }
    }

}
