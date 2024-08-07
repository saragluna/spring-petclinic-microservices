package com.azure.example.petclinic;

import com.azure.runtime.host.DistributedApplication;
import com.azure.runtime.host.dcp.DcpAppHost;
import com.azure.runtime.host.extensions.microservice.common.resources.EurekaServiceDiscovery;
import com.azure.runtime.host.extensions.spring.SpringExtension;
import com.azure.runtime.host.extensions.spring.resources.SpringProject;

public class PetClinicAppHost implements DcpAppHost {

    public static void main(String[] args) {
        new PetClinicAppHost().boot(args);
    }

    @Override
    public void configureApplication(DistributedApplication app) {
        app.printExtensions();

        SpringExtension spring = app.withExtension(SpringExtension.class);

        EurekaServiceDiscovery discoveryServer = spring
                .addEurekaServiceDiscovery("eureka");

        SpringProject configServer = spring.addSpringProject("config-server")
                .withPath("spring-petclinic-config-server")
                .withHttpEndpoint(8888)
                .withExternalHttpEndpoints();

        spring.addSpringProject("customers-service")
                .withPath("spring-petclinic-customers-service")
                .withDependency(configServer)
                .withDependency(discoveryServer)
                .withEnvironment("SERVER_PORT", "8081")
                .withReference(discoveryServer);

        spring.addSpringProject("vets-service")
                .withPath("spring-petclinic-vets-service")
                .withDependency(configServer)
                .withDependency(discoveryServer)
                .withEnvironment("SERVER_PORT", "8082")
                .withReference(discoveryServer);

        spring.addSpringProject("visits-service")
                .withPath("spring-petclinic-visits-service")
                .withDependency(configServer)
                .withDependency(discoveryServer)
                .withEnvironment("SERVER_PORT", "8083")
                .withReference(discoveryServer);

        spring.addSpringProject("api-gateway")
                .withPath("spring-petclinic-api-gateway")
                .withDependency(configServer)
                .withDependency(discoveryServer)
                .withEnvironment("SERVER_PORT", "8080")
                .withReference(discoveryServer);

        spring.addSpringProject("admin-server")
                .withPath("spring-petclinic-admin-server")
                .withDependency(configServer)
                .withDependency(discoveryServer)
                .withEnvironment("SERVER_PORT", "9090")
                .withReference(discoveryServer);

    }
}
