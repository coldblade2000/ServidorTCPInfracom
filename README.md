## Laboratorio 3 - Infraestructura de comunicaciones

#### 1. Instrucciones para correr el servidor en Ubuntu Server

Despues de haber ingresado a la MV, actualizela usando:

    sudo apt-get update

Luego instale la versión de Java mas apropiada, para el proyecto se uso JDK 17:

    sudo apt-get install openjdk-17-jdk-headless

Clone el repositorio de GitHub (Como los archivos de transferencia son pesados estos no se incluyen en el repositorio):

    git clone https://github.com/coldblade2000/ServidorTCPInfracom.git

Recree los archivos de tamaño especifico:

    cd ServidorTCPInfracom/ServidorTCPInfracom

    mkdir Archivos && truncate -s 250M /Archivos/grande.txt && truncate -s 100M /Archivos/pequenio.txt

Ejecución:

    cd ServidorTCPInfracom/ServidorTCPInfracom/ && java -cp . -jar out/artifacts/ServidorTCPInfracom_jar/ServidorTCPInfracom.jar

#### 2. Funcionamiento del servidor

Una vez ejecutado el servidor le pedira que ingrese el numero de clientes que se deben esperar para enviar el archivo. Ingreselo por consola.

Luego se le pedira escoger el archivo, 1 para 100MB y 2 para 250MB. Escoja el archivo que desee enviar e indiquelo por consola.

Solo cuando el servidor le indique que ya se inicializo, ejecute el cliente.

#### 3. Instalacion del cliente.

Similar al paso 1, clone el repositorio en su maquina Windows, procure tener Intellij IDEA instalado.

Abra el proyecto en Intellij y ejecute el archivo main.java del cliente.

#### 4. Funcionamiento del cliente

Cuando el cliente le pida el numero de clientes, ingrese por consola el mismo numero que le ingreso al servidor en cuanto a numero de clientes. El cliente procedera a realizar las conexiones y descargar los archivos.
