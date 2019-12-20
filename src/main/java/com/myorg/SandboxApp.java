package com.myorg;

import software.amazon.awscdk.core.App;


public class SandboxApp {
    public static void main(final String[] args) {
        App app = new App();

        SandboxStack infra= new SandboxStack(app, "InfrastructureStac");
        Ec2Stack ec2=new Ec2Stack(app, "Ec2Stack",infra.getVpc());

        app.synth();
    }
}
