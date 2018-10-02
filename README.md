# DXApps: A collection of various examples, development and benchmarking DXRAM applications
[![Build Status](https://travis-ci.org/hhu-bsinfo/dxapps.svg?branch=master)](https://travis-ci.org/hhu-bsinfo/dxapps)
[![Build Status](https://travis-ci.org/hhu-bsinfo/dxapps.svg?branch=development)](https://travis-ci.org/hhu-bsinfo/dxapps)

The applications in this repository are part of the [DXRAM](https://github.com/hhu-bsinfo/dxram) key-value storage.

[ChunkBench](dxa-chunkbench/README.md): A benchmark similar to the Yahoo! Cloud Service Benchmark to test and determine
the performance of DXRAM's key-value storage
[HelloWorld](dxa-helloworld/README.md): Minimal example to get started with developing applications for DXRAM
[Migration](dxa-migration/README.md): Test and benchmark DXRAM's migration
[Terminal](dxa-terminal/README.md): Terminal server and client which provides a CLI to connect to DXRAM peers and run
 commands on them.

# Compiling
To compile all applications, simply run the *build.sh* script from the root of the repository:
```
./build.sh
```

The output jar-files are located in the sub-project directories in *build*.

# Deployment and running an application
Please refer to the documentation in the [DXRAM](https://github.com/hhu-bsinfo/dxram) repository (doc/Applications.md)
on how to deploy and run applications.

Further instructions (and examples) are given in the readme files in the sub-project directories.

## License
This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details
