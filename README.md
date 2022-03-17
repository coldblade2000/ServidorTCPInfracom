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

    java -cp . -jar out/artifacts/ServidorTCPInfracom_jar/ServidorTCPInfracom.jar

#### 2. Funcionamiento del servidor

Una vez ejecutado el servidor le pedira que ingrese el numero de clientes que se deben esperar para enviar el archivo. Ingreselo por consola.

Luego se le pedira escoger el archivo, 1 para 100MB y 2 para 250MB. Escoja el archivo que desee enviar e indiquelo por consola.
