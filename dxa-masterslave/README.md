# DXApp Master-Slave example and template
This application provides a minimal Master-Slave example for a embarrassingly parallel distributed computation. This
means there are no data dependencies between computations. Each slave creates a chained list of integers, calculates
the sum of all list elements, and stores its local sum in the head of the list which is registered in the naming service.
The master prints out the results of each slave by accessing the heads registered in the naming service.

In DXRAM, the master only controls tasks and does not execute tasks itself. In this example, the master executes two
tasks: `InitTask` and `ComputeTask` - on all slaves in super steps. The latter is realized within the example code -
the master waits for all slaves to finish the `InitTask` before it starts the `ComputeTask`.

Within the `InitTask`, each slave creates a chained list using chunks (`NodeChunk` storing an integer) and registers
the head (`HeadChunk`) of the list in the naming service. Each slave uses its NodeID as the name for its `HeadChunk`
which is unique in order to avoid name collisions.

The master fires the `ComputeTask` after all slaves have finished the `InitTask`. As written above all slaves compute
the sum of all entries in their local list. The result is stored in the `HeadChunk` which was already registered in
the naming service by the `InitTask`.

After all slaves have finished the `ComputeTask`, the master fetches the results from each `HeadChunk` using the 
naming service.


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
