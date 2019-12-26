package com.myorg;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.services.ec2.Vpc;


public class SandboxApp {
    public static void main(final String[] args) {
        App app = new App();

        SandboxStack infra= new SandboxStack(app, "InfrastructureStac");
        InstanceStack ec2=new InstanceStack(app, "Ec2Stack",new SharedVpcPros(){
        
            @Override
            public Vpc getVpc() {
                return infra.getVpc();
            }
        });

        app.synth();
    }
}
