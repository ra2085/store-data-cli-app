package com.rgonzalez.dao.derby;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rgonzalez.converters.TransactionConverter;
import com.rgonzalez.dao.TransactionDAO;
import com.rgonzalez.entities.TransactionEntity;
import com.rgonzalez.exceptions.TransactionNotFoundException;
import com.rgonzalez.repositories.TransactionRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DAO Implementation to create and retrieve entries from a Derby database.
 * @author Ruben Antonio Gonzalez Saldierna
 */
@Service
public class TransactionsDAOImpl implements TransactionDAO{
    
    private final TransactionRepository repository;
    private final TransactionConverter converter;

    @Autowired
    public TransactionsDAOImpl(TransactionRepository repository, TransactionConverter converter) {
        this.repository = repository;
        this.converter = converter; 
    }

    @Override
    public String addTransaction(Long userId, String transaction) throws IOException {
        TransactionEntity toSave = converter.convert(transaction);
        if(!userId.equals(toSave.getUser_id())){
            throw new UnsupportedOperationException("user_id must match the user id in the transaction");
        }
        repository.saveAndFlush(toSave);
        return converter.convert(toSave);
    }

    @Override
    public String showTransaction(Long userId, String transactionId) throws TransactionNotFoundException, JsonProcessingException {
        Optional<TransactionEntity> result = repository.findByTransactionIdAndUserId(transactionId, userId);
        if(!result.isPresent()){
            throw new TransactionNotFoundException();
        }
        return converter.convert(result.get());
    }

    @Override
    public String listTransactions(Long userId) throws JsonProcessingException {
        return converter.convertList(repository.findByUserId(userId));
    }

    @Override
    public String sumTransactions(Long userId) {
        List<Object[]> entries = repository.sumAmountByUserId(userId);
        if(entries.isEmpty()){
            return converter.createSumEntry(userId, new BigDecimal(0.0));//Es mas seguro no decir que un usuario no existe.
        }
        String toReturn = converter.createSumEntry((Long) entries.get(0)[0], (BigDecimal) entries.get(0)[1]);
        return toReturn;
    }
    
    
    
}
