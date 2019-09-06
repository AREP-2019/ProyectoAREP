# Description

This project was implemented through HTTP so it can server images, and html resources inside the main resources folder, also i provides a framework to run classes (POJOS) inside com.arep.apps.

#Architecture
The architecture is pretty simple there is a single class that Serves when a HTTP get Request is made the resources from the resources folder, and uses reflexion to access the classes inside the package com.arep.apps and invoke their methods always returns 200 and shows NOT FOUND if not exists or some mismatch.
# Test

### Image:

[Link](https://proyecto-arep.herokuapp.com/imagenes/class.png)

[Link](https://proyecto-arep.herokuapp.com/imagenes/playa.png)

### Html:
[Link](https://proyecto-arep.herokuapp.com/resultado.html)

### Clase:

Example1:[Link](https://proyecto-arep.herokuapp.com/apps/Playa/write/this_is_a_test)

Expample2:[Link](https://proyecto-arep.herokuapp.com/apps/Playa/sum/4451&7541)

Example3:[Link](https://proyecto-arep.herokuapp.com/apps/Playa/playa)

# Deployment

Application was deployed in heroku [Link](https://proyecto-arep.herokuapp.com/)

# Built-in with

* [Maven](https://maven.apache.org/) - Dependency Management

# Author

* Oscar Ricardo Pinto Benavides 
