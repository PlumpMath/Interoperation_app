Interoperation Application
----
Description:
----
Receive and display the information collected by heterogeneous devices through private gateways and manipulate the actuators (e.g. AllJoyn Smart Plug, IP Camera) connected to these gateways.

Reference Technique:  
----
-Android

-Socket programming

-Video Streaming

-Real-time Transport Protocol

-Protocol design

-P2P

-AllJoyn framework


Domain Application:
----
Internet of Things


Hint:
-----
-You can see the brief requirements in the [Evolution of InteroperationApp](./Documents/Evolution of InteroperationApp.pptx).It includes the evolution of requirements, design, and some problem issues. 

-The main problem & solution is in the [Improvement](./Documents/Improvement.pptx),it also includes the latest version of System Architecture of our application.

-All class diagrams in the [Design](./Design/) should be open by "software ideas modeler".(Download link is here: [Software Ideas Modeler](https://www.softwareideas.net/))

-There are some technial surveys and architecture designs in the "Documents".

Feature:
-----
I design a connection infrustructure with the following features:

-Reducing the resources usage of connection maintenance.

-Decoupling the responsibility of data received and data handled.

-Easy to extend data protocols and data handler.

-Reducing the responsibility of middleman between UI and data model.

	