package com.kevag4.XMchallenge;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.kevag4.XMchallenge.model.CryptoSymbol;
import com.kevag4.XMchallenge.service.CryptoServiceImpl;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class XMchallengeApplication implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(XMchallengeApplication.class);

    private CryptoServiceImpl cryptoService;
    @Autowired
    ServletContext context;

    @Autowired
    public XMchallengeApplication(CryptoServiceImpl cryptoService) {
        this.cryptoService = cryptoService;
    }

    public static void main(String[] args) {
        SpringApplication.run(XMchallengeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Inside run() ");

        for (CryptoSymbol cryptoSymbol : CryptoSymbol.values()) {
            //logger.info("Resource path: " + context.getResource("/resources/" + cryptoSymbol + "_values.csv"));
            try {
                Resource resource = new ClassPathResource(cryptoSymbol + "_values.csv");
                //File file = new File(getClass().getResource(cryptoSymbol + "_values.csv").getFile());
                File file = resource.getFile();
                // File file = new File(context.getRealPath("/src/main/resources/") + cryptoSymbol + "_values.csv");
                if (file.exists()) {
                    logger.info("File exists: " + file.getName());
                    InputStream inputStream = new FileInputStream(file);
                    cryptoService.populateCryptosFromCsv(inputStream);
                } else {
                    logger.info("File does NOT exist");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Adding OpenAPI Bean for swagger UI
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Crypto Investment")
                        .description("Spring XM Crypto Investment Application")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("XM Challenge API Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}
