# WiiBalance

This project is based on research done by Haukeland University Hospital and their department for vestibular diseases. An article named ["The Turning Point for Maintaining Balance"](https://www.scitechnol.com/peer-review/the-turning-point-for-maintaining-balance-iBaH.php?article_id=9566) resultet in the need of an application to do further research on this. 


This project uses the Wii Balance Board TM as hardware to measure balance and connects over bluetooth. The bluetooth library used is [BlueCove v. 2.1.0](bluecove.org) which has its limits on newer OS. Wiibalance has been developed on Linux Ubuntu 18.04 and found to work well on the OS. In theory it should work on older OS as well (Windows XP/Vista) and on Mac OS, but keep in mind that the bluetooth driver needs to be in compliance with BlueCove 2.1.0 requirements.

##Basic requirements
The application is developed on JDK 8 and will require a compatible runtime environment. All Java versions 8+ should work fine, but the application has only been tested on JDK8 and JDK11. Bluetooth is also required and may need special setup as it doesn't automatically work with default drivers. See [BlueCove](bluecove.org) for specific requirements.

## Linux
Install libbluetooth-dev and BlueZ
```
sudo apt-get install libbluetooth-dev bluez
```
Also remember to make the jar executable
```
sudo chmod +x path/to/jar
```

This project was developed as a bachelor project for the Western University of Applied Science in Bergen, Norway.