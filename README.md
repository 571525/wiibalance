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

##User Manual
#####Connect Wii Balance Board
The Wii Balance Board is necessary for the application to work. In order to connect the board follow the following instructions:
1. Push the “Connect” button top left 
2. Press the red button on the Wii Balance Board
3. The status bar should read “Connected”

Once connected there should be a live plot on the COP plot showing the current COP reading.
Otherwise if the display reads any of the following:
- Failed, try again -> 
    - You may have wrong bluetooth settings or drivers. Check www.bluecove.org for compatable software.
    - Pairing may have failed once -> try again.
    - Program may have stoped working -> quit program and restart
- Disconnected -> 
    - Board was connected but lost connection
    - Try to connect again
    - Try to change batteries in the board
    - Try to restart the program

NOTE: There is no need to disconnect the board when done.

NOTE: The connection process is necessary on every connect. The application keeps no track of previously connected boards.

#####Record a test
In order to use the application and record a test follow these steps:
1. Select a test duration
    - This is the amount of seconds a test will record data.
2. Select number of tests
    - This decides how many tests are run after one another. 
    - NOTE: each test will have the same duration defined in step 1.
3. Click “Start test”.

Once the test has started you should see the X/Y split screen and live recordings being plotted on the plots.
On the completion of a test the Result area should update and display the parameters from the test. There should also be a new button in the area Switch test. See following chapter how this is used. 
If more tests are to be run a countdown will be displayed with red numbers. This is the countdown to the start of the next test. 

#####Switch Test
If more than one test was recorded there should be multiple buttons in the Switch test area. The buttons are labelled “Test X”, where X is instead a number. The tests are ordered in increasing order, meaning the first test run will have the label “Test 1”. By clicking this the result area and all the plots will be that of test number 1. 

#####Switch views
The Switch View menu changes the plots being displayed in the applications main area. 

- COP: A COP (Centre-of-Pressure) plot showing the test plot and the current COP reading.
- X/Y: COP plot but split into X and Y axis for better overview over the movement on the axis.
- TP: Shows 4 plots related to the turning point:

    - Top Left: Timeseries (Xi), is the timeseries generated from the COP plot
    - Bottom Left: MSD (Mean Squared Displacement) of the timeseries
    - Top Right: Slope, a double logarithmic plot of the MSD.
    - Bottom Right: TP (Turning Point), the turning point plot.

#####Export Data
There are 2 ways to export data, through the two buttons in the result area labelled “Save Current” and “Save All” and through the “File” menu option located top left.
You can save (export) the data currently being displayed (Save Current - button) or save the data from ALL tests run in that batch into the same file (Save All - button).

NOTE: If you are in doubt to which tests will be saved, it is the exact same tests that are located in the Switch test area. 

How to export data:
1. Provide a person ID. This can be anything.
2. Provide a test type. This can be anything as well, but should include information of how the test was condoned. 
    - NOTE: A predefined test type, Romberg, will automatically set the duration to 20 seconds and the amount of tests to 4. This can be changed manually after Romberg has been selected.
3. Export All test (Save All) or export the currently displayed test (Save Current)
4. A file-selector window should appear
5. Find location and click “Open”
	- NOTE: On other operating systems the button to click may differ from “Open” on Linux.
6. The data should be located where you selected as a CSV file.

NOTE: The file is preset to be labelled personId_testType_DDMMYY_TTTT_ CEST.csv with the predefined person ID and test type.


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
