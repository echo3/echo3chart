About
=====
The Echo3 Chart Library is a collection of Echo3 components to facilitate the display of charts in remote applications. 

At the present time this library should be considered API-unstable, i.e., breaking changes may be made to subsequent versions of the API.

Building
========
In order to build the test web application, you must have the echo3 source code checked out and built, and you must set up the location of that checkout as an environment variable.  Look at the included ant.properties to understand what you need to do.

To check out echo3, follow the instructions at http://echo.nextapp.com/site/echo3/download

## Instructions
1. Once you have built echo 3 framework (git clone, then ant dist)
2. Edit the echo3chart ant.properties file to reference your java version, release.version and echo.version)
3. ant dist.testapp
4. load the TestChart.war into your java servlet container (tomcat for example using manager)
5. run the testapp by using a browser and hitting this url: http://localhost:8080/ChartTest/app
