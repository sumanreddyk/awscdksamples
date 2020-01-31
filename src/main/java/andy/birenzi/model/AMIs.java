package andy.birenzi.model;

import software.amazon.awscdk.services.ec2.AmazonLinuxEdition;
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.AmazonLinuxStorage;
import software.amazon.awscdk.services.ec2.AmazonLinuxVirt;

public class AMIs {
    public void AMIS() {

    }

    private AmazonLinuxImage applicationImage;

    public AmazonLinuxImage getApplicationImage() {
        this.applicationImage = AmazonLinuxImage.Builder.create().generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
                .edition(AmazonLinuxEdition.STANDARD).virtualization(AmazonLinuxVirt.HVM)
                .storage(AmazonLinuxStorage.GENERAL_PURPOSE).build();
        return applicationImage;
    }
}