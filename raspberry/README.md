This is the folder for the code that runs on the Raspberry Pi.

As a starting point the material provided in the `skeleton-bleclient`
project from Thomas Tschol was used, which can be found
[here](https://git.uibk.ac.at/csat2410/skeleton-bleclient).

In order to use the program, we assume that a Raspberry Pi has
already been set up as described in the top-level README file of
the `skeleton-bleclient` project, in particular, that JDK 8, Maven,
BlueZ 5.47 and tinyb have been installed.

Following this, just clone this repo on the Raspberry Pi, `cd` into the
`raspberry` folder and build with either

    mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

(*without* tests and Javadoc) or

    mvn clean install

(*with* tests and Javadoc).

If a Timeflip device is within reach, you can run the program with the
following command.

    java -cp target/raspberry.jar:./lib/tinyb.jar:./target/dependencies/* at.timeguess.raspberry.Main 98:07:2D:EE:23:28

Useful ressources:
- [TimeFlip protocol](https://github.com/DI-GROUP/TimeFlip.Docs/blob/master/Hardware/BLE_device_commutication_protocol_v3.0_en.md)
- [TinyB Java documentation](http://iotdk.intel.com/docs/master/tinyb/java/index.html)
- [TinyB Java examples](https://github.com/intel-iot-devkit/tinyb/tree/master/examples/java)
