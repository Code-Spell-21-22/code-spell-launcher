package pt.ua.deti.codespell.codespelllauncher.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.codespell.codespelllauncher.code.report.CodeExecReport;

@Repository
public interface CodeExecReportRepository extends CrudRepository<CodeExecReport, String> { }
