package com.rgonzalez;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rgonzalez.dao.TransactionDAO;
import com.rgonzalez.exceptions.TransactionNotFoundException;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Properties;
import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * CLI Application Leveraging CommandLineRunner from Spring Boot.
 * @author Ruben Antonio Gonzalez Saldierna
 */
@SpringBootApplication
public class StoreDataCliApplication  implements CommandLineRunner{
    
    @Bean
    public DataSource dataSource() {
        
        DriverManagerDataSource dm = new DriverManagerDataSource("jdbc:derby:my-db", "root", "");
        Properties properties = new Properties();
        properties.setProperty("create", "true");
        dm.setConnectionProperties(properties);
        dm.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");

        return dm;
    }
    
    @Bean
    public javax.validation.Validator localValidatorFactoryBean() {
       return new LocalValidatorFactoryBean();
    }
    
    @Autowired
    @Lazy
    private TransactionDAO transactionDAO;
    
    @Value("${customTestArgs.customArg0}")
    private String customArg0;
    
    public static void main(String[] args) throws Exception {
        Properties p = System.getProperties();
        //Here I'm making sure the app is storing the data under the folder where the application is.
        ProtectionDomain pDomain = StoreDataCliApplication.class.getProtectionDomain();
        CodeSource cSource = pDomain.getCodeSource();
        String sPath = cSource.getLocation().getPath();
        String path = sPath.substring(0, sPath.indexOf("store-data-cli-app-"));
        path = path.substring(5);
        p.setProperty("derby.system.home", path);
        SpringApplication app = new SpringApplication(StoreDataCliApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
    
    /**
     * CLI runner. Perhaps Spring shell has some "eye candy" features that could make this implementation cleaner. I didn't want to step too far from the requirements so it only takes java arguments.
     * @param args
     * @throws Exception 
     */
    @Override
    public void run(String... args) throws Exception {
        Long userId = 0L;
        try{
        switch(args.length){
            case 0:
            case 1:
                if(!"test".equals(customArg0)){//Workaround to be able to implement some integration tests and save some time.
                    throw new UnsupportedOperationException("Should provide at least two arguments");
                }
                break;
            case 2:
                try{
                    userId = Long.parseLong(args[0]);
                }catch(Exception ex){
                    throw new UnsupportedOperationException("Must provide a valid user_id (Long)");
                }
                switch(args[1]){
                    case "list":
                        System.out.println(transactionDAO.listTransactions(userId));
                        break;
                    case "sum":
                        System.err.println(transactionDAO.sumTransactions(userId));
                        break;
                    default:
                        System.out.println(transactionDAO.showTransaction(userId, args[1]));
                        break;
                } 
                break;
            case 3:
                if(!"add".equals(args[1])){
                    throw new UnsupportedOperationException("Must provide a valid operation to perform");
                }
                try{
                    userId = Long.parseLong(args[0]);
                }catch(Exception ex){
                    throw new UnsupportedOperationException("Must provide a valid user_id (Long)");
                }
                System.out.println(transactionDAO.addTransaction(userId, args[2]));
                break;
            default:
                throw new UnsupportedOperationException("Unsupported arguments");
        }
        } catch (JsonProcessingException ex) {
            System.out.println("Must provide a valid transaction");
        } catch (TransactionNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (UnsupportedOperationException ex) {
            System.out.println(ex.getMessage());
        } catch (ConstraintViolationException ex) {
            System.out.println("Must provide a valid transaction. Here are some tips:");
            ex.getConstraintViolations().stream().forEach(c -> {System.out.println(c.getMessage());});
        } catch (Exception ex){
            System.out.println("Unexpected error");
        }

    }
    
}
