This is the folder for the code that runs on the Raspberry Pi.

As a starting point the material provided in the `skeleton-bleclient`
project from Thomas Tschol was used, which can be found
[here](https://git.uibk.ac.at/csat2410/skeleton-bleclient).

In order to use the program, we assume that a Raspberry Pi has
already been set up as described in the top-level README file of
the `skeleton-bleclient` project, in particular, that JDK 8, Maven,
BlueZ 5.47 and tinyb have been installed.

Following this, just clone this repo on the Raspberry Pi, `cd` into the
`raspberry` folder (which is the folder where this file is located)
and enter one of the commands shown below to build the program.

**Build:**

     mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

**Build with Tests and Javadoc:**

     mvn clean install

If a Timeflip device is within reach, you can run the program with the
following command.

**Run:**

     sudo java -cp target/raspberry.jar:./lib/tinyb.jar:./target/dependencies/* at.timeguess.raspberry.Main 
