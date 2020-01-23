package andy.birenzi.model;

import software.amazon.awscdk.services.ec2.UserData;

public class MyUserData {
    private UserData userData;

    public UserData getUserData(){
        String serviceName="duo.service";
        this.userData = UserData.forLinux();
        String service =String.join("\n"
               ,"touch "+serviceName
                ,"/bin/echo '[Unit]' >> "+serviceName
                ,"/bin/echo 'Description=DuoDeviceManagementPortal' >> "+serviceName 
                ,"/bin/echo 'After=syslog.target' >> "+serviceName
                ,"/bin/echo '[Service]' >> "+serviceName
                ,"/bin/echo 'Environment=\"JASYPT_ENCRYPTOR_PASSWORD=XXXXXXXXXXX\"' >> "+serviceName
                ,"/bin/echo 'Environment=\"LOG_PATH=/var/log/duo/myduodevices\"' >> "+serviceName
                ,"/bin/echo 'Environment=\"JAVA_OPTS=-Xmx2048M\"' >> "+serviceName
                ,"/bin/echo 'Environment=\"SPRING_PROFILES_ACTIVE=local\"' >> "+serviceName
                ,"/bin/echo 'User=root' >> "+serviceName
                ,"/bin/echo 'ExecStart=/opt/duodevicemanagement/myduodevices.jar' >> "+serviceName
                ,"/bin/echo 'SuccessExitStatus=143' >> "+serviceName
                ,"/bin/echo '[Install]' >> "+serviceName
                ,"/bin/echo 'WantedBy=multi-user.target' >> "+serviceName

        );
         userData.addCommands("sudo amazon-linux-extras install epel");
         userData.addCommands("sudo yum update -y");
         userData.addCommands("sudo yum install java-1.8.0-openjdk -y");
         userData.addCommands("sudo su");
         userData.addCommands("cd /etc/systemd/system/");
         userData.addCommands(service);
         userData.addCommands("mkdir -p /var/log/duo/myduodevices");
         userData.addCommands("mkdir -p /opt/duodevicemanagement/");
         userData.addCommands("systemctl daemon-reload");
         userData.addCommands("systemctl start duo");
         userData.addCommands("sudo systemctl enable duo");

         
        
        return userData;
    }
}