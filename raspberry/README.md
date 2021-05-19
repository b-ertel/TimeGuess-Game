This is the folder for the code that runs on the Raspberry Pi.

# Acknowledgments

As a starting point the material provided in the `skeleton-bleclient`
project from Thomas Tschol was used, which can be found
[here](https://git.uibk.ac.at/csat2410/skeleton-bleclient). His effort
is highly appreciated.

# Prerequisites

In order to use the program, we assume that you have just flashed a recent flavour of
Raspberry Pi OS on your SD card, following the instructions
[here](https://www.raspberrypi.org/documentation/installation/installing-images/).

We have successfully tested the deployment process with **Raspberry Pi OS Lite**, which
is the smallest OS image, but it is expected that **Raspberry Pi OS with desktop** or
**Raspberry Pi OS with desktop and recommended software** will also work, and
if you feel more comfortable setting up your Raspberry Pi using a graphical user
interface, you should probably choose on of those anyway.

That said, in order to proceed you actually only have to make sure that
- your Raspberry Pi is up and running,
- you have access to a terminal on your Raspberry Pi and
- your Raspberry Pi has access to the internet.

## Suggested workflow for "headless" mode

If you are running your Raspberry Pi without a screen (i.e., in "headless" mode), the
easiest way to achieve the above is probably as follows:
1. Place an empty file named `ssh` onto the boot partition of your SD card.
2. Insert the SD card into your Raspberry Pi's SD card slot.
3. Connect your Raspberry Pi to your router using an Ethernet cable.
4. Power up your Raspberry Pi.
5. Find out your Raspberry Pi's IP address from your router's device list.
6. Connect to your Raspberry Pi using SSH with the username `pi` and the password `raspberry`.

## Optional steps

If you are concerned about security, you are encouraged to change the password for
the user `pi` with `passwd`.

You might also want to update your system now

    sudo apt update
    sudo apt upgrade

and install some additional packages (e.g., `vim` and `git`).

The command for installing a package is

    sudo apt install <package>

where `<package>` has to be replaced with the package's name.

# Get the code

The next step is to download this repository to your Raspberry Pi. There are
several ways to do so and you are free to choose a method that best suits your needs.
If you use `git`, you have to install and configure it first. Although not strictly
required, it is recommended that you put the files in your home directory or
any subdirectory thereof.

# A brief note on shell scripts

You will use shell scripts for some of the following tasks.

Note that you can run a script by making it executable with the command
`chmod u+x <script>` and then calling `./<script>`, where `<script>` has
to be replaced with the script's name, optionally prepending `sudo`, if the script needs
to be run as root.

# Install dependencies

Before you can do anything else, you have to install some dependencies.
In order to do this, just run the script `install-dependencies.sh` in the `raspberry`
subfolder of the project. Note that this script has to be run as root.

After the script has completed, reboot your system.

# Build

Next run the script `build.sh` in the `raspberry` subfolder of the project
in order to build.

# Configuration file

Before you can actually use the program, one final step is required.

Create a file named `config.txt` in the `raspberry` subfolder of the project
and add the lines

    TIMEFLIP_MAC_ADDRESS=<timeflip_mac_address>
    BACKEND_URL=<backend_url>

where `<timeflip_mac_address>` has to be replaced with the MAC address of your
TimeFlip device and `<backend_url>` has to be replaced with the URL of your backend
(starting with `http://`).

## How to get the MAC address of your TimeFlip device

If you don't know the MAC address of your TimeFlip device, you can use the
command `sudo hcitool lescan`.

# Run

Finally, if your Timeflip device is within reach and your backend is
running, you can run the script `run.sh` in the `raspberry` subfolder of the
project. Note that this script has to be run as root.

