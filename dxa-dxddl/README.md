# DXApp DXDdl example and template
This application shows how to use the Data-Description-Language (DXDdl) for DXRAM.
The master-slave application is described [here](https://github.com/hhu-bsinfo/dxapps/tree/development/dxa-masterslave). 
In this example the chained lists are realized using DXDdl including direct memory access.

## Compiling
The application requires the development version of DXRAM. To compile athe application, simply run the *build.sh* script
in the application directory:
```
./build.sh
```
The output jar-file will be located within *~/build/libs/dxa-masterslave.jar*.

## Deployment and running an application
You need at least four DXRAM instances running. The easiest way is an interactive setup using a shell or going for
[cdepl](https://github.com/hhu-bsinfo/cdepl). Below is the interactive approach, each starting in the DXRAM homedirectory
of DXRAM.
1) Starting the first instance (super peer and bootstrapping node)
```
cd build/dist/dxram
./bin/dxram --bootstrap
```

2) Starting the master (peer)
```
cd build/dist/dxram
./bin/dxram --start --msrole master
```

3) Starting a slave (peer)
```
cd build/dist/dxram
./bin/dxram --start --msrole slave
```

4) Repeat 3) for additional slaves. Two are recommended to have some distributed use case.

5) Submit the application (on any peer)
```
cd build/dist/dxram
./bin/dxram submit  your_path/build/libs/dxa-masterslave.jar
```

Remark: *your_path* is the path of the dxa-masterslave application.

Further documentation on how to develop applications for DXRAM can be
found in the docs of the [DXRAM repository](https://github.com/hhu-bsinfo/dxram/).

# License
Copyright (C) 2019 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science, Department Operating Systems.
Licensed under the [GNU General Public License](LICENSE.md).
