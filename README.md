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


Appliance:
----
Internet of Things

Role:
----
-Project Manager

-Architect

-Developer


Work:
-----
-Survey the interoperability issue in Internet of Things

-Extract the requirements of interoperability issue in Internet of Things.

-Design the System Architecture of Interoperation Application.(see in "Documents/Improvement" p5)

-Design the Class Diagram of Interoperation Application.(see in "Design")

-Implementation of Interoperation Application.(see in "InteroperationApp")



Hint:
-----
-You can see the brief requirements in the "Documents/Evolution of InteroperationApp".It includes the evolution of requirements, design, and some problem issues. 

-The main problem & solution is in the "Documents/Improvement",it also includes the latest version of System Architecture of our application.

-All class diagrams in the "Design" document should be open by "software ideas modeler".(Download link is here: [Google](https://www.softwareideas.net/)

-There are some technial surveys and architecture designs in the "Documents".

Special:
-----
I design a connection infrustructure which can:

-Reduce the resource usage of connection maintenance.

-Decouple the responsibility of data receiving and data handling.

-Easy to future extend for data protocal or data handler.

-Reduce the responsibility of middleman between UI and data model.

	