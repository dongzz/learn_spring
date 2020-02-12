package com.dongz.codeutils.entitys.db;


import lombok.Data;

import java.util.List;

/**
 * 数据库实体类
 */
@Data
public class DataBase {

    private static String mysqlUrl = "jdbc:mysql://[ip]:[port]/[db]?useUnicode=true&characterEncoding=utf8";
    private static String oracleUrl = "jdbc:oracle:thin:@[ip]:[port]:[db]";

    /**
     * 数据库类型
     */
    private String dbType;
    private String driver;
    private String userName;
    private String passWord;
    private String url;
    private String db;
    private String port;
    private String ip;
    private List<String> dbList;

    public DataBase() {}

    public DataBase(String dbType) {
        this(dbType,"127.0.0.1","3306","");
    }

    public DataBase(String dbType,String db) {
        this(dbType,"127.0.0.1","3306",db);
    }

    public DataBase(String dbType,String ip,String port,String db) {
        this.dbType = dbType;
        this.db = db;
        this.port = port;
        this.ip = ip;

        if("MYSQL".endsWith(dbType.toUpperCase())) {
            this.driver="com.mysql.jdbc.Driver";
            this.url=mysqlUrl.replace("[ip]",ip).replace("[port]",port).replace("[db]",db);
        }else{
            this.driver="oracle.jdbc.driver.OracleDriver";
            this.url=oracleUrl.replace("[ip]",ip).replace("[port]",port).replace("[db]",db);
        }
    }

    public void setUrl(String db) {
        this.db = db;
        if("MYSQL".endsWith(dbType.toUpperCase())) {
            this.driver="com.mysql.jdbc.Driver";
            this.url=mysqlUrl.replace("[ip]",ip).replace("[port]",port).replace("[db]",db);
        }else{
            this.driver="oracle.jdbc.driver.OracleDriver";
            this.url=oracleUrl.replace("[ip]",ip).replace("[port]",port).replace("[db]",db);
        }
    }
}
