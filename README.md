# DXApps: A collection of various examples, development and benchmarking DXRAM applications
[![Build Status](https://travis-ci.org/hhu-bsinfo/dxapps.svg?branch=master)](https://travis-ci.org/hhu-bsinfo/dxapps)
[![Build Status](https://travis-ci.org/hhu-bsinfo/dxapps.svg?branch=development)](https://travis-ci.org/hhu-bsinfo/dxapps)

The applications in this repository are part of the [DXRAM](https://github.com/hhu-bsinfo/dxram) key-value storage.

* [HelloWorld](dxa-helloworld/README.md): Minimal example to get started with developing applications for DXRAM
* [MasterSlave](dxa-masterslave/README.md): Basic master-slave example 
* [DXDdl](dxa-dxddl/README.md): Basic example of direct chunk access using DXDdl.
* [ChunkBench](dxa-chunkbench/README.md): A benchmark similar to the Yahoo! Cloud Service Benchmark to test and
* [Migration](dxa-migration/README.md): Test and benchmark DXRAM's migration
* [Terminal](dxa-terminal/README.md): Terminal server and client which provides a CLI to connect to DXRAM peers and run
 commands on them.

# Compiling
To compile an application, simply run the *build.sh* script from the root of the repository specifying the application's module:
```
./gradlew dxa-helloworld:build
```

The output jar-files will be located within *~/dxram/dxapp*.

# Deployment and running an application
Please refer to the documentation in the [DXRAM](https://github.com/hhu-bsinfo/dxram) repository (doc/Applications.md)
on how to deploy and run applications.

Further instructions (and examples) are given in the readme files in the sub-project directories.

## License
This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details
