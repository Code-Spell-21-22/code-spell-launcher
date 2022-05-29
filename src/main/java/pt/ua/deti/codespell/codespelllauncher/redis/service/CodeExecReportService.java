package pt.ua.deti.codespell.codespelllauncher.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.deti.codespell.codespelllauncher.code.report.CodeExecReport;
import pt.ua.deti.codespell.codespelllauncher.redis.repository.CodeExecReportRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class CodeExecReportService {

    private final CodeExecReportRepository codeExecReportRepository;

    @Autowired
    public CodeExecReportService(CodeExecReportRepository codeExecReportRepository) {
        this.codeExecReportRepository = codeExecReportRepository;
    }

    public Optional<CodeExecReport> getById(UUID codeUniqueId) {
        return codeExecReportRepository.findById(codeUniqueId.toString());
    }

    public void save(CodeExecReport codeExecReport) {
        codeExecReportRepository.save(codeExecReport);
    }

}
