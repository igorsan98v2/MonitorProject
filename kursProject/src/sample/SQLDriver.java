package sample;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class SQLDriver {


        // JDBC driver name and database URL
         final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
         final String DB_URL = "jdbc:mysql://localhost:3306/?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        //  Database credentials
         final String USER = "root";
         final String PASS = "root";
         private Connection conn = null;
         private Statement stmt = null;
        public SQLDriver() {

            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("Creating database...");
                stmt = conn.createStatement();

                String sql = " CREATE TABLE IF NOT EXISTS `monitor`.`logs` (\n" +
                        "  `log_id` INT NOT NULL AUTO_INCREMENT,\n" +
                        "  `id` INT NOT NULL,\n" +
                        "  `HR` INT NOT NULL,\n" +
                        "  `pSYS` INT NOT NULL,\n" +
                        "  `pDIA` INT NOT NULL,\n" +
                        "  `measureDate` DATETIME NOT NULL,\n" +
                        "  PRIMARY KEY (`log_id`))\n" +
                        "ENGINE = InnoDB\n";
                stmt.executeUpdate(sql);
                sql ="CREATE TABLE IF NOT EXISTS `monitor`.`patients` (\n" +
                        "  `id` INT NOT NULL,\n" +
                        "  `first_name` VARCHAR(45) NOT NULL,\n" +
                        "  `second_name` VARCHAR(45) NOT NULL,\n" +
                        "  `patronymic` VARCHAR(45) NOT NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC))\n" +
                        "ENGINE = InnoDB";
                System.out.println("Database created successfully...");
                stmt.executeUpdate(sql);

            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();
            }catch(Exception e){
                //Handle errors for Class.forName
                e.printStackTrace();
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }// nothing we can do
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }//end finally try
            }//end try
            System.out.println("Goodbye!");
        }//end main
        public int getUsersCount(){
            int userCount =-1;
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("Creating database...");
                stmt = conn.createStatement();
                ResultSet res =  stmt.executeQuery("SELECT count(*) as \"value\" FROM `monitor`.`patients`;");
                while (res.next()) {
                    userCount = res.getInt("value");
                }
                stmt.close();
                conn.close();

            }
            catch (SQLException e){
                e.printStackTrace();
            }

            return userCount;
        }

        public int getPatientLogCount(int id){
        int userCount =-1;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = conn.createStatement();
            ResultSet res =  stmt.executeQuery("SELECT count(*) as \"value\" FROM `monitor`.`logs` WHERE `id`=\""+id+"\";");
            while (res.next()) {
                userCount = res.getInt("value");
            }
            stmt.close();
            conn.close();

        }

        catch (SQLException e){
            e.printStackTrace();
        }

        return userCount;
    }
        public  ArrayList<Patient> getLogsById(int id){
            ArrayList<Patient> patients = new ArrayList<Patient>() ;

            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("getting logs...");
                stmt = conn.createStatement();
                ResultSet res =  stmt.executeQuery("SELECT `HR`,`pSYS`,`pDIA`,`measureDate` FROM `monitor`.`logs` as log WHERE log.id="+id+" ORDER BY TIME(`measureDate`);");
                while (res.next()) {
                    Patient patient = new Patient(id);
                    int hr =res.getInt("HR");
                    int dia = res.getInt("pDIA");
                    int sys = res.getInt("pSYS");
                    Date date = res.getDate("measureDate");
                    Time time = res.getTime("measureDate");
                    System.out.println("hr"+hr+" dia"+dia+" sys"+sys+"date"+date.toString()+"time"+time.toString());
                    LocalDateTime localDateTime = LocalDateTime.of(((java.sql.Date) date).toLocalDate(),time.toLocalTime());
                    patient.makeMeasure(hr,dia,sys,localDateTime);
                    patients.add(patient);
                }
                stmt.close();
                conn.close();

            }
            catch (SQLException e){
                e.printStackTrace();
            }


            return patients;
        }
        public boolean checkPatient(int id){
            boolean isItNew = false;
            try {
                int patients =0;
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("Creating database...");
                stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery("SELECT count(*) as \"value\" FROM `monitor`.`patients`" +
                        "WHERE  `patients`.`id`="+id+";");

                while (res.next()) {
                    patients = res.getInt("value");
                }
                if(patients<1){
                    System.out.println("New patient income !");
                    return  true;
                }
                stmt.close();
                conn.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return isItNew;
        }
        public boolean addPatient(int id ,String fullname[]){
            boolean succed = false;
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("Creating database...");
                stmt = conn.createStatement();
                stmt.execute("INSERT INTO `monitor`.`patients`(`id`,`first_name`,`patronymic`,`second_name`) VALUES ("+"\""+id+"\""+
                        ","+"\""+fullname[0]+"\""+",\""+fullname[1]+"\""+",\""+fullname[2]+"\""+");");

                stmt.close();
                conn.close();
                succed = true;

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return succed;
        }
        public int getAvgHR(int id){
            int avg =-1;
            try {
                conn=null;
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery("SELECT \n" +
                        "    AVG(HR) as value\n" +
                        "FROM\n" +
                        "    `monitor`.`logs` as logs\n" +
                        "WHERE\n" +
                        "    date(measureDate) between date('2018-06-14') AND (\"2018-06-14\")\n" +
                        "    and  id="+id+";");

                while (res.next()) {
                    avg = Math.round( res.getFloat("value"));
                }
                //STEP 4: Execute a query
                System.out.println("Creating database...");

                stmt.close();
                conn.close();

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return avg;
        }
        public int[] getAvgPressure(int id){
            int avg[]= {-1,-1};
            float sys=0;
            float dia=0;
            try {
                conn=null;
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery("SELECT \n" +
                        "    AVG(pSYS) as value\n" +
                        "FROM\n" +
                        "    `monitor`.`logs` as logs\n" +
                        "WHERE\n" +" id="+id+";");

                while (res.next()) {
                    sys = Math.round( res.getFloat("value"));
                }
                res = stmt.executeQuery("SELECT \n" +
                        "    AVG(pDIA) as value\n" +
                        "FROM\n" +
                        "    `monitor`.`logs` as logs\n" +
                        "WHERE\n" +" id="+id+";");

                while (res.next()) {
                    dia = Math.round( res.getFloat("value"));
                }
                //STEP 4: Execute a query
                System.out.println("Creating database...");

                stmt.close();
                conn.close();
                avg[0]= Math.round(dia);
                avg[1]= Math.round(sys);

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return avg;
        }
        public boolean addLogForPatient(Patient patient){
            boolean succed = false;
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("Creating database...");
                stmt = conn.createStatement();
                stmt.execute("INSERT INTO `monitor`.`logs`(`id`,`HR`,`pSYS`,`pDIA`,`measureDate`) VALUES ("+"\""+patient.getId()+"\""+",\""+patient.getHR()+"\""+",\""+patient.getpSYS()+"\""+",\""+patient.getpDIA()+"\""+",\""+patient.getDateAndTime()+"\");");
                stmt.close();
                conn.close();
                succed = true;
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return succed;
        }
    }//end JDBCExample


