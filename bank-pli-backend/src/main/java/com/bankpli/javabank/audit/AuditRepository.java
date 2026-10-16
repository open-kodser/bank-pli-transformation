package com.bankpli.javabank.audit;import org.springframework.data.repository.Repository;import java.util.List;
public interface AuditRepository extends Repository<AuditRecord,Long>{AuditRecord save(AuditRecord r);List<AuditRecord> findAllByOrderByOccurredAtDesc();}
