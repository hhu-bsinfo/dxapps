# DXApp Master-Slave example and template
This application provides a minimal Master-Slave example. The master executes two tasks ((InitTask and ComputeTask) on
all slaves in super steps. The ComputeTask is started after all slaves have finished the InitTask. After all slaves
have finished the ComputeTask, the master retrieves and prints the results of the slaves by retrieving the HeadChunk
(storing results computed on a slave) registered by each slave in the naming service. Each slave uses its NodeID as
a name for its HeadChunk which is unique thus avoiding naming collisions. In the InitTask, the slaves create a very
basic chained list using chunks (NodeChunk) and register the head (HeadChunk) of the list in the naming service.
In the ComputeTask, the slaves sum up the values of all entries in their local list and store the result in their
HeadChunk. The master does not execute tasks but only controls them.

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
