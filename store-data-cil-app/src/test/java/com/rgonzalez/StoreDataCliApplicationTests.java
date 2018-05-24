package com.rgonzalez;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgonzalez.entities.TransactionEntity;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests and some unit tests for fields validation.
 * @author Ruben Antonio Gonzalez Saldierna
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "customTestArgs.customArg0=test"
}
)
public class StoreDataCliApplicationTests {
    
    @Autowired
    ApplicationContext ctx;
    
    @Rule public final OutputCapture outputCapture = new OutputCapture();

    @Test
    public void happyPathTest() throws Exception {
        CommandLineRunner runner = ctx.getBean(CommandLineRunner.class);
        Long randomUserId = generateRadomUserId();
        //Create Transaction
        runner.run(randomUserId.toString(), "add", String.format("{\"user_id\":%d,\"amount\":12.60,\"description\":\"the description\",\"date\":\"2018-12-30\"}", randomUserId));
        outputCapture.flush();
        String storedTransaction = outputCapture.toString();
        outputCapture.reset();
        ObjectMapper mapper = new ObjectMapper();
        TransactionEntity entity = mapper.readValue(storedTransaction, TransactionEntity.class);
        
        Assert.assertNotNull(entity.getTransaction_id());
        
        String uuid = entity.getTransaction_id();
        //Get Transaction
        runner.run(randomUserId.toString(), uuid);
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        entity = mapper.readValue(storedTransaction, TransactionEntity.class);
        Assert.assertEquals(entity.getTransaction_id(), uuid);
        
        //Transaction doesn't exists
        runner.run(randomUserId.toString(), uuid+"x");
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("Transaction not found"));
        
        //List Transactions
        runner.run(randomUserId.toString(), "list");
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        List<TransactionEntity> transactionsList = mapper.readValue(storedTransaction, new TypeReference<List<TransactionEntity>>() { });
        Assert.assertNotNull(transactionsList);
        Assert.assertEquals(1, transactionsList.size());
        Assert.assertEquals(transactionsList.get(0).getTransaction_id(), uuid);
        
        //Empty List Transactions
        runner.run(randomUserId.toString()+"4444", "list");
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("[]"));
        
        //Sum Transactions
        runner.run(randomUserId.toString(), "sum");
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        JsonNode sum = mapper.readTree(storedTransaction);
        Assert.assertNotNull(sum);
        Assert.assertEquals(randomUserId, new Long(sum.get("user_id").asLong()));
        Assert.assertEquals(new BigDecimal(12.6), new BigDecimal(sum.get("sum").asDouble()));
        
        //Sum Transactions to 0 because user doesn't exists (it's safer)
        runner.run(randomUserId.toString()+4444, "sum");
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        sum = mapper.readTree(storedTransaction);
        Assert.assertNotNull(sum);
        Assert.assertEquals(new BigDecimal(0.0), new BigDecimal(sum.get("sum").asDouble()));
        
        //Validate Sum for existing user.
        //Create new transaction
        runner.run(randomUserId.toString(), "add", String.format("{\"user_id\":%d,\"amount\":12.60,\"description\":\"the description\",\"date\":\"2018-12-30\"}", randomUserId));
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        entity = mapper.readValue(storedTransaction, TransactionEntity.class);
        Assert.assertNotNull(entity.getTransaction_id());
        //Sum new transaction
        runner.run(randomUserId.toString(), "sum");
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        sum = mapper.readTree(storedTransaction);
        Assert.assertNotNull(sum);
        Assert.assertEquals(randomUserId, new Long(sum.get("user_id").asLong()));
        Assert.assertEquals(new BigDecimal(25.2), new BigDecimal(sum.get("sum").asDouble()));
        
    }
    
    @Test
    public void invalidJsonAddTransactionTest() throws Exception{
        CommandLineRunner runner = ctx.getBean(CommandLineRunner.class);
        
        Long randomUserId = generateRadomUserId();
        
        //Create Transaction invalid JSON
        runner.run(randomUserId.toString(), "add", String.format("{\"user_id\":%d,\"amount\":12.60\"description\":\"the description\",\"date\":\"2018-12-30\"}", randomUserId));
        outputCapture.flush();
        String storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("Must provide a valid transaction"));

        //Create Transaction invalid description
        runner.run(randomUserId.toString(), "add", String.format("{\"user_id\":%d,\"amount\":12.60,\"date\":\"2018-12-30\"}", randomUserId));
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("description field cannot be null"));
        Assert.assertTrue(storedTransaction.contains("description field cannot be blank"));
        Assert.assertTrue(storedTransaction.contains("description field cannot be empty"));
        
        //Create Transaction null date
        runner.run(randomUserId.toString(), "add", String.format("{\"user_id\":%d,\"amount\":12.60,\"description\":\"the description\"}", randomUserId));
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("date field cannot be null"));
        
        
        //Create Transaction null Amount
        runner.run(randomUserId.toString(), "add", String.format("{\"user_id\":%d,\"description\":\"the description\",\"date\":\"2018-12-30\"}", randomUserId));
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("amount field cannot be null"));
        
        //Create Transaction null user_id
        runner.run(randomUserId.toString(), "add", String.format("{\"amount\":12.60,\"description\":\"the description\",\"date\":\"2018-12-30\"}", randomUserId));
        outputCapture.flush();
        storedTransaction = outputCapture.toString();
        outputCapture.reset();
        Assert.assertTrue(storedTransaction.contains("user_id must match the user id in the transaction"));
        
    }
    
    
    
    /**
     * I need to have the ability to run several times the tests without having to delete the whole database.
     * @return 
     */
    private Long generateRadomUserId(){
        Long min = 500L;
        Long max = 100000L;
        Long generatedLong = min + (long) (Math.random() * (max - min));
        return generatedLong;
    }
    
}
