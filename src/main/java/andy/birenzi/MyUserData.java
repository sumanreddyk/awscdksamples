package andy.birenzi;

import software.amazon.awscdk.services.ec2.UserData;

public class MyUserData {
    private UserData userData;
    public UserData getUserData(){
        this.userData = UserData.forLinux();
        
        userData.addCommands("sudo amazon-linux-extras install epel");
        userData.addCommands("sudo yum update -y");
         userData.addCommands("sudo yum install nginx -y");
         userData.addCommands("sudo systemctl start nginx");
         userData.addCommands("sudo systemctl enable nginx");
        
        return userData;
    }
}