Event Source Arch Concept

Windows 10 x64 professional
JDK 1.11 Zulu
Eclipse 2019-06 (4.12.0)
SpringBoot 2.3.6.Release

Application Crealogix: 

(Simulation of Bank Transaction using Event Sourcing)

Crealogix: Base Project
Module Crealogix-Gateway: Server Part; Rest Endpoints; Sketch; DataBase Connections.
Module Crealogix-Infraestructure: Server Part; Interfaces.
Module Crealogix-Transactions-Data: Model Layer.
Module Crealogix-Transaction: Business Layer.


Instructions: 

1) mvn clean install.
2) Crealogix-Gateway/SpringBootApplicationCrealogixGateway.java  (Java Server)
3) Crealogix-Gateway/SketchMain.java (Test Java Client)
4) Crealogix/doc/EVENT_SOURCING.postman_collection.json (Postman export data)


