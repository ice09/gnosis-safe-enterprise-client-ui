package tech.blockchainers.safe.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import tech.blockchainers.safe.GnosisSafeEnterpriseEditionApp;

@CucumberContextConfiguration
@SpringBootTest(classes = GnosisSafeEnterpriseEditionApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
