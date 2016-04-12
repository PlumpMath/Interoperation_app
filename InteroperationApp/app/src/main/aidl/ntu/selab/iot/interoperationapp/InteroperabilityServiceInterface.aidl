// InteroperabilityServiceInterface.aidl
package ntu.selab.iot.interoperationapp;

// Declare any non-default types here with import statements

interface InteroperabilityServiceInterface {
    /**
        * Demonstrates some basic types that you can use as parameters
        * and return values in AIDL.
        */
       void cancelSensorDataUpdateTask();
       void cancelDiscoveryTask();
       void close();
       void discover();
       String getSpecificSensorData(String gatewayIPAddress,String uuid, String type);
       void setSpecificSensorData(String gatewayIPAddress,String uuid);
       void startVideo(String ip, String uuid);
}
