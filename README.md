## Laboratorio 3 - Infraestructura de comunicaciones

#### 1. Instrucciones para correr el servidor en Ubuntu Server

Despues de haber ingresado a la MV, actualizela usando:

    sudo apt-get update

Luego instale la versión de Java mas apropiada, para el proyecto se uso JDK 17:

    sudo apt-get install openjdk-17-jdk-headless

Clone el repositorio de GitHub (Como los archivos de transferencia son pesados estos no se incluyen en el repositorio):

    git clone https://github.com/coldblade2000/ServidorTCPInfracom.git

Recree los archivos de tamaño especifico:

    mkdir Archivos

    truncate -s 250M /ServidorTCPInfracom/ServidorTCPInfracom/Archivos/grande.txt

    truncate -s 100M /ServidorTCPInfracom/ServidorTCPInfracom/Archivos/pequenio.txt

Ejecución:

    cd ServidorTCPInfracom/ServidorTCPInfracom/

    javac -cp . src/com/company/*.java -sourcepath src/com/company/*.java -d out/production/ServidorTCPInfracom

    java
