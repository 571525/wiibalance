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

##DISCLAIMER
The Software and code samples available on this website are provided "as is" without warranty of any kind, either express or implied. Use at your own risk.
The use of the software and scripts downloaded on this site is done at your own discretion and risk and with agreement that you will be solely responsible for any damage to your computer system or loss of data that results from such activities. You are solely responsible for adequate protection and backup of the data and equipment used in connection with any of the software, and we will not be liable for any damages that you may suffer in connection with using, modifying or distributing any of this software. No advice or information, whether oral or written, obtained by you from us or from this website shall create any warranty for the software.

We make makes no warranty that:
- the software will meet your requirements
- the software will be uninterrupted, timely, secure or error-free
- the results that may be obtained from the use of the software will be effective, accurate or reliable
- the quality of the software will meet your expectations any errors in the software obtained from us will be corrected.

The software, code sample and their documentation made available on this website:
- could include technical or other mistakes, inaccuracies or typographical errors. We may make changes to the software or documentation made available on its web site at any time without prior-notice.
- may be out of date, and we make no commitment to update such materials.

We assume no responsibility for errors or omissions in the software or documentation available from its web site.
In no event shall we be liable to you or any third parties for any special, punitive, incidental, indirect or consequential damages of any kind, or any damages whatsoever, including, without limitation, those resulting from loss of use, data or profits, and on any theory of liability, arising out of or in connection with the use of this software.
