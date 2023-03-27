# Distributed-Shared-White-Board

## Problem context description
**A brief Introduction:**

This project offers us an opportunity to build a shared whiteboard that allows multiple users to draw simultaneously on a canvas. The whiteboard provides a range of
functionalities for the users such as drawing lines, drawing shapes (circles triangles and sqaures), inserting text, change colors, a chat box for communicating with other users and so on. Also, in the problem context there are two different characters: Guest and Manager.

**Manager:**
The manager takes in charge of white board creation/shut down, file management and guests list management (including join permission and guest removal).

**Guest:**
Guest can only join the whiteboard that created by the manager, and will not have the ability to save, create, close the whiteboard.

Fortunately, there are multiple similar examples found on the Internet such as Zoom drawing tools, draw.io for us better understanding each requirements of the project. The communication functionality is similar to the previous project (Multi-threaded dictionary server), and also plenty of graphic user interfaces building experiences gained from the previous project. Therefore, those huge helped implementing the distributed shared whiteboard for this project.


**1.** **Interaction**

The communication protocol used in this project is TCP, TCP provides a reliable transmission environment which means it will ensure the data will be transferred during the network connection. In the problem, the chat box communication will be reliable and no message will be lost during transmission. And also for all the drawings by different users.

**2.** **Data format - JSON**

The data interchange format used is JSON, it is a very common data format use frequently during web applications with servers. And it is also an easy-to-learn and human understandable format. The request and response between the Manager and Guest will be in JSON format. All request and response will include a type and a body, and this format of data will be easy to achieved by using JSON. Such as a simple draw line request will be: {[“reqType: Draw”], [“reqBody”: “Line”], [“Properties” : “startPoint”, “endPoint”, “color”]. This can’t not be easy achieved by simple string text. There is another use of JSON, which is saving format and open file format. The format of saved files and openned files are in JSON format, it is easy to load and save for the manager in the local file system.

**3.** **Failure Model**

All the data will be safely transferred during the TCP connection, the reliability is guaranteed. All the exceptions occurred in the application has been carefully processed and dealt with, there is no glitchs or bugs while using the application.

**4.**

**Functional requirements**

**Manager side features:**
 
**4.1.** **Permission for join:** Reject or Accept a join request from the guests.

**4.2.** **Ability to kick guest out:** Kick a selected guest out of the whiteboard.

**4.3.** **Save/Open/Clear/Create File:** Save current frame, crate a new frame, open a existing frame.

**Common shared features:**

**4.4.** **Draw:** Draw simultaneously on the canvas.

**4.5.** **Chat:** Ability to send message to all online users.


**5.** **Graphic User Interface**

**5.1.** **Login GUI**

The implementation contains two Login GUI to each side of guest and manager. Manager can use the login window to open a frame for guest to join in an guest can join the existing whiteboard crated by the managers.

**5.2.** **Whiteboard GUI**

The whiteboard GUI offers a canvas for the guest and manager. Also with a drawing tool bar on the top of the window, a peer list on right and a chat window below the canvas. For manager it will offer a menu bar for file management.

## Components of the system

**1. ConnectionLanucher**

It is a common class shared between Guest and Manager, for the connection instantiation, output/input stream creation. For the Manager it will conain a list of connections of guests a list of guest names that currently exists. It will help the manager broadcast all messages concurrently and whiteboard synchornization.

**2. ManagerSideConnection**

The manager side connection will be corresponds to a guest side connection, once the connection has been established by the launcher, it will read all request send from this guest connection such as “Connect”, “Chat”, “Sync” and “Paint". Then the Manager will react by the request message, such as accept/reject the connection request or synchorinized the peer list with the guest, broadcast the newest changes to all the guests under the canvas.

**3. GuestSideConnection**

The Guest side connection acts as the same functionality as the manager side connection. It will be paired with the corresponding Manager connection, read and write response and request from/to the manager. However, it won’t need to board cast the changes made by itself. As the manager will perform the synchorization, once anything changes on the canvas, peerlist updates or chat message transmission.

**4. ConnectionManager**

The class connection manager served as an Observer in the Observer design pattern, it offers the mechanism to notify multiple objects about any events that happened to the object they’re observing. All the guest are Subscribers and the manager serves as the Publisher. Requests like drawing, chatting, quitting, joining will be received on the Manager side and the manager (Publisher) will broadcast all updates to the Guests(Subcribers) and hence achieve concurrency.

**5. CanvasTech**

CanvasTech class is an interface which implements several Listeners for observing the actions happened on the canvas, the listeners include MouseMotionListener, MouseListener, ActionListener. This class will record all mouse actions and transfer the record to the Canavs to performing all drawings, also includes text insertion and color change.

**6. Canvas**

The Canvas class will extends the JPanel, which is a framework component of the gui. It contains a list of all drawing contents currently on the canvas, and the repaint/update ability to performe content change. All user drawing actions will be transferred as JSON format into the canvas class, and accroding to the drawing actions, the canvas will performe the corresponding drawing.

**7. FileManager**

The FileManager class will offer the save and open file functionality to the Manager. The file format will be stored in the JSON format.

**8. LoginGUI**

The loginGUI of the Manager will open the port to receive all connection request from the guests, and perform rejection/accept to all the request. On the guest side of the loginGUI, when it wants to a specific manager’ whiteboard, it will send a request to the corresponding manager and waiting for the response in 5 seconds. If it get rejected or duplicate name imputed, the guest will not able to join the whiteboard.


Class diagram and interaction diagram

**Class Diagram of the Distributed Whiteboard**

![image](https://user-images.githubusercontent.com/69286762/227930423-aa6a385e-b49c-4550-85fe-2357bb37a6cd.png)


**Sequence Diagram of the Distributed Whiteboard**

![image](https://user-images.githubusercontent.com/69286762/227930467-0d9d294a-8357-48dc-bb86-c161e84fd659.png)

![image](https://user-images.githubusercontent.com/69286762/227930523-06d8d033-4d3a-4549-b497-4b40e64e74ce.png)

![image](https://user-images.githubusercontent.com/69286762/227930666-4c21f069-de74-48ca-a011-1683125b1764.png)


## Critical analysis

**1. Architecture**

The architecture used in the project is thread per connection, each time the manager receives one connection from the guest it will create a thread for processing the request and response. It is straightforward to implement in the program and it is suited for the multi-threaded connection. The manager will acts as a centroid in charge of all responsibilities such as file, peerlist management. However, it is quite expensive and will also impact the scalability of the server as the number of connections may grow significantly above the ideal amount of thread of the server. Also, if the manager is down, all the connections will lost, therefore, the architecture may not be suitable under unsecure network.

**2. Concurrency**

As we are performing a multithreaded server, therefore leads to a question: How to perform operations concurrently? One of the properties of thread is they are running in a random order. So, when it comes to multiple clients the server may send the response to the wrong clients(through thread of connection). Using synchronization is necessary, the strategies is to put “synchronized” in the method declaration, hence ensuring the order is kept.

## Conclusion

The distributed shared white board project improves my understanding of how distributed network transferring data under TCP connection also the concurrency management under multiple connections. Some design patterns like Observer are powerful for dealing with multiple users synchornization and message transmission.
