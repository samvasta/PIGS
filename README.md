# PIGS
_**P**rocedural **I**mage **G**eneration **S**andbox_ is a java
environment which provides tools to assist in the creation of
procedurally generated images.

## The IGenerator interface

## Run Modes
PIGS has a few different run modes:

1. Batch generation (`PIGS-batch`)
2. One-at-a-time generation (`PIGS-debug`)
3. Microservice

### PIGS-batch
`PIGS-batch` will automatically generate a number of images in a
batch operation. PIGS-batch uses a settings ini file to configure
how many images are generated, what kinds of images are generated,
and many more options.

##### Adding New Generators
`PIGS-batch` is built to be extensible. Users can make their own
image generators and easily share them with others using an image
generator plugin system. Simply drop your `.jar` into the
`GeneratorPlugins` folder and `PIGS-batch` will automatically
detect and use the new generators!

**WARNING**: The plugin system is inherently insecure. There are no
protections in place to prevent generators from doing suspicious
things like accessing your files so be cautious when trying
generators from unknown sources.


### PIGS-debug
`PIGS-debug` was created to make creating and debugging new
generators easier. This run mode opens a simple `JFrame` window
which generates and displays one image at a time.

### Microservice

The microservice is a small RESTful API which can generate images from HTTP requests.