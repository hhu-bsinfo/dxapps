# DXApp DXDdl example and template
This application shows how to use the Data-Description-Language (DXDdl) for DXRAM and is based on the master-slave
example wich is described [here](https://github.com/hhu-bsinfo/dxapps/tree/development/dxa-masterslave). 
In this example the chained lists are realized using DXDdl including direct memory access.

## Compiling
The application requires DXRAM and DXDdl. 

The description below assumes following directory structure.
```
 ~/dxram
 ~/dxddl
 ~/dxapps
 ```

1. Get and compile [DXRAM](https://github.com/hhu-bsinfo/dxram/) (development version)

2. Get [DXApps](https://github.com/hhu-bsinfo/dxapps)

3. Get and compile [DXDdl](https://github.com/hhu-bsinfo/dxddl/) (development version)

4. Go to the DXDdk directory and generate the `dxddl-api.jar` for the `Node.dxddl` file enclosed in this example.
   Copy the resulting output file `dxddl-api.jar` to the `libs` directory of this example.
   ```
   cp ~/dxapps/dxa-dxddl/Node.dxddl cd ~/dxddl/input
   cd ~/dxddl
   ./compile.sh input/Node.dxddl
   cp ~/dxapps/dxa-dxddl/output/dxddl-api.jar ~/dxapps/libs
   ```

4. Compile this application.
    ```
    ./build.sh
    ```
    The output jar-file will be located within `~/build/libs/dxa-dxddl.jar`.

## Deployment and running an application
How to start DXRAM and submit the `~/build/libs/dxa-dxddl.jar` can be found in the master-slave example, [here](https://github.com/hhu-bsinfo/dxapps/tree/development/dxa-masterslave). 

Further documentation on how to develop applications for DXRAM can be
found in the docs of the [DXRAM repository](https://github.com/hhu-bsinfo/dxram/).

# License
Copyright (C) 2019 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science, Department Operating Systems.
Licensed under the [GNU General Public License](LICENSE.md).
