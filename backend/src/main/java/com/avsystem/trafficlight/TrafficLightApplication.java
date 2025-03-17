package com.avsystem.trafficlight;

import com.avsystem.trafficlight.dto.CommandList;
import com.avsystem.trafficlight.dto.SimulationResult;
import com.avsystem.trafficlight.service.TrafficSimulationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
public class TrafficLightApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrafficLightApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(TrafficSimulationService simulationService, ObjectMapper objectMapper, ApplicationContext context) {
        return args -> {
            // Jeśli podano argumenty wiersza poleceń, uruchom symulację w trybie wsadowym
            if (args.length == 2) {
                String inputFilePath = args[0];
                String outputFilePath = args[1];
                
                System.out.println("Running simulation in batch mode");
                System.out.println("Input file: " + inputFilePath);
                System.out.println("Output file: " + outputFilePath);
                
                try {
                    // Wczytaj dane wejściowe z pliku JSON
                    CommandList commandList = objectMapper.readValue(new File(inputFilePath), CommandList.class);
                    
                    // Uruchom symulację
                    SimulationResult result = simulationService.runSimulation(commandList);
                    
                    // Zapisz wyniki do pliku wyjściowego
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFilePath), result);
                    
                    System.out.println("Simulation completed successfully");
                    System.out.println("Results written to: " + Paths.get(outputFilePath).toAbsolutePath());
                    
                    // Zakończ aplikację po wykonaniu symulacji
                    SpringApplication.exit(context, () -> 0);
                } catch (Exception e) {
                    System.err.println("Error running simulation: " + e.getMessage());
                    e.printStackTrace();
                    // Zakończ aplikację z kodem błędu
                    SpringApplication.exit(context, () -> 1);
                }
            }
        };
    }
} 