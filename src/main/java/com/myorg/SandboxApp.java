package com.myorg;

import software.amazon.awscdk.core.App;

import java.util.Arrays;

public class SandboxApp {
    public static void main(final String[] args) {
        App app = new App();

        new SandboxStack(app, "SandboxStack");

        app.synth();
    }
}
