#!/bin/sh

WORKDIR=$(pwd)

apt-get update
apt-get -y upgrade

apt-get -y install git cmake

# JDK 1.8

apt-get -y install openjdk-8-jdk
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf/

# Maven

apt-get -y install maven

# BlueZ 5.47

apt-get -y install libglib2.0-dev libdbus-1-dev libudev-dev libical-dev libreadline-dev

cd /tmp

wget http://www.kernel.org/pub/linux/bluetooth/bluez-5.47.tar.xz
tar -xf bluez-5.47.tar.xz
cd bluez-5.47
./configure --prefix=/usr --mandir=/usr/share/man --sysconfdir=/etc --localstatedir=/var
make
make install

cd $WORKDIR

cp deployment/bluetooth.conf /etc/dbus-1/system.d/bluetooth.conf

systemctl daemon-reload
systemctl restart bluetooth

# tinyb

apt-get -y install graphviz doxygen

cd /tmp

git clone https://github.com/intel-iot-devkit/tinyb.git
cd tinyb
mkdir build
cd build
cmake -DBUILDJAVA=ON -DCMAKE_INSTALL_PREFIX=/usr ..
make
make install


