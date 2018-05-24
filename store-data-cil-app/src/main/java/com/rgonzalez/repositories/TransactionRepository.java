package com.rgonzalez.repositories;

import com.rgonzalez.entities.TransactionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Out of the box interface to use hibernate + jpa with derby. Saves a lot of time.
 * @author Ruben Antonio Gonzalez Saldierna
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, String>{
    
    @Query("select t from TransactionEntity t where t.transaction_id = ?1 and t.user_id = ?2")
    Optional<TransactionEntity> findByTransactionIdAndUserId(String transactionId, Long userId);
    
    @Query("select t from TransactionEntity t where t.user_id = ?1 order by t.date asc")//chronological order?
    List<TransactionEntity> findByUserId(Long userId);
    
    @Query("select t.user_id, SUM(t.amount) from TransactionEntity t where t.user_id = ?1 group by t.user_id")
    List<Object[]> sumAmountByUserId(Long userId);
    
}
