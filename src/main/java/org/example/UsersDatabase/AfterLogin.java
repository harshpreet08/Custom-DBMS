package org.example.UsersDatabase;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.*;

public class AfterLogin {
    private Map<String, List<String[]>> transactionData = new HashMap<>();

    public static class Table {
        private final String name;
        private final List<Column> columns;

        public Table(String name) {
            this.name = name;
            this.columns = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void addColumn(String columnName, String dataType) {
            Column column = new Column(columnName, dataType);
            columns.add(column);
        }

        public List<? extends Column> getColumns() {
            return columns;
        }
    }
    public static class Column {
        private final String columnName;
        private final String dataType;

        public Column(String columnName, String dataType) {
            this.columnName = columnName;
            this.dataType = dataType;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getDataType() {
            return dataType;
        }
    }

    private Scanner scanner = new Scanner(System.in);

    public void userOptions() {
        while (true) {
            System.out.println("Please enter the operation that you would like to do...");
            System.out.println("1. CREATE");
            System.out.println("2. INSERT");
            System.out.println("3. SELECT");
            System.out.println("4. UPDATE");
            System.out.println("5. DELETE");
            System.out.println("6. Start Transaction");
            System.out.println("7. Exit the application");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        System.out.println("Enter table name: ");
                        String tableName = scanner.nextLine();
                        createQuery(tableName);
                        break;
                    case 2:
                        System.out.println("Enter table name: ");
                        String tN = scanner.nextLine();
                        System.out.println("Enter values (comma-separated): ");
                        String values = scanner.nextLine();
                        insertQuery(tN, values);
                        break;
                    case 3:
                        System.out.println("Enter the table name: ");
                        String tName = scanner.nextLine();
                        selectQuery(tName);
                        break;
                    case 4:
                        System.out.println("Enter the table name in which you want to perform update operation: ");
                        String name = scanner.nextLine();
                        updateQuery(name);
                        break;
                    case 5:
                        System.out.println("Enter the table name in which you want to perform delete operation: ");
                        String deleteTab = scanner.nextLine();
                        deleteQuery(deleteTab);
                        break;
                    case 6:
                        startTransaction();
                        break;
                    case 7:
                        System.out.println("Exiting the application.");
                        scanner.close(); // Close the scanner before exiting
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    private void createQuery(String tableName) {
        Table table = new Table(tableName);
        System.out.println("Enter columns names and data types (e.g., ColumnName DataType):");
        System.out.println("Enter an empty line to finish.");

        List<String> columnData = new ArrayList<>();

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            columnData.add(input);
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(tableName + ".csv"))) {
            if (!columnData.isEmpty()) {
                String[] headers = new String[columnData.size()];
                for (int i = 0; i < columnData.size(); i++) {
                    String[] parts = columnData.get(i).split(" ");
                    if (parts.length == 2) {
                        String columnName = parts[0];
                        String dataType = parts[1];
                        table.addColumn(columnName, dataType);
                        headers[i] = columnName;
                    } else {
                        System.out.println("Invalid input. Please use the format 'ColumnName DataType'.");
                        return;
                    }
                }
                writer.writeNext(headers);
                System.out.println("Table " + tableName + " created successfully.");
            } else {
                System.out.println("No columns provided. Unable to create the table.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create a new table.");
        }
    }

    public void insertQuery(String tableName, String values) {
        String[] valueArray = values.split(",");
        File fileName = new File(tableName + ".csv");

        // Table provided by user does not exist.
        if (!fileName.exists()) {
            createQuery(tableName);
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableName + ".csv"))) {
            String[] headers = null;
            try {
                headers = reader.readNext(); // I am reading the column names here from the csv
            } catch (CsvValidationException e) {
                e.printStackTrace();
                System.out.println("Error reading headers from CSV.");
                return;
            }

            if (headers != null) {
                if (valueArray.length != headers.length) {
                    System.out.println("Number of values does not match the number of columns.");
                    return;
                }

                Map<String, String> valueMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String columnName = headers[i];
                    String value = valueArray[i].trim();
                    valueMap.put(columnName, value);
                }

                try (CSVWriter writer = new CSVWriter(new FileWriter(tableName + ".csv", true))) {
                    String[] newRecord = new String[headers.length];
                    for (int i = 0; i < headers.length; i++) {
                        newRecord[i] = valueMap.get(headers[i]);
                    }
                    writer.writeNext(newRecord);
                    System.out.println("Data inserted into table: " + tableName);
                }
            } else {
                System.out.println("Table does not have headers. Unable to insert data.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to insert data.");
        }
    }

    public void selectQuery(String tableName) {
        File fileName = new File(tableName + ".csv");

        if (!fileName.exists()) {
            System.out.println("The table name you have provided is either wrong or does not exist: " + tableName);
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableName + ".csv"))) {
            String[] headers = reader.readNext();

            if (headers != null) {
                System.out.println("Table: " + tableName);

                // I am displaying the column names here
                String columnNames = String.join(" ", headers);
                System.out.println("Columns: " + columnNames);

                // Displaying the rows here
                int rowNum = 1;
                String[] record;
                while ((record = reader.readNext()) != null) {
                    System.out.print("Row " + rowNum + ": ");
                    String rowData = String.join(" ", record);
                    System.out.println(rowData);
                    rowNum++;
                }
            } else {
                System.out.println("The table entered does not have columns.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read data from the CSV file.");
        } catch (CsvValidationException e) {
            System.out.println("Error reading CSV data.");
            e.printStackTrace();
        }
    }

    public void updateQuery(String tableName) {
        File fileName = new File(tableName + ".csv");

        if (!fileName.exists()) {
            System.out.println("The table name you provided is either incorrect or does not exist: " + tableName);
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(tableName + ".csv"))) {
            String[] headers = reader.readNext();

            if (headers != null) {
                System.out.println("Table: " + tableName);
                String columnNames = String.join(" ", headers);
                System.out.println("Columns: " + columnNames);

                // I am displaying all the rows here
                List<String[]> records = new ArrayList<>();
                String[] record;
                int rowNum = 1;
                while ((record = reader.readNext()) != null) {
                    records.add(record);
                    System.out.print("Row " + rowNum + ": ");
                    String rowData = String.join(" ", record);
                    System.out.println(rowData);
                    rowNum++;
                }

                System.out.println("Enter the row number to update (or 0 to cancel):");
                int rowNumber = scanner.nextInt();
                scanner.nextLine();

                if (rowNumber == 0) {
                    System.out.println("Update operation canceled.");
                    return;
                }

                if (rowNumber < 1 || rowNumber > records.size()) {
                    System.out.println("The row number you have entered is incorrect.");
                    return;
                }

                System.out.println("Enter the new values for the row (comma-separated):");
                String newValues = scanner.nextLine();

                String[] newValueArray = newValues.split(",");
                if (newValueArray.length != headers.length) {
                    System.out.println("Number of values does not match the number of columns.");
                    return;
                }

                // I am updating the selected row with the new values
                // Note that this is happening in the temporary arraylist till now.
                records.set(rowNumber - 1, newValueArray);

                try (CSVWriter writer = new CSVWriter(new FileWriter(tableName + ".csv"))) {
                    writer.writeNext(headers);
                    writer.writeAll(records);
                    System.out.println("Row " + rowNumber + " updated successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to update the row.");
                }
            } else {
                System.out.println("The table entered does not have columns.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read data from the CSV file.");
        } catch (CsvValidationException e) {
            System.out.println("Error reading CSV data.");
            e.printStackTrace();
        }
    }

    public void deleteQuery(String tableName) {
        File csvTableFile = new File(tableName + ".csv");

        // Checking if the table exists
        if (!csvTableFile.exists()) {
            System.out.println("The table you specified does not exist: " + tableName);
            return;
        }

        // Giving option here to the user to choose between deleting the entire table or specific rows
        System.out.println("Do you want to delete the entire table or particular rows?");
        System.out.println("1. Delete the entire table");
        System.out.println("2. Delete specific rows");

        if (scanner.hasNextInt()) {
            int deleteChoice = scanner.nextInt();
            scanner.nextLine();

            if (deleteChoice == 1) {
                // Deleting the entire table
                if (csvTableFile.delete()) {
                    System.out.println("Table " + tableName + " deleted successfully.");
                } else {
                    System.out.println("Failed to delete the table.");
                }
            } else if (deleteChoice == 2) {
                // First I am displaying all available records
                System.out.println("Available Records:");
                try (CSVReader reader = new CSVReader(new FileReader(tableName + ".csv"))) {
                    String[] headers = reader.readNext();
                    String[] record;
                    int rowNum = 1;

                    while ((record = reader.readNext()) != null) {
                        System.out.print("Row " + rowNum + ": ");
                        for (int i = 0; i < headers.length; i++) {
                            System.out.println("\"" + headers[i] + "\", \"" + record[i] + "\"");
                        }
                        rowNum++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to read data from the CSV file.");
                    return;
                } catch (CsvValidationException e) {
                    System.out.println("Error reading CSV data.");
                    e.printStackTrace();
                    return;
                }

                // Asking the user to pick a row to delete
                System.out.print("Enter the row number to delete (or 0 to cancel): ");
                int rowNumber = scanner.nextInt();
                scanner.nextLine();

                if (rowNumber == 0) {
                    System.out.println("Delete operation canceled.");
                    return;
                }

                //Reading data from csv file
                List<String[]> csvData = new ArrayList<>();
                try (CSVReader reader = new CSVReader(new FileReader(tableName + ".csv"))) {
                    String[] headers = reader.readNext();
                    csvData.add(headers);

                    String[] record;
                    while ((record = reader.readNext()) != null) {
                        csvData.add(record);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to read data from the CSV file.");
                    return;
                } catch (CsvValidationException e) {
                    System.out.println("Error reading CSV data.");
                    e.printStackTrace();
                    return;
                }

                if (rowNumber < 1 || rowNumber > csvData.size() - 1) {
                    System.out.println("The row number you have entered is incorrect.");
                    return;
                }

                // Removing the selected row here
                csvData.remove(rowNumber);

                // Writing the updated data back to the CSV file
                try (CSVWriter writer = new CSVWriter(new FileWriter(tableName + ".csv"))) {
                    writer.writeNext(csvData.get(0));
                    csvData.remove(0);
                    writer.writeAll(csvData);
                    System.out.println("Row " + rowNumber + " deleted successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to delete the row.");
                }
            } else {
                System.out.println("Invalid option. Please select either 1 or 2.");
            }
        } else {
            System.out.println("Invalid input. Please enter a valid option.");
            scanner.nextLine();
        }
    }


    //TRANSACTION LOGIC STARTS HERE

    private void startTransaction() {
        System.out.println("Starting a new transaction...");
        boolean inTransaction = true;

        while (inTransaction) {
            System.out.println("Transaction Menu. Select the appropriate number for the operation you want to perform:");
            System.out.println("1. INSERT");
            System.out.println("2. UPDATE");
            System.out.println("3. DELETE");
            System.out.println("4. SELECT");
            System.out.println("5. End Transaction");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        insertData(transactionData);
                        break;
                    case 2:
                        updateData(transactionData);
                        break;
                    case 3:
                        deleteData(transactionData);
                        break;
                    case 4:
                        selectData(transactionData);
                        break;
                    case 5:
                        inTransaction = false;
                        System.out.println("End of transaction.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.nextLine();
            }
        }

        // After the transaction ends, I will ask the user if they want to commit or rollback changes
        System.out.println("Transaction completed. Do you want to commit (Y/N)?");
        String commitChoice = scanner.nextLine().trim();

        if (commitChoice.equalsIgnoreCase("Y")) {
            applyTransactionChanges(transactionData); // Committing changes here
        } else {
            transactionData.clear(); // Rolling back changes here
            System.out.println("Transaction changes rolled back.");
        }
    }

    private void insertData(Map<String, List<String[]>> transactionData) {
        System.out.println("Enter table name: ");
        String tableName = scanner.nextLine();

        File csvFile = new File(tableName + ".csv");

        if (csvFile.exists()) {
            List<String[]> tableData = readCSVFile(tableName);

            System.out.println("Enter comma-separated values: ");
            String values = scanner.nextLine();
            String[] valueArray = values.split(",");

            if (valueArray.length == tableData.get(0).length) {
                tableData.add(valueArray);
                System.out.println("Successful insertion of data.");
                List<String[]> changes = transactionData.computeIfAbsent(tableName, k -> new ArrayList<>());
                changes.add(valueArray);
            } else {
                System.out.println("No. of values does not match the no. of columns.");
            }
        } else {
            System.out.println("Incorrect table name.");
        }
    }

    private void updateData(Map<String, List<String[]>> transactionData) {
    System.out.println("Enter table name: ");
    String tableName = scanner.nextLine();

    File csvFile = new File(tableName + ".csv");

    if (csvFile.exists()) {
        List<String[]> tableData = readCSVFile(tableName);

        System.out.println("Enter row number to update: ");
        int rowNumber = Integer.parseInt(scanner.nextLine()) - 1;

            if (rowNumber >= 0 && rowNumber < tableData.size()) {
                System.out.println("Enter new values (comma-separated): ");
                String newValues = scanner.nextLine();
                String[] newValueArray = newValues.split(",");

                if (newValueArray.length == tableData.get(rowNumber).length) {
                    tableData.set(rowNumber, newValueArray);
                    System.out.println("Row " + (rowNumber + 1) + " updated during the transaction.");
                    List<String[]> changes = transactionData.computeIfAbsent(tableName, k -> new ArrayList<>());
                    changes.add(newValueArray);
                } else {
                    System.out.println("Number of values does not match the number of columns.");
                }
            } else {
                System.out.println("Row number is out of bounds.");
            }
    } else {
        System.out.println("CSV file not found. Table does not exist.");
    }
}

    private void deleteData(Map<String, List<String[]>> transactionData) {
        System.out.println("Enter table name: ");
        String tableName = scanner.nextLine();

        File csvFile = new File(tableName + ".csv");

        if (csvFile.exists()) {
            List<String[]> tableData = readCSVFile(tableName);

            System.out.println("Enter the row number you want to delete: ");
            int rowNumber = Integer.parseInt(scanner.nextLine()) - 1;

            if (rowNumber >= 0 && rowNumber < tableData.size()) {
                tableData.remove(rowNumber);
                System.out.println("Row " + (rowNumber + 1) + " deleted during the transaction.");
                List<String[]> changes = transactionData.computeIfAbsent(tableName, k -> new ArrayList<>());
                changes.add(new String[0]); // I am using the empty array here to flag deletion
            } else {
                System.out.println("Row number is out of bounds.");
            }
        } else {
            System.out.println("CSV file not found. Table does not exist.");
        }
    }

    private void selectData(Map<String, List<String[]>> transactionData) {
        System.out.println("Enter table name: ");
        String tableName = scanner.nextLine();

        File csvFile = new File(tableName + ".csv");

        if (csvFile.exists()) {
            List<String[]> tableData = readCSVFile(tableName);

            for (String[] change : transactionData.getOrDefault(tableName, new ArrayList<>())) {
                if (change.length > 0) {
                    try {
                        int rowNumber = Integer.parseInt(change[0]) - 1;
                        if (rowNumber >= 0 && rowNumber < tableData.size()) {
                            String[] updatedRow = Arrays.copyOfRange(change, 1, change.length);
                            tableData.set(rowNumber, updatedRow);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Format of row number is wrong: " + change[0]);
                    }
                }
            }

            System.out.println("Selected data within the transaction:");

            for (int i = 0; i < tableData.size(); i++) {
                String[] row = tableData.get(i);
                System.out.println("Row " + (i + 1) + ": " + String.join(", ", row));
            }
        } else {
            System.out.println("Table name is incorrect.");
        }
    }

    private void applyTransactionChanges(Map<String, List<String[]>> transactionData) {
        for (Map.Entry<String, List<String[]>> entry : transactionData.entrySet()) {
            String tableName = entry.getKey();
            List<String[]> changes = entry.getValue();

            List<String[]> originalData = readCSVFile(tableName);

            // Applying changes here from transaction data
            for (String[] change : changes) {
                if (change.length > 0) {
                    try {
                        int rowNumber = Integer.parseInt(change[0]) - 1;
                        if (rowNumber >= 0 && rowNumber < originalData.size()) {
                            originalData.set(rowNumber, Arrays.copyOfRange(change, 1, change.length));
                            System.out.println("Row " + (rowNumber + 1) + " updated during the transaction.");
                        } else {
                            System.out.println("Row number is out of bounds.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid row number format: " + change[0]);
                    }
                }
            }

            // I am transferring the updated contents of file
            writeCSVFile(tableName, originalData);
            System.out.println("Changes applied to the table: " + tableName);
        }

        // Freeing up memory
        transactionData.clear();
    }

    private List<String[]> readCSVFile(String tableName) {
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(tableName + ".csv"))) {
            String[] record;
            while ((record = reader.readNext()) != null) {
                data.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            System.out.println("Failed to read data from the CSV file: " + tableName);
        }
        return data;
    }
    private void writeCSVFile(String tableName, List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(tableName + ".csv"))) {
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write data to the CSV file: " + tableName);
        }
    }
}
