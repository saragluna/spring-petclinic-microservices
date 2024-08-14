package com.azure.example.petclinic;

import com.azure.runtime.host.DistributedApplication;
import com.azure.runtime.host.dcp.DcpAppHost;
import com.azure.runtime.host.extensions.microservice.common.resources.ConfigServerService;
import com.azure.runtime.host.extensions.microservice.common.resources.ZipkinServerService;
import com.azure.runtime.host.extensions.spring.SpringExtension;
import com.azure.runtime.host.resources.JavaComponentEurekaServerForSpring;

public class PetClinicAppHost implements DcpAppHost {

    public static void main(String[] args) {
        new PetClinicAppHost().boot(args);
    }

    @Override
    public void configureApplication(DistributedApplication app) {
        app.printExtensions();
        SpringExtension spring = app.withExtension(SpringExtension.class);

        JavaComponentEurekaServerForSpring discoveryServer = spring
            .addJavaComponentEurekaServerForSpring("eureka");

        ConfigServerService configServer = spring
            .addConfigServer("config-server")
            .withGitRepositoryPath("https://github.com/spring-petclinic/spring-petclinic-microservices-config");

        ZipkinServerService zipkinServer = spring
            .addZipkinServer("zipkin-server");

        spring.addSpringProject("customers-service")
            .withPath("spring-petclinic-customers-service")
            .withReference(configServer)
            .withReference(discoveryServer)
            .withReference(zipkinServer);

        spring.addSpringProject("vets-service")
            .withPath("spring-petclinic-vets-service")
            .withReference(configServer)
            .withReference(discoveryServer)
            .withReference(zipkinServer);

        spring.addSpringProject("visits-service")
            .withPath("spring-petclinic-visits-service")
            .withReference(zipkinServer)
            .withReference(configServer)
            .withReference(discoveryServer);

        spring.addSpringProject("api-gateway")
            .withPath("spring-petclinic-api-gateway")
            .withReference(zipkinServer)
            .withReference(configServer)
            .withReference(discoveryServer)
            .withHttpEndpoint(8080)
            .withExternalHttpEndpoints();

        spring.addSpringProject("admin-server")
            .withPath("spring-petclinic-admin-server")
            .withReference(zipkinServer)
            .withReference(configServer)
            .withReference(discoveryServer)
            .withHttpEndpoint(9090)
            .withExternalHttpEndpoints();

    }
}
