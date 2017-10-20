
import javax.swing.*;
import java.util.logging.*;

class ProcessLog{
    public static ProcessLog instance = null;
    public static ProcessLog getInstance(){
        if (instance==null){
            instance = new ProcessLog();
        }
        return instance;
    }
    public Logger logger = Logger.getLogger("processLog");
    public Logger requestLogger = Logger.getLogger("requestLog");
    ProcessLog(){
        try {
            String logPath = "./log/processLog";
            logger.setLevel(Level.ALL);
            FileHandler fileHandler = new FileHandler(logPath);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setUseParentHandlers(true);
            logger.addHandler(fileHandler);

            String requestLogPath = "./log/requestLog";
            requestLogger.setLevel(Level.ALL);
            FileHandler fileHandler1 = new FileHandler(requestLogPath);
            fileHandler1.setFormatter(new SimpleFormatter());
            requestLogger.setUseParentHandlers(true);
            requestLogger.addHandler(fileHandler1);

        } catch (Exception e){
            System.out.println("初始化日志失败 " + e.toString());
        }
    }
    public void server(String msg){
        logger.severe(msg);
    }


}

