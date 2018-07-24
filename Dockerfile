FROM ubuntu:18.04

MAINTAINER hirooka

# Package
RUN apt-get -y update
#RUN apt-get -y update && apt-get -y upgrade
#RUN apt-get -y dist-upgrade
RUN apt-get -y install build-essential git wget libasound2-dev autoconf libtool pcsc-tools pkg-config libpcsclite-dev unzip yasm nasm curl

# Web camera (audio)
RUN mkdir /etc/modprobe.d
RUN touch /etc/modprobe.d/alsa-base.conf

# ASUS B85M-E
RUN echo 'options snd slots=snd_usb_audio,snd_hda_intel,snd_hda_intel' >> /etc/modprobe.d/alsa-base.conf
RUN echo 'options snd_usb_audio index=0' >> /etc/modprobe.d/alsa-base.conf
RUN echo 'options snd_hda_intel index=1' >> /etc/modprobe.d/alsa-base.conf
RUN echo 'options snd_hda_intel index=2' >> /etc/modprobe.d/alsa-base.conf

# ASUS Z170I PRO GAMING
#RUN echo 'options snd slots=snd_usb_audio,snd_hda_intel' >> /etc/modprobe.d/alsa-base.conf
#RUN echo 'options snd_usb_audio index=0' >> /etc/modprobe.d/alsa-base.conf
#RUN echo 'options snd_hda_intel index=1' >> /etc/modprobe.d/alsa-base.conf

# Java
RUN apt-get -y install software-properties-common
RUN add-apt-repository -y ppa:linuxuprising/java
RUN apt-get update
RUN echo 'oracle-java10-installer shared/accepted-oracle-license-v1-1 select true' | debconf-set-selections
RUN apt-get -y install oracle-java10-installer
ENV JAVA_HOME /usr/lib/jvm/java-10-oracle

# x264
RUN cd /tmp && \
    wget https://download.videolan.org/pub/x264/snapshots/last_stable_x264.tar.bz2  && \
    tar xjvf last_stable_x264.tar.bz2 && \
    cd x264-* && \
    ./configure --enable-shared && \
    make && \
    make install

# FFmpeg
RUN cd /tmp && \
    wget https://ffmpeg.org/releases/ffmpeg-4.0.2.tar.bz2 && \
    tar jxvf ffmpeg-* && \
    cd ffmpeg-* && \
    ./configure --enable-gpl --enable-libx264 && \
    make -j$(nproc) && \
    make install

# nginx
RUN cd /tmp && \
    apt-get -y install libpcre3-dev libpcre++-dev libssl-dev zlibc zlib1g zlib1g-dev && \
    wget http://nginx.org/download/nginx-1.15.1.tar.gz && \
    tar zxvf nginx-*.tar.gz && \
    cd nginx-* && \
    ./configure --with-http_ssl_module --with-ipv6 --with-http_v2_module && \
    make && \
    make install
ADD ubuntu/nginx/nginx.conf /usr/local/nginx/conf/nginx.conf

# .sh for run both Spring Boot and nginx
ADD docker/startup.sh /startup.sh

RUN rm -rf /tmp/*

# chukasa
RUN mkdir -p /opt/chukasa/video
ADD ./build/libs/chukasa-0.0.1-SNAPSHOT.jar chukasa.jar

# locale
RUN locale-gen ja_JP.UTF-8
ENV LANG ja_JP.UTF-8
ENV LANGUAGE ja_JP:ja
ENV LC_ALL ja_JP.UTF-8

RUN ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# run only Spring Boot
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/chukasa.jar"]

# run both Spring Boot and nginx
EXPOSE 80
ENTRYPOINT ["/bin/bash", "/startup.sh"]
