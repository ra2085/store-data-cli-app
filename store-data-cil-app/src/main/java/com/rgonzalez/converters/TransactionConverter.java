package com.rgonzalez.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rgonzalez.entities.TransactionEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component that is shared among other beans that need to transform from/to POJO and JSON. I know is kind of dirty. I could set the mapper to be injected from a singleton. Anyway, It will not affect the performance.
 * @author Ruben Antonio Gonzalez Saldierna
 */
@Component
public class TransactionConverter{

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Validator validator;
    
    @Autowired
    public TransactionConverter(Validator validator) {
        this.validator = validator;
    }
    
    
    public String convert(TransactionEntity s) throws JsonProcessingException {
        return localConvert(s);
    }
    
    public String convertList(List<TransactionEntity> s) throws JsonProcessingException {
        return localConvert(s);
    }
    
    public String createSumEntry(Long userId, BigDecimal amount) {
        ObjectNode node = mapper.createObjectNode();
        node.put("user_id", userId);
        node.put("sum", amount);
        String toReturn = null;
        try {
            toReturn = mapper.writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(TransactionConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    public TransactionEntity convert(String s) throws IOException {
        TransactionEntity toReturn = null;
        toReturn = mapper.readValue(s, TransactionEntity.class);
        return toReturn;
    }
    
    private String localConvert(Object obj) throws JsonProcessingException{
        String toReturn = null;
        toReturn = mapper.writeValueAsString(obj);
        return toReturn;
    }
    
}
