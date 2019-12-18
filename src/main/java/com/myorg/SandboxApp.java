package com.myorg;

import software.amazon.awscdk.core.App;


public class SandboxApp {
    public static void main(final String[] args) {
        App app = new App();

        new SandboxStack(app, "BirenziVpc");

        app.synth();
    }
}
