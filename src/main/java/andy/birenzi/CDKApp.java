package andy.birenzi;

import software.amazon.awscdk.core.App;


public class CDKApp {
    public static void main(final String[] args) {
        App app = new App();

        new VpcStack(app, "BirenziVpc");

        app.synth();
    }
}
